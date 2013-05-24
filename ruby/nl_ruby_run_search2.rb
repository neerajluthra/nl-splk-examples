require 'splunk-sdk-ruby'

# How to get to the Splunk server. 
config = {:scheme => "https", :host => "localhost", :port => 8089, :username => "admin", :password => "changeme"}

service = Splunk::connect(config)
puts "Logged in service 0. Token: #{service.token}"

job = service.create_search("search index=_internal | head 1", :earliest_time => "-1d", :latest_time => "now")

while !job.is_done?()
  sleep(0.1)
end

stream = job.results(:count => 1, :offset => 0)
results = Splunk::ResultsReader.new(stream)
results.each do |result|
  puts result["_raw"]
end
