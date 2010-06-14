=================================================================
Bazaarvoice Social Alerts Connector API Reference Implementation
=================================================================

=================================================================
Introduction
=================================================================

This package includes the following:

* WSDL for the Social Alerts Connector API (socialalerts.wsdl)
* Reference Implementation of the Web Service API
* Sample client tool


=================================================================
Pre-Requisites
=================================================================

* Java JDK 1.6.0 or later
* Apache Ant 1.6.2 or later
* Apache Axis2 1.5.1 or later

You need to download and install Ant and Axis2 to be able to build and run
the reference implementation of the SocialAlerts API and client tool.

Set ANT_HOME environment variable to the directory where you installed Ant,
and add the bin directory to your PATH.
Make sure you can run ant from command line.

Make sure to set AXIS2_HOME environment variable to point to the installation directory of Axis2.
Verify that you can start standalone AXIS2 server by running axis2server.bat or axis2server.sh
from the AXIS2_HOME/bin directory.


=================================================================
Building and Running The Reference Implementation
=================================================================

Social Alerts Connector API Reference Implementation provides a simple implementation
of the web service interface which uses JavaMail to send messages to an SMTP server.
Reference Implementation has been tested with google's gmail smtps server.

In order to build and run the Reference Implementation:

* ant generate.service
* Go to AXIS2_HOME/bin directory and run either
  axis2server.bat or axis2server.sh depending on your platform.

If you go to http://localhost:8080/axis2/services/ using a browser,
you should see SocialAlertsService is deployed. 

If you go to http://localhost:8080/axis2/services/SocialAlertsService?wsdl,
you should see the WSDL for the SocialAlertsService. 


=================================================================
Building and Running The Client
=================================================================


Test client exercises API functions provided by the Reference Implementation.

Test client implementation is in the com/bazaarvoice/www/socialalerts/SocialAlertsClient.java.

In order to build and run the client tool:

* ant generate.client
* ant run.client -Durl=http://localhost:8080/axis2/services/SocialAlertsService -Dusername=username -Dpassword=password -Ddestination=email_address

The following 3 parameters are required to run a client tool:
url for the web service end point, user name and password used to authenticate with the server,
and destination e-mail address.

Since Reference Implementation of the web service authenticates with gmail's smtp server,
you have to provide valid gmail username and password when running a client tool.