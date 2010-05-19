Bazaarvoice SocialAlerts Web Service API Reference Implementation
=================================================================

Introduction
============

This package includes the following:

* WSDL for the SocialAlerts Web Service API
* Reference Implementation of the Web Service API
* Sample client tool


Pre-Requisites
==============

* Apache Ant 1.6.2 or later
* Apache Axis2 1.5.1 or later

You need to download and install Ant and Axis2 to be able to build and run
the reference implementation of the SocialAlerts API and client tool.


Building and Running The Reference Implementation
=====================

* Type ant generate.service
* Go to Axis2_HOME/bin directory and run either 
  axis2server.bat or axis2server.sh depending on your platform.

If you go to http://localhost:8080/axis2/services/,
you should see SocialAlertsService is deployed. 

If you go to http://localhost:8080/axis2/services/SocialAlertsService?wsdl,
you should see the WSDL for the SocialAlertsService. 


Running The Client
==================

Test client exercises API functions provided by reference implementation.

Invoke the com/bazaarvoice/www/socialalerts/SocialAlertsClient.java class. 
You may use the command scripts to do so. You need to supply 3 parameters to the command:
url for the web service end point, user name and password.

 * ant run.client -Durl=http://localhost:8080/axis2/services/SocialAlertsService -Dusername=username -Dpassword=password
