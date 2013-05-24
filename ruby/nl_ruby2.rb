require 'splunk-sdk-ruby'

# How to get to the Splunk server. 
config = {
  :scheme => "https",
  :host => "localhost",
  :port => 8000,
  :username => "admin",
  :password => "changeme"
}

# Create a Service logged into Splunk, and print the authentication token
# that Splunk sent us.
service0 = Splunk::connect(config)
puts "Logged in service 0. Token: #{service0.token}"

# connect is a synonym for creating a Service by hand and calling login.
service1 = Splunk::Service.new(config)
service1.login()
puts "Logged in. Token: #{service1.token}"

# We don't always want to call login. If we have already obtained a valid
# token, we can use it instead of a username or password. In this case we
# must create the Service manually.
token_config = {
  :scheme => config[:scheme],
  :host => config[:host],
  :port => config[:port],
  :token => service1.token
}

service2 = Splunk::Service.new(token_config)
puts "Logged in. Token: #{service2.token}"