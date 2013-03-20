require "/Users/nluthra/git/splunk-sdk-ruby/lib/splunk-sdk-ruby.rb"
#require "splunk-sdk-ruby"

def getService
	config = { :username => "admin", :password => "changeme", :server => "localhost", :port => "8089" }
	service = Splunk::connect(config)
	service.login
	service
end

service = getService

puts "Listing Apps ..."
	service.apps.each do |app|
	  puts "#{app.name}"
	end
puts

puts "Listing Indexes ..."
	service.apps.each do |index|
	  puts "#{index.name}"
	end
puts

puts "Listing Jobs ..."
	for i in service.jobs
		puts "#{i.sid}"
	end
puts

puts "Listing Oneshot Job results ..."
stream = service.jobs.create_oneshot("search index=_internal | head 2")
reader = ResultsReader.new(stream)
reader.each do |result|
  puts result
end
puts

puts "Listing Oneshot Job results in json ..."
stream = service.jobs.create_oneshot("search index=_internal | head 2", :output_mode=>"json")
puts stream
puts


