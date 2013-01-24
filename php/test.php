#!/usr/bin/php

<?php

require_once '../Splunk.php';
//require_once 'settings.php';

?>


<?php
$ns = Splunk_Namespace::createApp('NL1Test');
$service = new Splunk_Service(array(
    'host'      => 'localhost',
    'port'      => '8089',
    'username'  => 'admin',
    'password'  => 'changeme',
    'namespace' => $ns,
));

$service->login();

// Get all saved searches
$savedSearches = $service->getSavedSearches()->items(array(
  'namespace' => Splunk_Namespace::createUser(NULL, NULL),     // all owners, all apps
));

//foreach ($savedSearches as $savedSearch)
  //{
  //  print_r($savedSearch->getName());
 //}

//$savedSearch = $service->getSavedSearches()->get('NL1TestSearch1', $ns);
$savedSearch = $service->getSavedSearches()->get('NL1TestSearch1');
//print($savedSearch['search']);

$foo = Splunk_Namespace::createApp('foo');
$job = $savedSearch->dispatch(array('namespace' => 'foo'));
while (!$job->isDone())
{
   usleep(0.5 * 1000000);
    $job->reload();
}
$results = $job->getResults();
//print_r($results);
?> 