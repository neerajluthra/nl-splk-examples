import sys
import time
from os import path

# splunk support files
from splunklib.binding import connect
from utils import error, parse

def export(options, service):
    success = False
    squery='search * index=_internal | head 2'
    while not success:
        result = service.get('search/jobs/export', 
                             search="search * index=_internal | head 2", 
                             output_mode="csv",
                             timeout=60,
                             f=["sourcetype","host","source"],
                             earliest_time="0.000",
                             time_format="%s.%Q",
                             count=0)

        if result.status != 200:
            print "warning: export job failed: %d, sleep/retry" % result.status
            time.sleep(60)
        else:
            success = True

    # write export file 
    while True:
        content = result.body.read()
        if len(content) == 0: break
        print content

def listSavedSearches(options, service):
    result = service.get('saved/searches', 
                 output_mode="xml")

    #result = service.get("saved/searches",

    xml_data = result.body.read()
    sys.stdout.write(xml_data)

def main():
    options = parse(sys.argv[1:], {}, ".splunkrc")
    service = connect(**options.kwargs)
    #export(options, service)
    #listSavedSearches(options, service)
    listApps(options, service)

def listApps(options, service):
    result = service.get('apps/local/odata', 
                 output_mode="xml")

    xml_data = result.body.read()
    sys.stdout.write(xml_data)

if __name__ == '__main__':
    main()
