<?php

require_once '/Users/nluthra/git/splunk-sdk-php/Splunk.php';
//require_once 'settings.php';

?>


<?php
error_reporting(1);

// Create an instance of Splunk_Service to connect to a Splunk server
$service = new Splunk_Service(array(
    'host' => 'localhost',
    'port' => '8089',
    'username' => 'admin',
    'password' => 'changeme',
));
$service->login();

//test($service);
//printSavedSearches($service);
//printSearchresults($service);

function test($service)
{
    echo "test function done\n";
}

function printSavedSearches($service)
{
    // Get all saved searches
    $savedSearches = $service->getSavedSearches()->items();

    foreach ($savedSearches as $element)
      {
        print_r($element->getName());
        print("\r\n");
     }
    echo "<br>";
}

function printSearchResults($service)
{
    $searchExpression = 'search index=_internal | head 2';

    // Create a normal search job
    $job = $service->getJobs()->create($searchExpression);

    print $job->getName() . "\r\n";
    
    // Wait for the job to complete, then get results
    while (!$job->isDone())
    {
        printf("Progress: %03.1f%%\r\n", $job->getProgress() * 100);
        usleep(0.5 * 1000000);
        $job->refresh();
    }
    $results = $job->getResults();

    // Process results
    foreach ($results as $result)
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
    echo "<br>";
}

?> 