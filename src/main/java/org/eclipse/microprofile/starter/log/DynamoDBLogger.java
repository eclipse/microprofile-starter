/*
 * Copyright (c) 2019 - 2022 Contributors to the Eclipse Foundation
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
package org.eclipse.microprofile.starter.log;

import io.quarkus.arc.Unremovable;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.starter.view.EngineData;
import org.jboss.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
@Unremovable
public class DynamoDBLogger {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final Logger LOG = Logger.getLogger(DynamoDBLogger.class);
    private static final String ACCESS_KEY = getProperty("AWS_ACCESS_KEY_ID", null);
    private static final String SECRET_KEY = getProperty("AWS_SECRET_ACCESS_KEY", null);
    private static final String WEB_APP_INSTANCE_ID = getProperty("MP_STARTER_APP_ID", "test-instance");
    private static final String REGION = getProperty("AWS_REGION", "eu-west-1");
    private static final String TABLE_NAME = getProperty("AWS_DYNAMODB_TABLE_NAME", "microprofile_starter_log");
    private static final String LOG_RECORD_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String HOST = "dynamodb" + "." + REGION + ".amazonaws.com";
    private static final String ENDPOINT = "https://" + HOST + '/';
    private static final String AMZ_TARGET = "DynamoDB_20120810.PutItem";

    public static String getProperty(String key, String def) {
        String v = System.getProperty(key);
        if (StringUtils.isNotBlank(v)) {
            return v;
        }
        v = System.getenv().get(key);
        if (StringUtils.isNotBlank(v)) {
            return v;
        }
        return def;
    }

    private static byte[] sign(final byte[] key, final byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(message);
    }

    private static byte[] signatureKey(
            final String key, final String dateStamp, final String regionName, final String serviceName)
            throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] kDate = sign(StandardCharsets.UTF_8.encode("AWS4" + key).array(), StandardCharsets.UTF_8.encode(dateStamp).array());
        final byte[] kRegion = sign(kDate, StandardCharsets.US_ASCII.encode(regionName).array());
        final byte[] kService = sign(kRegion, StandardCharsets.US_ASCII.encode(serviceName).array());
        return sign(kService, StandardCharsets.US_ASCII.encode("aws4_request").array());
    }

    private static String preparePayload(final EngineData engineData, final Date date) {
        final SimpleDateFormat logMessageTimeFormat = new SimpleDateFormat(LOG_RECORD_TIMESTAMP_FORMAT);
        final MessageDigest sha1Hash;
        try {
            sha1Hash = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SHA-1 not available.", e);
            return null;
        }
        final String timestamp = logMessageTimeFormat.format(date);
        final String logmark = Stream.concat(
                Stream.of(engineData.getMpVersion(), engineData.getSupportedServer(),
                        engineData.getBeansxmlMode(), WEB_APP_INSTANCE_ID,
                        timestamp, engineData.getMavenData().getArtifactId(),
                        engineData.getMavenData().getGroupId(),
                        engineData.getTrafficSource().toString()),
                engineData.getSelectedSpecs().stream()
        ).collect(Collectors.joining());
        sha1Hash.update(logmark.getBytes());
        final String hashedLogmark = bytesToHex(sha1Hash.digest());

        final StringBuilder s = new StringBuilder(1024)
                .append("{\"TableName\":\"")
                .append(TABLE_NAME)
                .append("\",\"Item\":{")
                .append("\"logmark\":{\"S\":\"")
                .append(hashedLogmark)
                .append("\"},")
                .append("\"mpVersion\":{\"S\":\"")
                .append(engineData.getMpVersion())
                .append("\"},")
                .append("\"supportedServer\":{\"S\":\"")
                .append(engineData.getSupportedServer())
                .append("\"},")
                .append("\"beansxmlMode\":{\"S\":\"")
                .append(engineData.getBeansxmlMode())
                .append("\"},")
                .append("\"webAppInstanceID\":{\"S\":\"")
                .append(WEB_APP_INSTANCE_ID)
                .append("\"},")
                .append("\"selectedSpecs\":{\"SS\":[\"");
        if (engineData.getSelectedSpecs().isEmpty()) {
            s.append("NONE_SELECTED");
        } else {
            s.append(String.join("\",\"", engineData.getSelectedSpecs()));
        }
        s.append("\"]},")
                .append("\"trafficSource\":{\"S\":\"")
                .append(engineData.getTrafficSource().toString())
                .append("\"},")
                .append("\"timestamp\":{\"S\":\"")
                .append(timestamp)
                .append("\"}}}");

        return s.toString();
    }

    private static String authHeaderSignatureHash(final String amzDate, final String dynamoDBJSON, final String credentialScope) {
        final String canonicalHeaders =
                "content-type:" + "application/x-amz-json-1.0" + '\n' +
                        "host:" + HOST + '\n' +
                        "x-amz-date:" + amzDate + '\n' +
                        "x-amz-target:" + AMZ_TARGET + '\n';
        // Request to be signed:
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SHA-256 not available.", e);
            return null;
        }
        digest.update(StandardCharsets.UTF_8.encode(dynamoDBJSON));
        final String payloadHash = bytesToHex(digest.digest()).toLowerCase();
        digest.reset();
        final String canonicalUri = "/";
        final String canonicalQuerystring = "";
        final String canonicalRequest =
                "POST" + '\n' +
                        canonicalUri + '\n' +
                        canonicalQuerystring + '\n' +
                        canonicalHeaders + '\n' +
                        "content-type;host;x-amz-date;x-amz-target" + '\n' +
                        payloadHash;
        digest.update(StandardCharsets.UTF_8.encode(canonicalRequest));

        return "AWS4-HMAC-SHA256" + '\n' +
                amzDate + '\n' +
                credentialScope + '\n' +
                bytesToHex(digest.digest()).toLowerCase();
    }

    private static String createAuthHeader(final Date date, final String amzDate, final String dynamoDBJSON) {
        final SimpleDateFormat dateStampFormat = new SimpleDateFormat("yyyyMMdd");
        dateStampFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String dateStamp = dateStampFormat.format(date);
        final String credentialScope = dateStamp + '/' + REGION + '/' + "dynamodb" + '/' + "aws4_request";

        // Sign request and auth header:

        final byte[] signingKey;
        final Mac mac;
        try {
            signingKey = signatureKey(SECRET_KEY, dateStamp, REGION, "dynamodb");
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOG.error("HmacSHA256 key problem:", e);
            return null;
        }
        final String stringToSign = authHeaderSignatureHash(amzDate, dynamoDBJSON, credentialScope);
        if (stringToSign == null) {
            return null;
        }
        final byte[] signatureBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.US_ASCII));
        final String signature = bytesToHex(signatureBytes).toLowerCase();

        return "AWS4-HMAC-SHA256" + ' ' + "Credential=" + ACCESS_KEY + '/' + credentialScope + ", "
                + "SignedHeaders=" + "content-type;host;x-amz-date;x-amz-target" + ", " + "Signature=" + signature;

    }

    private static void sendPayload(final String amzDate, final String authorizationHeader, final String dynamoDBJSON) {
        final HttpURLConnection myURLConnection;
        try {
            myURLConnection = (HttpURLConnection) (new URL(ENDPOINT)).openConnection();
            myURLConnection.setRequestMethod("POST");
            myURLConnection.setRequestProperty("Content-Type", "application/x-amz-json-1.0");
            myURLConnection.setRequestProperty("X-Amz-Date", amzDate);
            myURLConnection.setRequestProperty("X-Amz-Target", AMZ_TARGET);
            myURLConnection.setRequestProperty("Authorization", authorizationHeader);
            myURLConnection.setDoOutput(true);
            try (OutputStream os = myURLConnection.getOutputStream()) {
                os.write(dynamoDBJSON.getBytes(StandardCharsets.US_ASCII));
            }
            LOG.debug(myURLConnection.getResponseMessage());
        } catch (IOException e) {
            LOG.error("HttpURLConnection failed.", e);
            return;
        }

        try {
            if (myURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (LOG.isDebugEnabled()) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            LOG.debug(line);
                        }
                    }
                }
                return;
            }
        } catch (IOException e) {
            LOG.error("HttpURLConnection failed while reading error stream.", e);
            return;
        }

        if (LOG.isDebugEnabled()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    LOG.debug(line);
                }
            } catch (IOException e) {
                LOG.error("HttpURLConnection failed while reading input stream.", e);
            }
        }
    }

    private static void validateEngineData(final EngineData engineData) {
        if (engineData.getMpVersion() == null || engineData.getMpVersion().length() < 1) {
            throw new IllegalArgumentException("mpVersion must not be null/empty.");
        }
        if (engineData.getSupportedServer() == null || engineData.getSupportedServer().length() < 1) {
            throw new IllegalArgumentException("supportedServer must not be null/empty.");
        }
        if (engineData.getBeansxmlMode() == null || engineData.getBeansxmlMode().length() < 1) {
            throw new IllegalArgumentException("beansxmlMode must not be null/empty.");
        }
        if (engineData.getSelectedSpecs() == null) {
            throw new IllegalArgumentException("selectedSpecs must not be null");
        }
        if (engineData.getMavenData().getArtifactId() == null || engineData.getMavenData().getArtifactId().length() < 1) {
            throw new IllegalArgumentException("artifacId must not be null/empty.");
        }
        if (engineData.getMavenData().getGroupId() == null || engineData.getMavenData().getGroupId().length() < 1) {
            throw new IllegalArgumentException("groupId must not be null/empty.");
        }
    }

    /**
     * Expensive blocking method that creates signed request and does HTTP POST to AWS DynamoDB API
     *
     * @param engineData Logged as is except for groupIdArtifacId which is merely used to enhance log message hash.
     */
    public void log(final EngineData engineData) {
        if (ACCESS_KEY == null || (ACCESS_KEY.length()) < 10) {
            LOG.warn("System property AWS_ACCESS_KEY_ID not defined. Log call dropped.");
            return;
        }
        if (SECRET_KEY == null || (SECRET_KEY.length()) < 32) {
            LOG.warn("System property AWS_SECRET_ACCESS_KEY not defined. Log call dropped.");
            return;
        }

        validateEngineData(engineData);

        final Date date = new Date();

        final String dynamoDBJSON = preparePayload(engineData, date);
        if (dynamoDBJSON == null) {
            LOG.error("Failed to prepare DynamoDB JSON payload.");
            return;
        }

        final SimpleDateFormat amzDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        amzDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String amzDate = amzDateFormat.format(date);
        final String authorizationHeader = createAuthHeader(date, amzDate, dynamoDBJSON);
        if (authorizationHeader == null) {
            LOG.error("Failed to prepare authorizationHeader.");
            return;
        }

        sendPayload(amzDate, authorizationHeader, dynamoDBJSON);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
