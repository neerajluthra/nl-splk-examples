#!/usr/bin/env python
import httplib
import urllib
import json
import pprint

HOST = "localhost"
PORT = 8089
USERNAME = "admin"
PASSWORD = "changeme"

connection = httplib.HTTPSConnection(HOST, PORT)

# Login
auth_body = urllib.urlencode({"username": USERNAME,
	                   "password": PASSWORD,
	                   "output_mode": "json"})
connection.request("POST", "/services/auth/login", body=auth_body)
response = connection.getresponse()
token = "Splunk " + json.loads(response.read())["sessionKey"]


# Create a saved search
create_body = urllib.urlencode({
        "name": "mysavedsearch",
        "search": "search index=_internal | head 10"})
connection.request("POST", "/services/saved/searches",
                   body=create_body,
                   headers={"Authorization": token})
response = connection.getresponse()
if response.status != 201:
    print "Saved search creation failed (%d): %s" % \
        (response.status, response.reason)

connection.request("POST", "/services/saved/searches/mysavedsearch/dispatch",
                   headers={"Authorization": token})

# get job results
create_body = urllib.urlencode({
        "count": "3",
        "f": "sourcetype"})
connection.request("POST", "/services/search/jobs/admin__admin__search__mysavedsearch_at_1363894492_5/results",
                   body=create_body,
                   headers={"Authorization": token})
response = connection.getresponse()
if response.status != 201:
    print "Getting job results failed (%d): %s" % \
        (response.status, response.reason)
