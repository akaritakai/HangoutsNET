Upstart Scripts
===============

The following upstarts scripts help manage HangoutsNET software. Upstart has the following advantages over other init daemons:

1. The services are monitored for crashes and automatically restarted.
2. The services wait for the file system *and* the networking system to be operational before starting (not just the runlevel).
3. They track process IDs.
4. They automatically log daemon output to `/var/log/upstart/...` (useful for dealing with crashes).
5. They are easy to read/follow at a glance.
