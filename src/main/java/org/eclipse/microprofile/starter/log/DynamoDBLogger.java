/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

import org.eclipse.microprofile.starter.view.EngineData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.DatatypeConverter;
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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class DynamoDBLogger {

    private static final Logger Log = Logger.getLogger(org.eclipse.microprofile.starter.log.DynamoDBLogger.class.getName());
    private static final String accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
    private static final String secretKey = System.getProperty("AWS_SECRET_ACCESS_KEY");
    private static final String webAppInstanceID = System.getProperty("MP_STARTER_APP_ID", "test-instance");
    private static final String region = System.getProperty("AWS_REGION", "eu-west-1");
    private static final String tableName = System.getProperty("AWS_DYNAMODB_TABLE_NAME", "microprofile_starter_log");
    private static final String logRecordTimestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String method = "POST";
    private static final String service = "dynamodb";
    private static final String host = service + "." + region + ".amazonaws.com";
    private static final String endpoint = "https://" + service + "." + region + ".amazonaws.com/";
    private static final String contentType = "application/x-amz-json-1.0";
    private static final String amzTarget = "DynamoDB_20120810.PutItem";
    private static final String algorithm = "AWS4-HMAC-SHA256";
    private static final String signedHeaders = "content-type;host;x-amz-date;x-amz-target";

    /**
     * Return value for the logger
     */
    public enum Status {
        OK,
        NOK,
        NOT_CONFIGURED
    }

    private static byte[] sign(final byte[] key, final byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        final Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(message);
    }

    private static byte[] signatureKey(
            final String key, final String date_stamp, final String regionName, final String serviceName) throws NoSuchAlgorithmException, InvalidKeyException {
        final byte[] kDate = sign(StandardCharsets.UTF_8.encode("AWS4" + key).array(), StandardCharsets.UTF_8.encode(date_stamp).array());
        final byte[] kRegion = sign(kDate, StandardCharsets.US_ASCII.encode(regionName).array());
        final byte[] kService = sign(kRegion, StandardCharsets.US_ASCII.encode(serviceName).array());
        return sign(kService, StandardCharsets.US_ASCII.encode("aws4_request").array());
    }

    private static String preparePayload(final EngineData engineData, final Calendar calendar) {
        final SimpleDateFormat logMessageTimeFormat = new SimpleDateFormat(logRecordTimestampFormat);
        final MessageDigest sha1Hash;
        try {
            sha1Hash = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            Log.log(Level.SEVERE, "SHA-1 not available.", e);
            return null;
        }
        final String timestamp = logMessageTimeFormat.format(calendar.getTime());
        final String logmark = Stream.concat(
                Stream.of(engineData.getMpVersion(), engineData.getSupportedServer(), engineData.getBeansxmlMode(), webAppInstanceID,
                        timestamp, engineData.getMavenData().getArtifactId(), engineData.getMavenData().getGroupId()),
                engineData.getSelectedSpecs().stream()
        ).collect(Collectors.joining());
        sha1Hash.update(logmark.getBytes());
        final String hashedLogmark = DatatypeConverter.printHexBinary(sha1Hash.digest());

        final StringBuilder s = new StringBuilder(1024)
                .append("{\"TableName\":\"")
                .append(tableName)
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
                .append(webAppInstanceID)
                .append("\"},")
                .append("\"selectedSpecs\":{\"SS\":[\"");
        if (engineData.getSelectedSpecs().isEmpty()) {
            s.append("NONE_SELECTED");
        } else {
            s.append(String.join("\",\"", engineData.getSelectedSpecs()));
        }
        s.append("\"]},")
                .append("\"timestamp\":{\"S\":\"")
                .append(timestamp)
                .append("\"}}}");

        return s.toString();
    }

    private static String authHeaderSignatureHash(final String amzDate, final String dynamoDBJSON, final String credentialScope) {
        final String canonicalHeaders =
                "content-type:" + contentType + '\n' +
                        "host:" + host + '\n' +
                        "x-amz-date:" + amzDate + '\n' +
                        "x-amz-target:" + amzTarget + '\n';
        // Request to be signed:
        final MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Log.log(Level.SEVERE, "SHA-256 not available.", e);
            return null;
        }
        digest.update(StandardCharsets.UTF_8.encode(dynamoDBJSON));
        final String payloadHash = DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
        digest.reset();
        final String canonicalUri = "/";
        final String canonicalQuerystring = "";
        final String canonicalRequest =
                method + '\n' +
                        canonicalUri + '\n' +
                        canonicalQuerystring + '\n' +
                        canonicalHeaders + '\n' +
                        signedHeaders + '\n' +
                        payloadHash;
        digest.update(StandardCharsets.UTF_8.encode(canonicalRequest));

        return algorithm + '\n' +
                amzDate + '\n' +
                credentialScope + '\n' +
                DatatypeConverter.printHexBinary(digest.digest()).toLowerCase();
    }

    private static String createAuthHeader(final Date date, final String amzDate, final String dynamoDBJSON) {
        final SimpleDateFormat dateStampFormat = new SimpleDateFormat("yyyyMMdd");
        dateStampFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String dateStamp = dateStampFormat.format(date);
        final String credentialScope = dateStamp + '/' + region + '/' + service + '/' + "aws4_request";

        // Sign request and auth header:

        final byte[] signingKey;
        final Mac mac;
        try {
            signingKey = signatureKey(secretKey, dateStamp, region, service);
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey, "HmacSHA256"));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Log.log(Level.SEVERE, "HmacSHA256 key problem:", e);
            return null;
        }
        final String stringToSign = authHeaderSignatureHash(amzDate, dynamoDBJSON, credentialScope);
        if (stringToSign == null) {
            return null;
        }
        final byte[] signatureBytes = mac.doFinal(stringToSign.getBytes(StandardCharsets.US_ASCII));
        final String signature = DatatypeConverter.printHexBinary(signatureBytes).toLowerCase();

        return algorithm + ' ' + "Credential=" + accessKey + '/' + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

    }

    private static Status sendPayload(final String amzDate, final String authorizationHeader, final String dynamoDBJSON) {
        final HttpURLConnection myURLConnection;
        try {
            myURLConnection = (HttpURLConnection) (new URL(endpoint)).openConnection();
            myURLConnection.setRequestMethod(method);
            myURLConnection.setRequestProperty("Content-Type", contentType);
            myURLConnection.setRequestProperty("X-Amz-Date", amzDate);
            myURLConnection.setRequestProperty("X-Amz-Target", amzTarget);
            myURLConnection.setRequestProperty("Authorization", authorizationHeader);
            myURLConnection.setDoOutput(true);
            try (OutputStream os = myURLConnection.getOutputStream()) {
                os.write(dynamoDBJSON.getBytes(StandardCharsets.US_ASCII));
            }
            Log.log(Level.FINE, myURLConnection.getResponseMessage());
        } catch (IOException e) {
            Log.log(Level.SEVERE, "HttpURLConnection failed.", e);
            return Status.NOK;
        }

        try {
            if (myURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                if (Log.isLoggable(Level.FINE)) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Log.log(Level.FINE, line);
                        }
                    }
                }
                return Status.NOK;
            }
        } catch (IOException e) {
            Log.log(Level.SEVERE, "HttpURLConnection failed while reading error stream.", e);
            return Status.NOK;
        }

        if (Log.isLoggable(Level.FINE)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.log(Level.FINE, line);
                }
            } catch (IOException e) {
                Log.log(Level.SEVERE, "HttpURLConnection failed while reading input stream.", e);
                return Status.NOK;
            }
        }

        return Status.OK;
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
     * @return Either Status.OK or Status.NOK if the AWS DynamoDB access is configured.
     * The caller is not expected to be able to do anything about the error anyway.
     */
    public Status log(final EngineData engineData) {
        if (accessKey == null || (accessKey.length()) < 10) {
            Log.log(Level.FINE, "System property AWS_ACCESS_KEY_ID not defined. Log call dropped.");
            return Status.NOT_CONFIGURED;
        }
        if (secretKey == null || (secretKey.length()) < 32) {
            Log.log(Level.FINE, "System property AWS_SECRET_ACCESS_KEY not defined. Log call dropped.");
            return Status.NOT_CONFIGURED;
        }

        validateEngineData(engineData);

        final Calendar calendar = Calendar.getInstance();

        final String dynamoDBJSON = preparePayload(engineData, calendar);
        if (dynamoDBJSON == null) {
            Log.log(Level.SEVERE, "Failed to prepare DynamoDB JSON payload.");
            return Status.NOK;
        }

        final Date date = calendar.getTime();
        final SimpleDateFormat amzDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        amzDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String amzDate = amzDateFormat.format(date);
        final String authorizationHeader = createAuthHeader(date, amzDate, dynamoDBJSON);
        if (authorizationHeader == null) {
            Log.log(Level.SEVERE, "Failed to prepare authorizationHeader.");
            return Status.NOK;
        }

        return sendPayload(amzDate, authorizationHeader, dynamoDBJSON);
    }
}
