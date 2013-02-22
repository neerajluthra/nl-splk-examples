#require "/Users/nluthra/git/splunk-sdk-ruby/lib/splunk-sdk-ruby"
require 'splunk-sdk-ruby'

# How to get to the Splunk server. Edit this to match your
# own Splunk install.
config = {
    :scheme => :https,
    :host => "localhost",
    :port => 8089,
    :username => "admin",
    :password => "changeme"
}

# Create a Service logged into Splunk, and print the authentication token
# that Splunk sent us.
service0 = Splunk::connect(config)
puts "Logged in service 0. Token: #{service0.token}"

