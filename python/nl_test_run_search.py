import splunklib.client as client
import splunklib.results as results
from time import sleep
import json

# Create a Service instance and log in 
service=client.connect(host="localhost",port=8089,username="admin",password="changeme")

ss = service.saved_searches["NL2"]
job = ss.dispatch()

while not job.is_ready():
	sleep(1)

print "============>Job output in default (XML) mode:<============\n"
print job.results()


print "============>Job output in JSON mode:<============\n"
kwargs = {"output_mode": "json"}
print json.load(job.results(**kwargs))
print