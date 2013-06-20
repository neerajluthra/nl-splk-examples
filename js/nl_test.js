var splunkjs = require('splunk-sdk');
var service = new splunkjs.Service(
	//{username:"admin", password:"sk8free", host:"demos.splunk.com", port:8089, scheme:"https", version:5.0}
	{username:"admin", password:"changeme", host:"localhost", port:8089, scheme:"https", version:6.0}
);
service.login(function (err, success) {
    if (err) {
      console.log("Error logging in", err);
    } else {
      console.log(service.sessionKey);
    }
});

// The saved search created earlier
var searchName = "NLSS1";
var mySavedSearches = service.savedSearches();
mySavedSearches.fetch(function(err, mySavedSearches) {

    console.log("There are " + mySavedSearches.list().length + " saved searches");

    var savedSearchColl = mySavedSearches.list();

	// Retrieve the saved search that was created earlier
	var mySavedSearch = mySavedSearches.item(searchName);

	// Run the saved search and poll for completion
	mySavedSearch.dispatch({"args.host": "demo_bubbles"}, function(err, job) {

	// Display the job's search ID
	console.log("Job SID: ", job.sid);

	// Poll the status of the search job
	job.track({
	  period: 200
	}, {
	  done: function(job) {
	    console.log("Done!");

	    // Print out the statics
	    console.log("Job statistics:");
	    console.log("  Event count:  " + job.properties().eventCount);
	    console.log("  Result count: " + job.properties().resultCount);
	    console.log("  Disk usage:   " + job.properties().diskUsage + " bytes");
	    console.log("  Priority:     " + job.properties().priority);

	    // Get 10 results and print them
	    job.results({
	      count: 10
	    }, function(err, results, job) {
	      console.log(JSON.stringify(results));
	    });
	  },
	  failed: function(job) {
	    console.log("Job failed")
	  },
	  error: function(err) {
	    done(err);
	  }
	});
  });

});

// The saved search created earlier
var searchName = "NLSS1";


mySavedSearches.fetch(function(err, mySavedSearches) {

});
