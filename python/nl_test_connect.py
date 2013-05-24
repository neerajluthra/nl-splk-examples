import splunklib.client as client

# Create a Service instance and log in 
service = client.connect(host="localhost", port=8089, username="admin", password="changeme")

print "============>Successful connection:  %s  <============\n" % service.token