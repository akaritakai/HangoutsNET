HangoutsNET Web Application
===========================

The HangoutsNET web application is two fold. Part one allows for password resets through a common web interface at `https://chat.parted.me/recover/` and part two allows for a web chat which authenticates using the ZNC gateway at `https://chat.parted.me/`.

Installation
============

The HangoutsNET Web Application was installed on chat.parted.me as follows:

```sh
mydir=`pwd`
git clone --recursive https://github.com/akaritakai/HangoutsNET

# Populate the Java source in /usr/local/src/web
mkdir /usr/local/src/web
cp ./HangoutsNET/web/recover/build.xml /usr/local/src/web/build.xml
cp -R ./HangoutsNET/web/recover/src /usr/local/src/web/

# Add the Java libraries
mkdir /usr/local/src/web/lib
cd /usr/local/src/web/lib
wget http://central.maven.org/maven2/commons-codec/commons-codec/1.9/commons-codec-1.9.jar
wget http://central.maven.org/maven2/commons-io/commons-io/2.4/commons-io-2.4.jar
wget http://central.maven.org/maven2/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar
wget http://central.maven.org/maven2/commons-validator/commons-validator/1.4.0/commons-validator-1.4.0.jar
wget http://central.maven.org/maven2/org/eclipse/jetty/jetty-server/9.2.1.v20140609/jetty-server-9.2.1.v20140609.jar
wget http://central.maven.org/maven2/org/eclipse/jetty/jetty-servlet/9.2.1.v20140609/jetty-servlet-9.2.1.v20140609.jar
wget http://central.maven.org/maven2/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar
wget http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.7/slf4j-api-1.7.7.jar
wget http://central.maven.org/maven2/org/slf4j/slf4j-nop/1.7.7/slf4j-nop-1.7.7.jar
wget http://central.maven.org/maven2/com/google/guava/guava/17.0/guava-17.0.jar
wget http://central.maven.org/maven2/com/zaxxer/HikariCP/1.4.0/HikariCP-1.4.0.jar
wget http://central.maven.org/maven2/javax/mail/javax.mail-api/1.5.2/javax.mail-api-1.5.2.jar
wget http://central.maven.org/maven2/javassist/javassist/3.12.1.GA/javassist-3.12.1.GA.jar
wget http://central.maven.org/maven2/org/mindrot/jbcrypt/0.3m/jbcrypt-0.3m.jar
wget http://central.maven.org/maven2/joda-time/joda-time/2.3/joda-time-2.3.jar
wget http://central.maven.org/maven2/mysql/mysql-connector-java/5.1.31/mysql-connector-java-5.1.31.jar
wget http://central.maven.org/maven2/org/pircbotx/pircbotx/2.0.1/pircbotx-2.0.1.jar

# Build Java project
mkdir /opt/web
cd /usr/local/src/web
ant

# Put data files in /opt/web
cp ./HangoutsNET/web/404.html /opt/web/index.html
mkdir /opt/web/recover
cp -R ./HangoutsNET/web/recover/templates/* /opt/web/recover
cp -R ./HangoutsNET/web/static /opt/web

# Add IRIS
cd /opt/web 
git clone --recursive https://github.com/akaritakai/iris

# Clean-up
rm -rf $mydir/HangoutsNET
```

Additional Dependencies
=======================
The HangoutsNET Web Application runs best with Oracle's Java 8 JRE, and also requires nginx and IRIS dependencies to be configured. A configuration file for nginx is shown in `nginx.conf` and IRIS instructions can be described at `https://github.com/atheme/iris`.

Configuration
=============
The HangoutsNET Web Application is managed by the upstart script in `/etc/init/hangoutsweb.conf` which allows for `start hangoutsweb` and `stop hangoutsweb` to be used to turn on and off the server.

Updates are managed by the update script in `/usr/local/bin/update` as `update hangoutsweb`.
