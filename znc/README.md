ZNC Server
==========

The ZNC server acts as the "gateway" to the HangoutsNET network. It controls user authentication as well as provides the buffering and storing of private messages. It ensures that every user connecting has a previously registered username and password, allows for centralized control of those users, and ensures that all IRC traffic is encrypted.

The znc-contrib repository (linked above) provides additional modules for the server.

Installation
============

ZNC was installed on chat.parted.me as follows:

```sh
adduser --system --home /opt/znc/var --no-create-home znc

cd /usr/local/src
git clone --recursive https://github.com/znc/znc
cd znc
./autogen.sh
./configure --prefix="/opt/znc" --enable-cyrus
make && make install

cd /usr/local/src
git clone --recursive https://github.com/kylef/znc-contrib
cd znc-contrib
export PATH=$PATH:/opt/znc/bin
sed -i 's/MODDIR=.*/MODDIR=\/opt\/znc\/lib\/znc/' Makefile
make privmsg
```

Configuration
=============
ZNC is managed by the upstart script in `/etc/init/znc.conf` which allows for `start znc` and `stop znc` to be used to turn on and off the server.

Updates are managed by the update script in `/usr/local/bin/update` as `update znc`.

HangoutsNET requires that one user *zncadmin* be an admin user on ZNC for the purposes of resetting passwords through the HangoutsNET Web Application.

The `znc.conf` script describes the daemon configuration.
