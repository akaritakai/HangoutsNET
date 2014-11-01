#!/usr/bin/env ruby

require 'mysql'

class DB

  @@host = 'localhost'
  @@db   = 'hangouts'
  @@user = 'hangouts'
  @@pass = 'redacted' # redacted

  def self.con
    return Mysql.new @@host, @@user, @@pass, @@db
  end

end