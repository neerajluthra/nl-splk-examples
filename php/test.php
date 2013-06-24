#!/usr/bin/php

<?php
require_once '/Users/nluthra/git/splunk-sdk-php/Splunk.php';
?>


<?php
$service = new Splunk_Service(array(
    'host'      => 'localhost',
    'port'      => '8089',
    'username'  => 'admin',
    'password'  => 'changeme',
));

$service->login();
print_r($service->getToken());

$searchQueryOneshot = 'search index=_internal'; // Return the first 100 events

// Set the search parameters; specify a time range
$searchParams = array(
    'count' => 30,
    'offset' => 100000,
    'earliest_time' => '2012-06-20T12:00:00.000-07:00', 
    'latest_time' => '2013-12-02T12:00:00.000-07:00'
);

// Run a oneshot search that returns the job's results
$resultsStream = $service->oneshotSearch($searchQueryOneshot, $searchParams);
$resultsOneshotSearch = new Splunk_ResultsReader($resultsStream);

// Use the built-in XML parser to display the job results
foreach ($resultsOneshotSearch as $result)
  {
    if ($result instanceof Splunk_ResultsFieldOrder)
    {
      // Process the field order
      print "FIELDS: " . implode(',', $result->getFieldNames()) . "\r\n";
    }
    else if ($result instanceof Splunk_ResultsMessage)
    {
      // Process a message
      print "[{$result->getType()}] {$result->getText()}\r\n";
    }
    else if (is_array($result))
    {
      // Process a row
      print "{\r\n";
      foreach ($result as $key => $valueOrValues)
        {
          if (is_array($valueOrValues))
            {
              $values = $valueOrValues;
              $valuesString = implode(',', $values);
              print "  {$key} => [{$valuesString}]\r\n";
            }
          else
            {
              $value = $valueOrValues;
              print "  {$key} => {$value}\r\n";
            }
        }
      print "}\r\n";
    }
    else
    {
      // Ignore unknown result type
    }
  }

?> 