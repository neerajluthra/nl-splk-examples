require "splunk-sdk-ruby"

# How to get to the Splunk server. 
config = {
  :scheme => :http,
  :host => "localhost",
  :port => 8089,
  :username => "admin",
  :password => "changeme"
}

service = Splunk::connect(config)
puts service.token

puts "Oneshot Job results ..."
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


