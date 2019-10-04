/*
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.microprofile.starter.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.eclipse.microprofile.starter.TestMatrixTest.API_URL;
import static org.eclipse.microprofile.starter.TestMatrixTest.TMP;
import static org.eclipse.microprofile.starter.utils.Logs.S;
import static org.junit.Assert.assertEquals;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class Commands {
    private static final Logger LOGGER = Logger.getLogger(Commands.class.getName());

    private static final String STARTER_TS_WORKSPACE = "STARTER_TS_WORKSPACE";

    public static String getWorkspaceDir() {
        String env = System.getenv().get(STARTER_TS_WORKSPACE);
        String sys = System.getProperty(STARTER_TS_WORKSPACE);
        String fallback = System.getProperty("java.io.tmpdir");
        if (StringUtils.isNotBlank(env)) {
            return env;
        }
        if (StringUtils.isNotBlank(sys)) {
            return sys;
        }
        return fallback;
    }

    public static void download(Client client, String supportedServer, String artifactId, SpecSelection specSelection, String location) {
        Response response = client.target(API_URL + "/project?supportedServer=" + supportedServer + specSelection.queryParam + "&artifactId=" + artifactId).request().get();
        assertEquals("Download failed.", Response.Status.OK.getStatusCode(), response.getStatus());
        try (FileOutputStream out = new FileOutputStream(location); InputStream in = (InputStream) response.getEntity()) {
            in.transferTo(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File unzip(String location, String artifactId) throws InterruptedException, IOException {
        ProcessBuilder pb;
        if (isThisWindows()) {
            pb = new ProcessBuilder("powershell", "-c", "Expand-Archive", "-Path", location, "-DestinationPath", TMP, "-Force");
        } else {
            pb = new ProcessBuilder("unzip", "-o", location, "-d", TMP);
        }
        Map<String, String> env = pb.environment();
        env.put("PATH", System.getenv("PATH"));
        pb.directory(new File(TMP));
        File unzipLog = new File(TMP + S + artifactId + "-unzip.log");
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.to(unzipLog));
        Process p = pb.start();
        // On slow cloud VMs with weird I/O, this could be minutes for some reason...
        p.waitFor(3, TimeUnit.MINUTES);
        return unzipLog;
    }

    public static void cleanWorkspace(String artifactId) {
        String path = TMP + S + artifactId;
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            // Silence is golden
        }
        (new File(path + ".zip")).deleteOnExit();
        (new File(path + "-unzip.log")).deleteOnExit();
        (new File(path + ".zip")).delete();
        (new File(path + "-unzip.log")).delete();
    }

    public static boolean waitForTcpClosed(String host, int port, long loopTimeoutS) throws InterruptedException, UnknownHostException {
        InetAddress address = InetAddress.getByName(host);
        long now = System.currentTimeMillis();
        long startTime = now;
        InetSocketAddress socketAddr = new InetSocketAddress(address, port);
        while (now - startTime < 1000 * loopTimeoutS) {
            try (Socket socket = new Socket()) {
                // If it let's you write something there, it is still ready.
                socket.connect(socketAddr, 1000);
                socket.setSendBufferSize(1);
                socket.getOutputStream().write(1);
                socket.shutdownInput();
                socket.shutdownOutput();
                LOGGER.info("Socket still available: " + host + ":" + port);
            } catch (IOException e) {
                // Exception thrown - socket is likely closed.
                return true;
            }
            Thread.sleep(1000);
            now = System.currentTimeMillis();
        }
        return false;
    }

    public static int parsePort(String url) {
        return Integer.parseInt(url.split(":")[2].split("/")[0]);
    }

    public static Process runCommand(String[] command, File directory, File logFile) {
        ProcessBuilder pa;
        if (isThisWindows()) {
            pa = new ProcessBuilder(ArrayUtils.addAll(new String[]{"cmd", "/C"}, command));
        } else {
            pa = new ProcessBuilder(ArrayUtils.addAll(command));
        }
        Map<String, String> envA = pa.environment();
        envA.put("PATH", System.getenv("PATH"));
        pa.directory(directory);
        pa.redirectErrorStream(true);
        pa.redirectOutput(ProcessBuilder.Redirect.to(logFile));
        Process pA = null;
        try {
            pA = pa.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pA;
    }

    public static void pidKiller(long pid) {
        try {
            // TODO: /F is actually -9, so we are more strict on Windows. Good/no good?
            if (isThisWindows()) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/C", "taskkill", "/PID", Long.toString(pid), "/F", "/T"});
            } else {
                Runtime.getRuntime().exec(new String[]{"kill", "-15", Long.toString(pid)});
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    public static void processStopper(Process p, String artifactId) throws InterruptedException, IOException {
        // Unlike all others, Tomee creates a child :-)
        p.children().forEach(child -> {
            child.destroy();
            pidKiller(child.pid());
        });
        p.destroy();
        p.waitFor(3, TimeUnit.MINUTES);
        pidKiller(p.pid());
        if (isThisWindows()) {
            windowsCmdCleaner(artifactId);
        }
    }

    public static void windowsCmdCleaner(String artifactId) throws IOException, InterruptedException {
        List<Long> pidsToKill = new ArrayList<>(2);
        String[] wmicPIDcmd = new String[]{
                "wmic", "process", "where", "(",
                "commandline", "like", "\"%\\\\" + artifactId + "\\\\%\"", "and", "name", "=", "\"java.exe\"", "and",
                "not", "commandline", "like", "\"%wmic%\"", "and",
                "not", "commandline", "like", "\"%maven%\"",
                ")", "get", "Processid", "/format:list"};
        ProcessBuilder pbA = new ProcessBuilder(wmicPIDcmd);
        pbA.redirectErrorStream(true);
        Process p = pbA.start();
        try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String l;
            while ((l = processOutputReader.readLine()) != null) {
                if (l.contains("ProcessId=")) {
                    try {
                        pidsToKill.add(Long.parseLong(l.split("=")[1].trim()));
                    } catch (NumberFormatException ex) {
                        //Silence is golden. We don't care about wmic output glitches. This is a best effort.
                    }
                }
            }
            p.waitFor();
        }
        if (pidsToKill.isEmpty()) {
            LOGGER.warning("wmic didn't find any additional PIDs to kill.");
        } else {
            LOGGER.info(String.format("wmic found %d additional pids to kill", pidsToKill.size()));
        }
        pidsToKill.forEach(Commands::pidKiller);
    }

    public static boolean isThisWindows() {
        return System.getProperty("os.name").matches(".*[Ww]indows.*");
    }

    public static class ProcessRunner implements Runnable {
        final File directory;
        final File log;
        final String[] command;
        final long timeoutMinutes;

        public ProcessRunner(File directory, File log, String[] command, long timeoutMinutes) {
            this.directory = directory;
            this.log = log;
            this.command = command;
            this.timeoutMinutes = timeoutMinutes;
        }

        @Override
        public void run() {
            ProcessBuilder pb;
            if (isThisWindows()) {
                pb = new ProcessBuilder(ArrayUtils.addAll(new String[]{"cmd", "/C"}, command));
            } else {
                pb = new ProcessBuilder(ArrayUtils.addAll(command));
            }
            Map<String, String> env = pb.environment();
            env.put("PATH", System.getenv("PATH"));
            pb.directory(directory);
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.to(log));
            Process p = null;
            try {
                p = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Objects.requireNonNull(p).waitFor(timeoutMinutes, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
