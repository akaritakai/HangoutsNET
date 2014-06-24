Atheme Server
=============

The Atheme server provides various IRC services to users logged into HangoutsNET. For example: vanity virtualhosts, channel ACLs (for private rooms), and much more.

The additional modules in this folder (`*.c` -- to be placed in `/usr/local/src/extras`) provide additional value. 

Installation
============

Atheme was installed on chat.parted.me as follows:

```sh
adduser --system --home /opt/atheme --no-create-home atheme

cd /usr/local/src
git clone --recursive https://github.com/atheme/atheme
cd atheme
./configure --prefix="/opt/atheme" --enable-contrib --with-pcre --with-perl
make && make install

cp /usr/local/src/extras/* /usr/local/src/atheme/modules/contrib/
cd /usr/local/src/atheme/modules/contrib
make SRCS=cs_ponies.c install
make SRCS=cs_hug.c install
make SRCS=cs_boop.c install
```

Configuration
=============
Atheme is managed by the upstart script in `/etc/init/atheme.conf` which allows for `start atheme` and `stop atheme` to be used to turn on and off the server.

Updates are managed by the update script in `/usr/local/bin/update` as `update atheme`.

The `atheme.conf` script describes the server configuraiton, and the `atheme.motd` file provides a MOTD to be presented to users upon connecting.
