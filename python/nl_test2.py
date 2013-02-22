import splunklib.client as client

HOST = "localhost"
PORT = 8089
USERNAME = "admin"
PASSWORD = "changeme"
OWNER = "admin"
APP = "search"

# Create a Service instance and log in 
service = client.connect(
    host=HOST,
    port=PORT,
    username=USERNAME,
    password=PASSWORD,
    owner=OWNER,
    app=APP)

for savedsearch in service.saved_searches:
    print "  " + savedsearch.name
