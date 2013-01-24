#!/usr/bin/env ruby
require "rubygems"

require "test/unit"
require "splunk-sdk-ruby/aloader"
require "splunk-sdk-ruby/context"

class NLExample1
  attr_accessor :names

  # Say bye to everybody
  def say_bye
    if @names.nil?
      puts "..."
    elsif @names.respond_to?("join")
      # Join the list elements with commas
      puts "Goodbye #{@names.join(", ")}.  Come back soon!"
    else
      puts "Goodbye #{@names}.  Come back soon!"
    end
  end

end


if __FILE__ == $0
  puts "start"
  nl1 = NLExample1.new
  config = { "user" => "admin", "password" => "changeme", "server" => "localhost", "port" => "8089" };
  context = Splunk::Context.new($config)
  puts "#{config[2]}"
  puts "finish"

end
