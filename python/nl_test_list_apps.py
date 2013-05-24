import splunklib.client as client

# Create a Service instance and log in 
service = client.connect(host="localhost", port=8089, username="admin", password="changeme")

# Print installed apps to the console
print "============>Printing a list of all apps on Splunk server:<============\n"
for app in service.apps:
    print app.name

print

