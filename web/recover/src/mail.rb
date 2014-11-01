#!/usr/bin/env ruby

require 'net/smtp'

def send_mail realname, email, key
  message = <<EOF
From: HangoutsNET <no-reply@parted.me>
To: #{realname} <#{email}>
Subject: HangoutsNET Account Recovery

#{realname},

We've sent this message because a request to reset your password was submitted to us.

To change your password, please click on the following link: https://chat.parted.me/recover/#{key}/

Sincerely,

HangoutsNET Account Recovery System
EOF

  Net::SMTP.start('localhost') do |smtp|
    smtp.send_message message, 'no-reply@parted.me', email
  end
end