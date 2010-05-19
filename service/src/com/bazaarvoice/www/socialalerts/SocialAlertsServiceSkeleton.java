/*
 * Copyright 2010 Bazaarvoice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.bazaarvoice.www.socialalerts;

import javax.mail.MessagingException;
import javax.mail.Transport;
import java.util.logging.Logger;

/**
 * Reference implementation for the SocialAlerts web service
 * <p/>
 * Uses JavaMail to send messages using a configured smtp server
 * (for instance, gmail).
 * <p/>
 * See socialalerts.properties for configuration parameters
 */
public class SocialAlertsServiceSkeleton {

    private final static Logger logger = Logger.getLogger(SocialAlertsServiceSkeleton.class.getName());

    public LoginResponse login(LoginRequest param0) throws LoginFaultMessage {
        String username = param0.getUsername();
        String password = param0.getPassword();
        logger.info("Received login call for user: " + username);

        SocialAlertsServiceMailSessionManager sessionManager = SocialAlertsServiceMailSessionManager.getInstance();
        String sessionUUID = null;
        try {
            sessionUUID = sessionManager.login(username, password);

            logger.info("Auth succeeded for user: " + username);
            logger.info("Session started: " + sessionUUID);
        } catch (MessagingException e) {
            logger.warning("Auth failed for user: " + username);

            LoginFault fault = generateLoginFault("Auth failure for " + username,
                    ErrorCode.ERROR_AUTH_FAILURE, e.toString());
            LoginFaultMessage messageException = new LoginFaultMessage();
            messageException.setFaultMessage(fault);

            throw messageException;
        }

        LoginResponse response = new LoginResponse();
        LoginResult result = new LoginResult();
        result.setSessionId(sessionUUID);
        response.setResult(result);

        return response;
    }

    public SendAlertResponse sendAlert(SendAlertRequest param0) {
        String sessionID = param0.getSessionId();
        Alert alert = param0.getAlert();
        logger.info("Received sendAlert call for session: " + sessionID);

        AlertSendResult result = sendAlert(sessionID, alert);

        SendAlertResponse response = new SendAlertResponse();
        response.setResult(result);

        return response;
    }

    public SendAlertBatchResponse sendAlertBatch(SendAlertBatchRequest param0) {
        String sessionID = param0.getSessionId();
        logger.info("Received sendAlertBatch call for session: " + sessionID);

        SendAlertBatchResponse response = new SendAlertBatchResponse();

        for (Alert alert : param0.getAlert()) {
            AlertSendResult result = sendAlert(sessionID, alert);
            response.addResult(result);
        }

        return response;
    }

    public LogoutResponse logout(LogoutRequest param0) {
        String sessionID = param0.getSessionId();
        logger.info("Received logout call for session: " + sessionID);

        LogoutResponse response = new LogoutResponse();

        SocialAlertsServiceMailSessionManager sessionManager = SocialAlertsServiceMailSessionManager.getInstance();
        try {
            sessionManager.logout(sessionID);
            response.setResult(true);

            logger.info("Session closed: " + sessionID);
        } catch (MessagingException e) {
            response.setResult(false);
            logger.warning("Failed closing session: " + sessionID);
        }

        return response;
    }

    private AlertSendResult sendAlert(String sessionID, Alert alert) {
        AlertSendResult result = new AlertSendResult();
        result.setAlertID(alert.getAlertID());
        result.setSuccess(true);

        // validate session first
        SocialAlertsServiceMailSessionManager sessionManager = SocialAlertsServiceMailSessionManager.getInstance();
        Transport transport = sessionManager.getTransport(sessionID);
        if (transport == null) {
            // session not found
            SendFault fault = generateSendFault("Invalid session id: " + sessionID,
                    ErrorCode.ERROR_INVALID_SESSION_ID, "");
            result.setFault(fault);
            result.setSuccess(false);
        } else if (!transport.isConnected()) {
            // session expired
            SendFault fault = generateSendFault("Expired session id: " + sessionID,
                    ErrorCode.ERROR_EXPIRED_SESSION, "");
            result.setFault(fault);
            result.setSuccess(false);
        } else {
            // session is valid and connected, let's try sending alert
            try {
                SocialAlertsServiceMailSender.sendMailUsingTemplate(
                        sessionID, "test@bazaarvoice.com", alert.getDestinationAddress(),
                        alert.getTemplateID(), alert.getParameters());
            } catch (MessagingException e) {
                logger.warning("sendAlert failed for alert: " + alert.getAlertID());
                SendFault fault = generateSendFault("Failed to send alert: " + alert.getAlertID(),
                        ErrorCode.ERROR_SEND_FAILED, e.toString());
                result.setFault(fault);
                result.setSuccess(false);
            } catch (RuntimeException e) {
                logger.warning("Invalid alert: " + alert.getAlertID());
                SendFault fault = generateSendFault("Invalid alert: " + alert.getAlertID(),
                        ErrorCode.ERROR_INVALID_ALERT, e.toString());
                result.setFault(fault);
                result.setSuccess(false);
            }
        }
        return result;
    }

    private SendFault generateSendFault(String errorMessage, ErrorCode errorCode, String vendorErrorCode) {
        SendFault fault = new SendFault();

        fault.setErrorCode(errorCode);
        fault.setErrorMessage(errorMessage);
        fault.setVendorErrorCode(vendorErrorCode);

        return fault;
    }

    private LoginFault generateLoginFault(String errorMessage, ErrorCode errorCode, String vendorErrorCode) {
        LoginFault fault = new LoginFault();

        fault.setErrorCode(errorCode);
        fault.setErrorMessage(errorMessage);
        fault.setVendorErrorCode(vendorErrorCode);

        return fault;
    }

}


