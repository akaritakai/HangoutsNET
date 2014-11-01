#!/usr/bin/env ruby

require 'socket'

class ZNCAdmin

  def self.set_password user, pass
    s = TCPSocket.open('127.0.0.1', 1340)
    s.puts 'PASS zncadmin:redacted' # redacted
    s.puts 'NICK ZNCAdmin'
    s.puts 'USER zncadmin 8 * :ZNC Admin'
    s.puts "PRIVMSG CHAT-controlpanel :Set Password #{user} #{pass}"
  end

end