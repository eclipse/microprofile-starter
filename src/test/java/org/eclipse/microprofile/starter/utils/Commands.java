/*
 * Copyright (c) 2017 - 2022 Contributors to the Eclipse Foundation
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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.eclipse.microprofile.starter.TestMatrixITCase.WORKSPACE_DIR;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
public class Commands {
    private static final Logger LOGGER = Logger.getLogger(Commands.class);

    private static final String STARTER_TS_WORKSPACE = "STARTER_TS_WORKSPACE";

    private static final Pattern LINUX_PS_AUX_PID = Pattern.compile("\\w*\\s*(\\d*).*");

    public static final boolean IS_THIS_WINDOWS = System.getProperty("os.name").matches(".*[Ww]indows.*");

    public static String getWorkspaceDir() {
        String env = System.getenv().get(STARTER_TS_WORKSPACE);
        if (StringUtils.isNotBlank(env)) {
            return env;
        }
        String sys = System.getProperty(STARTER_TS_WORKSPACE);
        if (StringUtils.isNotBlank(sys)) {
            return sys;
        }
        return System.getProperty("java.io.tmpdir");
    }

    public static File unzip(String location, String artifactId) throws InterruptedException, IOException {
        ProcessBuilder pb;
        if (IS_THIS_WINDOWS) {
            pb = new ProcessBuilder(
                    "powershell", "-c", "Expand-Archive", "-Path", location, "-DestinationPath", WORKSPACE_DIR, "-Force");
        } else {
            pb = new ProcessBuilder("unzip", "-o", location, "-d", WORKSPACE_DIR);
        }
        Map<String, String> env = pb.environment();
        env.put("PATH", System.getenv("PATH"));
        pb.directory(new File(WORKSPACE_DIR));
        File unzipLog = new File(WORKSPACE_DIR + File.separator + artifactId + "-unzip.log");
        if (unzipLog.exists()) {
            unzipLog.delete();
        }
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.to(unzipLog));
        Process p = pb.start();
        // On slow cloud VMs with weird I/O, this could be minutes for some reason...
        p.waitFor(3, TimeUnit.MINUTES);
        return unzipLog;
    }

    public static void deleteRecursively(Path dir) throws IOException {
        if (dir == null || Files.notExists(dir)) {
            return;
        }
        Files.walkFileTree(dir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (DirectoryNotEmptyException e) {
                    LOGGER.error(dir.toAbsolutePath()
                            + " is not empty. That means someone is still writing to it. Stray Gradle daemon? Server?");
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Does not fail the TS. Makes the best effort to clean up.
     *
     * @param artifactId by convention, this is a filename friendly name of the server
     */
    public static void cleanWorkspace(String artifactId) throws IOException {
        deleteRecursively(Path.of(WORKSPACE_DIR, artifactId));
        Files.deleteIfExists(Path.of(WORKSPACE_DIR, artifactId + ".zip"));
        Files.deleteIfExists(Path.of(WORKSPACE_DIR, artifactId + "-unzip.log"));
    }

    public static boolean waitForTcpClosed(String host, int port, long loopTimeoutS)
            throws InterruptedException, UnknownHostException {
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
        if (IS_THIS_WINDOWS) {
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

    public static void pidKiller(Long... pids) {
        try {
            final List<String> cmd = new ArrayList<>();
            if (IS_THIS_WINDOWS) {
                cmd.add("cmd");
                cmd.add("/C");
                cmd.add("taskkill");
                for (long pid : pids) {
                    cmd.add("/PID");
                    cmd.add(Long.toString(pid));
                }
                cmd.add("/F");
                cmd.add("/T");
            } else {
                cmd.add("kill");
                cmd.add("-9");
                for (long pid : pids) {
                    cmd.add(Long.toString(pid));
                }
            }
            Runtime.getRuntime().exec(cmd.toArray(new String[0]));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
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
        if (IS_THIS_WINDOWS) {
            windowsCmdCleaner(artifactId);
        }
    }

    public static void windowsCmdCleaner(String... appNames) throws IOException, InterruptedException {
        for (String appName : appNames) {
            final List<Long> pidsToKill = new ArrayList<>(2);
            final String[] wmicPIDcmd = new String[]{
                    "wmic", "process", "where", "(",
                    "commandline", "like", "\"%\\\\" + appName + "\\\\%\"", "and",
                    "not", "commandline", "like", "\"%wmic%\"", "and",
                    "not", "commandline", "like", "\"%maven%\"",
                    ")", "get", "Processid", "/format:list"};
            final ProcessBuilder pbA = new ProcessBuilder(wmicPIDcmd);
            pbA.redirectErrorStream(true);
            final Process p = pbA.start();
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
                LOGGER.warn("wmic didn't find any additional PIDs to kill.");
            } else {
                LOGGER.info(String.format("wmic found %d additional pids to kill", pidsToKill.size()));
            }
            pidKiller(pidsToKill.toArray(new Long[0]));
        }
    }

    public static void linuxCmdCleaner(String... appNames) throws IOException, InterruptedException {
        for (String appName : appNames) {
            final List<Long> pidsToKill = new ArrayList<>(2);
            final ProcessBuilder pbA = new ProcessBuilder("ps", "aux");
            pbA.redirectErrorStream(true);
            final Process p = pbA.start();
            try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String l;
                while ((l = processOutputReader.readLine()) != null) {
                    if (l.contains(appName)) {
                        final Matcher m = LINUX_PS_AUX_PID.matcher(l);
                        if (m.lookingAt()) {
                            try {
                                pidsToKill.add(Long.parseLong(m.group(1)));
                            } catch (NumberFormatException ex) {
                                // Silence is golden. We don't care about ps output glitches. This is the best effort.
                            }
                        }
                    }
                }
                p.waitFor();
            }
            if (pidsToKill.isEmpty()) {
                LOGGER.warn("ps didn't find any additional PIDs to kill.");
            } else {
                LOGGER.info(String.format("ps found %d additional pids to kill", pidsToKill.size()));
            }
            pidKiller(pidsToKill.toArray(new Long[0]));
        }
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
            if (IS_THIS_WINDOWS) {
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
