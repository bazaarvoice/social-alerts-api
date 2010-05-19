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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration holder for the SocialAlerts web service Reference Implementation
 */
public class SocialAlertsServiceConfiguration {

    public static final String CONFIG_FILE = "/socialalerts.properties";

    private final static Logger logger = Logger.getLogger(SocialAlertsServiceConfiguration.class.getName());

    private static SocialAlertsServiceConfiguration configInstance;

    private Properties configProperties = new Properties();

    static {
        try {
            configInstance = new SocialAlertsServiceConfiguration();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SocialAlertsServiceConfiguration getInstance() {
        return configInstance;
    }

    private void initFromProperties(String fileName) throws IOException {
        InputStreamReader configReader = new InputStreamReader(
                this.getClass().getResourceAsStream(fileName));
        configProperties.load(configReader);
        logger.info("Initialized SocialAlertsServiceConfiguration from " + fileName);
        if (logger.isLoggable(Level.FINE)) {
            for (String key : configProperties.stringPropertyNames()) {
                logger.fine("Property - " + key + " = " + configProperties.getProperty(key));
            }
        }
    }

    private SocialAlertsServiceConfiguration() throws IOException {
        initFromProperties(CONFIG_FILE);
    }

    public Properties getConfigProperties() {
        return configProperties;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public String getSMTPHostName() {
        return configProperties.getProperty("smtp.host.name");
    }

    public int getSMTPHostPort() {
        return Integer.parseInt(configProperties.getProperty("smtp.host.port"));
    }

    public String getTemplateSubject(String templateID) {
        return configProperties.getProperty("template." + templateID + ".subject");
    }

    public String getTemplateBody(String templateID) {
        return configProperties.getProperty("template." + templateID + ".body");
    }

}
