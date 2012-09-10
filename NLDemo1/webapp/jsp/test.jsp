<%@ page import = "users.nluthra.MyServerSideClass" %>

<html>
  <head>
    <meta charset="utf-8">
    <title>List Apps using Splunk Java SDK</title>
    <script type="text/javascript" src="../resources/jquery.min.js"></script>
    <script type="text/javascript" src="../resources/client/splunk.js"></script>
 
    <script type="text/javascript" charset="utf-8">
    function displayApps() {
		var appsList = <%= MyServerSideClass.getAppNames() %>;
		for(var i = 0; i < appsList.length; i++) {
		    myapps += "App " + i + ": " + appsList[i].name + "<br/>"
		}
		
		document.getElementById("applist").innerHTML=myapps;
    }
    </script>
</head>
<body>
    <button type="button" onclick="displayApps()">Log in and list apps</button>
    <p id="applist"></p>
</body>
</html>