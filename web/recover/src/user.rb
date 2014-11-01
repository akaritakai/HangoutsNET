#!/usr/bin/env ruby

require_relative 'db'

class HangoutsUser

  def initialize username, password, sasl_password, email, realname
    @username = username
    @password = password
    @sasl_password = sasl_password
    @email = email
    @realname = realname
  end

  def get_username
    @username
  end

  def get_password
    @password
  end

  def get_sasl_password
    @sasl_password
  end

  def get_email
    @email
  end

  def get_realname
    @realname
  end

  def set_password password
    begin
      # Update in database
      con = DB.con
      stmt = con.prepare 'UPDATE users SET password = ? WHERE username = ?'
      stmt.execute password
      stmt.close

      # Update in ZNC
      ZNCAdmin.set_password(@username, password)
    ensure
      con.close
    end
  end

  def self.from_info info
    begin
      con = DB.con
      query = con.prepare 'SELECT * FROM users WHERE username = ?'
      query = query.execute info
      result = query.fetch
      return HangoutsUser.new(result[0], result[1], result[2], result[3], result[4])
    rescue
      return nil
    ensure
      con.close if con
    end
  end

  def self.from_key key
    begin
      con = DB.con
      query = con.prepare 'SELECT username FROM recover_keys WHERE token = ?'
      query = query.execute key
      result = query.fetch
      return HangoutsUser.from_info result[0]
    rescue
      return nil
    ensure
      con.close if con
    end
  end

end