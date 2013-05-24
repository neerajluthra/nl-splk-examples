import splunklib.client as client

# Create a Service instance and log in 
service = client.connect(host="localhost", port=8089, username="admin", password="changeme")

# Print saved searches to the console
print "============>Printing a list of all saved searches:<============\n"
print
for saved_search in service.saved_searches:
    header = saved_search.name
    print "Saved Search Name: %s" %header
    print '='*len(header)
    key = "search"
    print "%s: %s" % (key, saved_search.content[key])
    print
