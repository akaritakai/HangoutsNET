#!/usr/bin/env ruby

require_relative 'user'
require_relative 'key'
require_relative 'mail'
require 'sinatra'

set :server, 'thin'
set :bind, '127.0.0.1'
set :port, 8080

get '/recover/:key/' do
  user = KeyDb.get_user params[:key]
  if user.nil?
    File.read '/opt/web/recover/badkey.html'
  else
    File.read '/opt/web/recover/passwordchange.html'
  end
end

get '/recover/' do
  File.read '/opt/web/recover/emailrequest.html'
end

post '/recover/:key/' do
  user = KeyDb.get_user params[:key]
  if user.nil?
    'bad_key'
  else
    newpass = params[:newpass]
    if newpass.nil? or newpass.empty?
      'no_pass'
    else
      'pw_changed'
    end
  end
end

post '/recover/' do
  info = params[:info]
  if info.nil? or info.empty?
    'bad_info'
  else
    user = HangoutsUser.from_info info
    if user.nil?
      'bad_info'
    else
      key = KeyDb.get_key user
      if send_mail(user.get_realname, user.get_email, key)
        'email_sent'
      else
        'email_fail'
      end
    end
  end
end

post '/sasl/' do
  username = params[:username]
  password = params[:password]
  if !username.nil? or !username.empty?
    user = HangoutsUser.from_info username
    if !user.nil?
      if !password.nil? and password == user.get_password
        user.get_sasl_password
      end
    end
  end
end
