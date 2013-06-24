var splunkjs = require('splunk-sdk');
var service = new splunkjs.Service({username: "admin",password: "changeme",host: "localhost",port:8089,scheme: "https",version: 6.0});
service.login(function (err, success) {
    if (err) console.log("Error logging in", err);
    else console.log(service.sessionKey);
});

// Get the collection of users, sorted by realname
var myusers = service.users();

// Print the users' real names, usernames, and roles
myusers.fetch({
  sort_key: "realname",
  sort_dir: "asc"
}, function(err, myusers) {

  console.log("There are " + myusers.list().length + " users:");

  var usercoll = myusers.list();

  for(var i = 0; i < usercoll.length; i++) {
    console.log(usercoll[i].properties().realname + " (" + usercoll[i].name + ")");
    var roles = usercoll[i].properties().roles;
    console.log("  - roles: " + roles);
  }
});
