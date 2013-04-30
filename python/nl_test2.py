import splunklib.client as client
import splunklib.results as results
from time import sleep
import sys
import json

# Create a Service instance and log in 
service=client.connect(host="localhost",port=8089,username="admin",password="changeme")

ss = service.saved_searches["NLSS1"]

history = ss.history()

if len(history) > 0:
	job = history[0]

while True:
    job.refresh()
    if job["isDone"] == "1":
        sys.stdout.write("\n\nDone!\n\n")
        break
    sleep(1)


kwargs = {"output_mode": "json"}

print json.load(job.results(**kwargs))