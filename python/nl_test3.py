import splunklib 
import splunklib.client as client
from xml.dom import minidom

baseurl = 'https://localhost:8089'
userName = 'admin'
password = 'changeme'

myhttp = httplib2.Http()

# Step 1: Get an auth token
serverContent = myhttp.request(baseurl + '/services/auth/login', 'POST', 
    headers={}, body=urllib.urlencode({'username':userName, 'password':password}))[1]
sessionKey = minidom.parseString(serverContent).getElementsByTagName('sessionKey')[0].childNodes[0].nodeValue
#print "============>auth token:  %s  <============" % sessionKey

# Step 2: Create a search job
searchJob = myhttp.request(baseurl + '/services/search/jobs','POST',
    headers={'Authorization': 'Splunk %s' % sessionKey},body=urllib.urlencode({'search': searchQuery}))[1]
sid = minidom.parseString(searchJob).getElementsByTagName('sid')[0].childNodes[0].nodeValue
#print "============>sid:  %s  <============" % sid

# Step 3: Get search status
myhttp.add_credentials(userName, password)
services_search_status_str = '/services/search/jobs/%s/' % sid
isNotDone = True
while isNotDone:
    searchStatus = myhttp.request(baseurl + services_search_status_str, 'GET')[1]
    isDoneStatus = re.compile('isDone">(0 1)')
    isDoneStatus = isDoneStatus.search(searchStatus).groups()[0]
    if (isDoneStatus == '1'):
        isNotDone = False
#print "============>search status:  %s  <============" % isDoneStatus

# Step 4: Get search results
services_search_results_str = '/services/search/jobs/%s/results?output_mode=json&count=0' % sid
searchResults = myhttp.request(baseurl + services_search_results_str, 'GET')[1]
#print "============>length: %s  <============" % len(searchResults)
#print "============>search result:  [%s]  <============" % searchResults
