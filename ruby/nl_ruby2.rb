#require "/Users/nluthra/git/splunk-sdk-ruby/lib/splunk-sdk-ruby"
require 'splunk-sdk-ruby'
service = Splunk::connect(:scheme=>"https", :host=>"localhost", :port=>8089, :username=>"admin", :password=>"changeme")

main = service.indexes["main"]
socket = main.attach()
begin
  socket.write("The first event.\r\n")
  socket.write("The second event.\r\n")
ensure
  socket.close()
end

