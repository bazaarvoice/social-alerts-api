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

import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Test client for the SocialAlerts Web Service which performs the following actions
 * <p/>
 * 1. Login to the web service
 * 2. Send a test alert
 * 3. Log out
 */
public final class SocialAlertsClient {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.err.println("Usage: SocialAlertsClient <web_service_url> <user> <password>");
            return;
        }

        String url = args[0];
        String username = args[1];
        String password = args[2];

        try {
            SocialAlertsService socialAlertsService = new SocialAlertsServiceStub(url);

            // login first
            String sessionID = login(username, password, socialAlertsService);

            // send a test alert
            sendAlert(socialAlertsService, sessionID);

            // send a test alert batch
            sendAlertBatch(socialAlertsService, sessionID);

            // log out
            logout(socialAlertsService, sessionID);
        }
        catch (LoginFaultMessage e) {
            LoginFault fault = e.getFaultMessage();
            System.err.println("Login error: " + fault.getErrorCode().getValue() + ": " + fault.getErrorMessage());
            e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("Unknown error: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void logout(SocialAlertsService socialAlertsService, String sessionID) throws RemoteException {
        LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSessionId(sessionID);

        LogoutResponse logoutResponse = socialAlertsService.logout(logoutRequest);
        System.out.println("Log out - closed session: " + sessionID +
                ", success: " + logoutResponse.getResult());
    }

    private static void sendAlert(SocialAlertsService socialAlertsService, String sessionID) throws RemoteException {
        Alert alert = createTestAlert("single");

        SendAlertRequest alertRequest = new SendAlertRequest();
        alertRequest.setSessionId(sessionID);
        alertRequest.setAlert(alert);

        SendAlertResponse alertResponse = socialAlertsService.sendAlert(alertRequest);
        System.out.println("Sent alert: " + alertResponse.getResult().getAlertID() +
                ", success: " + alertResponse.getResult().getSuccess());
    }

    private static void sendAlertBatch(SocialAlertsService socialAlertsService, String sessionID) throws RemoteException {
        String templateName = "batch";
        Alert alert1 = createTestAlert(templateName);
        Alert alert2 = createTestAlert(templateName);

        SendAlertBatchRequest alertBatchRequest = new SendAlertBatchRequest();
        alertBatchRequest.setSessionId(sessionID);
        alertBatchRequest.addAlert(alert1);
        alertBatchRequest.addAlert(alert2);

        SendAlertBatchResponse alertBatchResponse = socialAlertsService.sendAlertBatch(alertBatchRequest);
        System.out.println("Sent alert batch of " + alertBatchRequest.getAlert().length);
        for (AlertSendResult result : alertBatchResponse.getResult()) {
            System.out.println("Sent alert: " + result.getAlertID() +
                    ", success: " + result.getSuccess());
        }
    }

    private static Alert createTestAlert(String templateID) {
        Alert alert = new Alert();
        String alertUUID = UUID.randomUUID().toString();
        alert.setAlertID(alertUUID);
        alert.setDestinationAddress("apinkin@gmail.com");
        alert.setTemplateID(templateID);
        alert.setParameters(generateAlertParameters());
        return alert;
    }

    private static String login(String username, String password, SocialAlertsService socialAlertsService) throws RemoteException, LoginFaultMessage {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        loginRequest.setParameters(generateLoginParameters());

        LoginResponse loginResponse = socialAlertsService.login(loginRequest);
        String sessionID = loginResponse.getResult().getSessionId();
        System.out.println("Login session id = " + loginResponse.getResult().getSessionId());
        return sessionID;
    }

    private static Parameter[] generateAlertParameters() {
        Parameter[] alertParameters = new Parameter[2];

        alertParameters[0] = new Parameter();
        alertParameters[0].setName("subject");
        alertParameters[0].setValue("test subject");

        alertParameters[1] = new Parameter();
        alertParameters[1].setName("body");
        alertParameters[1].setValue("test body");

        return alertParameters;
    }

    private static Parameter[] generateLoginParameters() {
        Parameter[] parameters = new Parameter[1];

        parameters[0] = new Parameter();
        parameters[0].setName("parameter1");
        parameters[0].setValue("value1");

        return parameters;
    }
}
