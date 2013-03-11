import splunklib.client as client

HOST = "localhost"
PORT = 8089
USERNAME = "admin"
PASSWORD = "changeme"
OWNER = "admin"
APP = "oidemo"

# Create a Service instance and log in 
service=client.connect(host="localhost",port=8089,username="admin",password="changeme",autologin=True,app="-",owner="-")

print len(service.saved_searches)

for savedsearch in service.saved_searches:
    print "  " + savedsearch.name
