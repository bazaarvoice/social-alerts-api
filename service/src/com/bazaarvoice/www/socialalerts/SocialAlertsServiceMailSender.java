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

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class used for sending alerts using JavaMail
 */
public class SocialAlertsServiceMailSender {

    public static void sendMailUsingTemplate(String sessionUUID, String fromAddress, String toAddress, 
                                             String templateID, Parameter[] parameters) throws MessagingException{
        SocialAlertsServiceConfiguration config = SocialAlertsServiceConfiguration.getInstance();
        String subject = config.getTemplateSubject(templateID);
        String body = config.getTemplateBody(templateID);
        for (Parameter p : parameters) {
            subject = subject.replaceAll("\\{"+p.getName()+"\\}", p.getValue());
            body = body.replaceAll("\\{"+p.getName()+"\\}", p.getValue());
        }

        sendMail(sessionUUID, fromAddress, toAddress, subject, body);
    }

    public static void sendMail(String sessionUUID, String fromAddress, String toAddress, String subject, String text) throws MessagingException {
        SocialAlertsServiceMailSessionManager sessionManager = SocialAlertsServiceMailSessionManager.getInstance();

        Transport transport = sessionManager.getTransport(sessionUUID);
        Session session = sessionManager.getSession(sessionUUID);

        if (session == null || transport == null)
            throw new RuntimeException("Invalid smtp session.");

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(toAddress));
        message.setSubject(subject);
        message.setText(text);

        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
    }
}
