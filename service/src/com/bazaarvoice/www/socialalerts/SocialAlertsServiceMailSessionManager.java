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
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Session Manager class maintains a session with an SMTP server
 * using JavaMail
 */
public class SocialAlertsServiceMailSessionManager {

    public final static String MAIL_PROTOCOL = "smtps";

    private static SocialAlertsServiceMailSessionManager managerInstance = new SocialAlertsServiceMailSessionManager();

    private Map<String, Transport> transportMap = new HashMap<String, Transport>();

    private Map<String, Session> sessionMap = new HashMap<String, Session>();

    public static SocialAlertsServiceMailSessionManager getInstance() {
        return managerInstance;
    }

    private SocialAlertsServiceMailSessionManager() {
    }

    public synchronized String login(String username, String password) throws MessagingException {
        Properties props = new Properties();


        SocialAlertsServiceConfiguration config = SocialAlertsServiceConfiguration.getInstance();
        props.put("mail.transport.protocol", MAIL_PROTOCOL);
        props.put("mail.smtps.host", config.getSMTPHostName());
        props.put("mail.smtps.auth", "true");

        Session session = Session.getInstance(props);

        try {
            Transport transport = session.getTransport();
            transport.connect(config.getSMTPHostName(), config.getSMTPHostPort(),
                    username, password);

            String newSessionUUID = UUID.randomUUID().toString();
            transportMap.put(newSessionUUID, transport);
            sessionMap.put(newSessionUUID, session);

            return newSessionUUID;
        }
        catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void logout(String sessionUUID) throws MessagingException {
        sessionMap.remove(sessionUUID);
        Transport transport = transportMap.remove(sessionUUID);
        if (transport != null) {
            transport.close();
        }
    }

    public Transport getTransport(String sessionUUID) {
        return transportMap.get(sessionUUID);
    }

    public Session getSession(String sessionUUID) {
        return sessionMap.get(sessionUUID);
    }

}
