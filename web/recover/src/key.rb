#!/usr/bin/env ruby

require_relative 'db'

class KeyDb

  def initialize
    thread = Thread.new {
      sleep 60000; # one minute
      begin
        con = DB.con
        con.query 'DELETE FROM recover_keys WHERE creation_date < UNIX_TIMESTAMP(DATE_SUB(NOW(), INTERVAL 1 DAY00'
      ensure
        con.close if con
      end
    }
  end

  def self.get_key user
    return nil if user.nil?
    key = get_random_str
    begin
      con = DB.con
      stmt = con.prepare 'INSERT INTO recover_keys (username, token, creation_date) VALUES (?, ?, ?) on DUPLICATE KEY UPDATE token = ?'
      stmt = stmt.execute user.get_username, key, Time.now.to_i, key
      return key
    rescue
      return nil
    ensure
      con.close if con
    end
  end

  def self.get_user key
    return HangoutsUser.from_key key
  end

  def self.get_random_str length=24
    chars = ('A'..'Z').to_a + ('a'..'z').to_a + ('0'..'9').to_a
    password = ''
    length.times { password << chars[rand(chars.size)] }
    return password
  end

end

KeyDb.new()