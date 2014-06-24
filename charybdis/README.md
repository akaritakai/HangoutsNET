Charybdis Server
================

The Charybdis server is the backbone of HangoutsNET -- connecting and allowing the IRC services (Atheme) to talk to the gateway (ZNC).

Installation
============

Charybdis was installed on chat.parted.me as follows:

```sh
adduser --system --home /opt/charybdis --no-create-home charybdis 

cd /usr/local/src
git clone --recursive https://github.com/atheme/charybdis
cd charybdis
./configure --prefix="/opt/charybdis" --enable-epoll --enable-openssl --enable-small-net
make && make install
```

Configuration
=============
Charybdis is managed by the upstart script in `/etc/init/znc.conf` which allows for `start charybdis` and `stop charybdis` to be used to turn on and off the server.

Updates are managed by the update script in `/usr/local/bin/update` as `update charybids`.

The `ircd.conf` script describes the daemon configuration, and the `ircd.motd` file provides a MOTD to be presented to users upon connecting.
