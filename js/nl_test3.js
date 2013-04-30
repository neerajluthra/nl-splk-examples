var splunkjs = require('splunk-sdk');
var service = new splunkjs.Service({username: "admin",password: "changeme",host: "localhost",port:8089,scheme: "https",version: 6.0});
service.login(function (err, success) {
    if (err) console.log("Error logging in", err);
    else console.log(service.sessionKey);
});