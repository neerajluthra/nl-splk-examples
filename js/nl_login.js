var splunkjs = require('splunk-sdk');
//var service = new splunkjs.Service({username:"admin", password:"changeme", host:"localhost", port:8089, scheme:"https", version:6.0});

var service = new splunkjs.Service({
    //username: "admin",
    //password: "changeme",
    sessionKey: "63829ffc2b74cb7712f21c25c26f2d29",
    scheme: "https",
    host: "localhost",
    port:"8089",
    version:"5.0"
});

service.login(function (err, success) {
    if (err) {
      console.log("Error logging in", err);
    } else {
      console.log(service.sessionKey);
    }
});
