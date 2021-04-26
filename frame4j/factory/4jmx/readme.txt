Files for JMX / JConsole access to Frame4J based programs

Copyright (c) 2021 Albrecht Weinert  a-weinert.de
$Revision: 35 $ ($Date: 2021-04-04 12:58:39 +0200 (So, 04 Apr 2021) $)

To enable no-auth-no-ssl JMX replace the file management.properties
in .../jre/lib/management/ of your Java installation. Set the correct
IP address for access via the private laboratory (W)LAN there. Other
files in .../jre/lib/management/ may stay as they are.

To start a Java application / JVM for JMX access use the script jmx
instead of java. Put it in a directory in the PATH; like ~/bin/ e.g..
Set the correct IP address for access via the private laboratory
(W)LAN in this script. Background is a JMX bug on Linux. Without 
the IP set in the property the JVM would answer all external requests
on the mirror IP (leading to no connect error for any client).
 