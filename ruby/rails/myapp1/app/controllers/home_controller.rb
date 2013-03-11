#require 'splunk-sdk-ruby'
require "/Users/nluthra/git/splunk-sdk-ruby/lib/splunk-sdk-ruby"

class HomeController < ApplicationController
	def index
		config = { :username => "admin", :password => "changeme", :server => "localhost", :port => "8089" }
		service = Splunk::connect(config)
		service.login
		puts(service.token)
	end
end
