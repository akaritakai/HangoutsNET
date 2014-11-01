HangoutsNET Web Application
===========================

The HangoutsNET web application is two fold. Part one allows for password resets through a common web interface at `https://chat.parted.me/recover/` and part two allows for a web chat which authenticates using the ZNC gateway at `https://chat.parted.me/`.

Installation
============

The HangoutsNET Web Application was installed on chat.parted.me as follows:

```sh
mydir=`pwd`
git clone --recursive https://github.com/akaritakai/HangoutsNET

# Populate the Ruby source in /usr/local/src/web
mkdir /usr/local/src/web
cp ./HangoutsNET/web/recover/build.xml /usr/local/src/web/build.xml
cp -R ./HangoutsNET/web/recover/src /usr/local/src/web/

# Populate the Ruby Source in /opt/web/
mkdir /opt/web
mkdir /opt/web/ruby
cp -R ./HangoutsNET/web/recover/src/* /opt/web/ruby/

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
The HangoutsNET Web Application uses Ruby, and also requires nginx, MySQL, and IRIS dependencies to be configured. A configuration file for nginx is shown in `nginx.conf` and IRIS instructions can be described at `https://github.com/atheme/iris`.

Configuration
=============
The HangoutsNET Web Application is managed by the upstart script in `/etc/init/hangoutsweb.conf` which allows for `start hangoutsweb` and `stop hangoutsweb` to be used to turn on and off the server.

Updates are managed by the update script in `/usr/local/bin/update` as `update hangoutsweb`.
