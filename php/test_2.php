<html>
<head>

</head>

<title>Splunk Javascript Test</title>
<body>
<script type="text/javascript" src="jquery/jquery-1.8.2.js"></script>
<script type="text/javascript" src="splunk_js_scripts/splunk.js"></script>

<script type="text/javascript" charset="utf-8">

//log into splunk
var service = new splunkjs.Service({
  username:"admin",
  password:"changme",
  port:"8888"
});

//identify indexes
var myindexes = service.indexes();

//called each time you write to splunk
function submitEvent(event_text) {
	myindexes.fetch(function(err, myindexes){
	var myindex = myindexes.item("test_index");
		myindex.submitEvent(event_text);
	});
}

</script>

<P>
<button onclick="submitEvent('user=<?=$_GET['user']?> action=\'button_a\'')">Button A</button>
<button onclick="submitEvent('user=<?=$_GET['user']?> action=\'button b\'')">Button B</button>
</P>


<P>
<select onChange="submitEvent('user=<?=$_GET['user']?> action=pulldown_a selection=' + this.options[this.selectedIndex].value)">
<option value="" selected>Select an item
<option value="Item 1">Item 1
<option value="Item 2">Item 2
<option value="Item 3">Item 3
</select>
</P>

<P>
I'll buy it!  And I'll pay:
<input type=text id="dollar_amount"></input>
<button onclick="submitEvent('user=<?=$_GET['user']?> action=purchase amount=' + document.getElementById('dollar_amount').value)">Buy It!</button>
</P>




</body>
</html>     
     
     