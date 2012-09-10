<%@ page import = "users.nluthra.MyServerSideClass" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Visualization Examples -- Splunk Java combined with JavaScript SDK</title>
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le HTML5 shim, for IE6-8 support of HTML elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le styles -->
    <link href="../resources/bootstrap.css" rel="stylesheet">
    <link href="../resources/prettify.css" type="text/css" rel="stylesheet" />
    <link type="text/css" rel="stylesheet" href="../resources/rickshaw/graph.css">
    <link type="text/css" rel="stylesheet" href="../resources/rickshaw/legend.css">
    <link type="text/css" rel="stylesheet" href="../resources/rickshaw/detail.css">
    <link type="text/css" rel="stylesheet" href="../resources/rickshaw/extensions.css">
    <link type="text/css" rel="stylesheet" href="../resources/rickshaw/rickshaw.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
    <style type="text/css">
      body {
      }
      
      section {
        padding-top: 60px;
      }
      
      .code {
        font-family: Monaco, 'Andale Mono', 'Courier New', monospace;
      }
      
      button.run {
        float: right;
      }
      
      pre {
        overflow-x: auto;
      }
      
      code {
        font-size: 12px!important;
        background-color: ghostWhite!important;
        color: #444!important;
        padding: 0 .2em!important;
        border: 1px solid #DEDEDE!important;
      }
      
      #google-container.active { 
        height: 250px;
        margin-bottom: 20px;
      }
      
      #rickshaw-container.active { 
        height: 250px;
        width: 100%;
        
        position: relative;
        margin-bottom: 20px;
      }
      
      #rickshaw-chart {
        position: relative;
        left: 40px;
      }
      
      .rickshaw_legend span {
        color: white;
      }
      .rickshaw_legend a.action {
        color: white;
      }
      
      #y_axis {
        width: 40px;
        position: absolute;
        top: 0;
        bottom: 0;
        width: 40px;
      }
      
      .secondary-nav ul.dropdown-menu  {
        padding: 10px;
      }
      
      .secondary-nav .dropdown-menu li {
        width: 100%;
      }
      
      .secondary-nav .dropdown-menu li input {
        width: 200px;
        margin: 1px auto;
        margin-bottom: 8px;
      }
    </style>

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="images/favicon.ico">
    <link rel="apple-touch-icon" href="images/apple-touch-icon.png">
    <link rel="apple-touch-icon" sizes="72x72" href="images/apple-touch-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="114x114" href="images/apple-touch-icon-114x114.png">
    
    <script type="text/javascript" src="../resources/json2.js"></script>
    <script type="text/javascript" src="../resources/jquery.min.js"></script>
    <script type="text/javascript" src="../resources/prettify.js"></script>
    <script type="text/javascript" src="../resources/bootstrap.tabs.js"></script>
    <script type="text/javascript" src="../resources/bootstrap.dropdown.js"></script>
    <script type="text/javascript" src="../resources/jquery.placeholder.min.js"></script>
    <script type="text/javascript" src="../resources/rickshaw/d3.min.js"></script>
    <script type="text/javascript" src="../resources/rickshaw/d3.layout.min.js"></script>
    <script type="text/javascript" src="../resources/rickshaw/rickshaw.min.js"></script>
    <script type="text/javascript" src="../resources/client/splunk.js"></script>
    <script>       
      $.fn.pVal = function() {
        return this.hasClass('placeholder') ? '' : this.val();
      };

      utils = splunkjs.Utils;
      
      $(function() {
        prettyPrint();
        
        var head = $("head");
        
        var injectCode = function(code) {
          var sTag = document.createElement("script");
          sTag.type = "text/javascript";
          sTag.text = code;
          $(head).append(sTag);
          
          return sTag;
        }
        
        var getCode = function(id) {
          var code = "";
          $(id + " pre li").each(function(index, line) {
            var lineCode = "";
            $("span" ,line).each(function(index, span) {
              if ($(span).hasClass("com")) {
                lineCode += " ";
              }
              else {
                lineCode += $(span).text();
              }
            });
            
            lineCode += "\n";
            code += lineCode;
          });
          
          return code;
        }
        
        $("#rickshaw-run").click(function() {
          // Get the code except comments
          var code = getCode("#rickshaw");
          
          var tag = null;
          
          // setup the global variables we need
          done = callback = function() {
            $(tag).remove();
          };
          
          $("#rickshaw-chart").html("");
          $("#rickshaw-legend").html("");
          $("#y_axis").html("");
          $("#rickshaw-container").addClass("active");
          tag = injectCode(code);
        });
      });
    </script>
  </head>

  <body>
    <div class="topbar">
      <div class="fill">
        <div class="container-fluid">
          <a class="brand" href="#">SDK Visualization Examples</a>
        </div>
      </div>
    </div>

    <div class="container">
      
      <section id="rickshaw">
       <div class="page-header">
          <h1>
            Rickshaw <small>Using Java and JavaScript SDK</small>
          </h1>
       </div>
       <div class="row">
          <div class="span4">
            <h2>Description</h2>
            <p>
              <p><a href="http://shutterstock.github.com/rickshaw/">Rickshaw</a> is a time series visualization library built by the excellent  folks over at Shutterstock. It is built on top of the immensely powerful <a href="http://mbostock.github.com/d3/">d3</a> library,  and exposes a lot of the raw power of d3 in a more user friendly way.</p>
              <p>Much like the Google Charts example, this example shows how to create a simple line graph given a Splunk search. This graph shows the HTTP status codes broken down by minute as observed by splunkd.</p>
              <p>It might look like a lot of code, but most of it is just setting up the Rickshaw graph - so have no fear!</p>
              <p>
                <strong>Note:</strong> this example will not work in &lt= IE9 due to browser limitations.
              </p>
            </p>
          </div>
         <div class="span12">
            <h3>Code <button class="btn primary run" id="rickshaw-run">Run</button></h3>
            <div id="rickshaw-container">
              <div id="y_axis"></div>
              <div id="rickshaw-chart"></div>
              <div id="legend_container">
                <div id="smoother" title="Smoothing"></div>
                <div id="rickshaw-legend"></div>
              </div>
              <div id="slider"></div>
            </div>
            <pre class='prettyprint lang-js linenums'>
var palette = new Rickshaw.Color.Palette( { scheme: 'httpStatus' } );

var transformData = function(d) {
  var data = [];
  var statusCounts = {};

  Rickshaw.keys(d).sort().forEach( function(t) {
    Rickshaw.keys(d[t]).forEach( function(status) {
      statusCounts[status] = statusCounts[status] || [];
      statusCounts[status].push( { x: parseFloat(t), y: d[t][status] } );
    } );
  } );

  Rickshaw.keys(statusCounts).sort().forEach( function(status) {
    data.push( {
      name: status,
      data: statusCounts[status],
      color: palette.color(status)
    } );
  } );

  Rickshaw.Series.zeroFill(data);
  return data;
};

var drawChart = function(rows, fields) {  
  
  var data = {};
  
  var timeIndex = utils.indexOf(fields, "formatted_time");    
  for(var i = 0; i < rows.length; i++) {
    var row = rows[i];
    var timeValue = parseInt(row[timeIndex]);
    data[timeValue] = {};
    for(var j = 0; j < row.length; j++) {
      if (j !== timeIndex) {
        data[timeValue][fields[j]] = row[j];
      }
    }
  }

  var transformed = transformData(data);

  // Setup the graph
  var graph = new Rickshaw.Graph( {
    element: document.getElementById("rickshaw-chart"),
    series: transformed,
    renderer: 'line',
    width: 550
  });
  
  var y_ticks = new Rickshaw.Graph.Axis.Y({
    graph: graph,
    orientation: 'left',
    tickFormat: Rickshaw.Fixtures.Number.formatKMBT,
    element: document.getElementById('y_axis')
  });
  
  // Render the graph
  graph.render();

  var hoverDetail = new Rickshaw.Graph.HoverDetail( {
    graph: graph
  });

  var legend = new Rickshaw.Graph.Legend( {
    graph: graph,
    element: document.getElementById('rickshaw-legend')

  });

  var shelving = new Rickshaw.Graph.Behavior.Series.Toggle( {
    graph: graph,
    legend: legend
  });
  
  var axes = new Rickshaw.Graph.Axis.Time({
    graph: graph
  });
  axes.render();
};

// The javascript 'results' variable is set using a JSP scriptlet that runs the server side code using the Java SDK ...
// < % String searchQuery = "search index=_internal sourcetype=splunkd* | head 10000 | timechart span=1m count by status | eval formatted_time=_time | fields - NULL, _*, _time"; %>
// results = < %= MyServerSideClass.getSearchResults(searchQuery) %>;
<% String rickshawSearchQuery = "search index=_internal sourcetype=splunkd* | head 10000 | timechart span=1m count by status | eval formatted_time=_time | fields - NULL, _*, _time"; %>
results = <%= MyServerSideClass.getSearchResults(rickshawSearchQuery, "json_rows") %>;

var rows = results.rows.slice();
var fields = results.fields.slice();

var timeIndex = utils.indexOf(fields, "formatted_time");
for(var i = 0; i < rows.length; i++) {
  var row = rows[i];
  for(var j = 0; j < row.length; j++) {
    
    // Don't change the time field
    if (j !== timeIndex && fields[j] !== "_time") {
      row[j] = parseInt(row[j]);
    }
  }
}

drawChart(rows, fields);
            </pre>
          </div>
        </div>
      </section>
      
      <footer>
        <p>&copy; Splunk 2011-2012</p>
      </footer>

    </div> <!-- /container -->

  </body>
</html>
