require "/Users/nluthra/git/splunk-sdk-ruby/lib/splunk-sdk-ruby.rb"
#require "splunk-sdk-ruby"

def getService
	config = { :username => "admin", :password => "changeme", :server => "localhost", :port => "8089" }
	service = Splunk::connect(config)
	service.login
	service
end

service = getService

saved_search = service.saved_searches["ODBCDemoSavedSearch"]
job = saved_search.dispatch()
while !job.is_done?()
    sleep(1)
end
stream = job.results()
results = Splunk::ResultsReader.new(stream)
count = 0
results.each do |result|
    count += 1
end
puts "....................."
puts count
puts "....................."

stream = service.create_oneshot("search index=_internal | head 2", :count => 0)
results = Splunk::ResultsReader.new(stream)
count = 0
results.each do |result|
    count += 1
end



puts "Listing Oneshot Job results ..."
stream = service.jobs.create_oneshot("search index=_internal | head 2")
reader = Splunk::ResultsReader.new(stream)
reader.each do |result|
  puts result
end
puts

puts "Listing Oneshot Job results in json ..."
stream = service.jobs.create_oneshot("search index=_internal | head 2", :output_mode=>"json")
puts stream
puts
