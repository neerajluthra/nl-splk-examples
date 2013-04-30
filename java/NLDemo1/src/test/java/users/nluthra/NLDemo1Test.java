package users.nluthra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.splunk.Args;
import com.splunk.Event;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobArgs.ExecutionMode;
import com.splunk.JobArgs.SearchMode;
import com.splunk.JobResultsArgs;
import com.splunk.JobResultsArgs.OutputMode;
import com.splunk.JobResultsPreviewArgs;
import com.splunk.ResponseMessage;
import com.splunk.ResultsReaderCsv;
import com.splunk.ResultsReaderJson;
import com.splunk.ResultsReaderXml;
import com.splunk.SavedSearch;
import com.splunk.Service;
import com.splunk.ServiceArgs;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


public class NLDemo1Test extends TestCase {
	
	public void testOneshotWithTimeBoundaries() throws Exception {
		Service service = getService();
		Args oneshotSearchArgs = new Args();
		oneshotSearchArgs.put("earliest_time", "2013-04-10T12:00:00.000-07:00");
		oneshotSearchArgs.put("latest_time", "2013-04-20T12:00:00.000-07:00");
		oneshotSearchArgs.put("output_mode",   "csv");
		String searchQuery = "search index=_internal | head 2";
		
		System.out.println("oneshot search results without the CSV parser ...");
		InputStream results =  service.oneshotSearch(searchQuery, oneshotSearchArgs);
		BufferedReader br = new BufferedReader(new InputStreamReader(results));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.length() == 0) continue;
			System.out.println(line);
		}

		System.out.println("oneshot search results with the CSV parser ...");
		results =  service.oneshotSearch(searchQuery, oneshotSearchArgs);
		ResultsReaderCsv rr = new ResultsReaderCsv(results);
		Event event = null;
		while ((event=rr.getNextEvent()) != null) {
			System.out.println(event);
		}

	}

	public void testWriteDataSimple() throws Exception {
		Service service = getService();
		service.getReceiver().log("foo1");

	}

	public void testGetLastRunJobFromHistory() throws Exception {
		// instantiate Service and connect
		Service service = new Service("localhost", 8089);
		service.login("admin", "changeme");
		SavedSearch savedSearch = service.getSavedSearches().get("NLSS1");
		Job job = savedSearch.dispatch();
	}

	public void testSavedSearchPermissions() throws IOException,
			InterruptedException {
		Service service = new Service("localhost", 8089);
		service.login("admin", "changeme");

		SavedSearch ss = service.getSavedSearches().get("NLSS1");
		ss.setActionSummaryIndexName("foo");
		Args args = new Args();
		args.add("action.summary_index", "1");
		ss.update(args);
	}

	public void testRealtime() throws IOException, InterruptedException {
		Service service = new Service("localhost", 8089);
		service.login("admin", "changeme");

		Job job = null;
		JobArgs jobArgs = new JobArgs();
		jobArgs.setExecutionMode(ExecutionMode.NORMAL);
		jobArgs.setEarliestTime("rt-1m");
		jobArgs.setLatestTime("rt");
		jobArgs.setSearchMode(SearchMode.REALTIME);
		jobArgs.setStatusBuckets(300);

		job = service.search("search index=_internal", jobArgs);

		while (!job.isReady()) {
			Thread.sleep(500);
		}
		System.out.println("Is realtime search? " + job.isRealTimeSearch());

		JobResultsPreviewArgs resultsArgs = new JobResultsPreviewArgs();
		resultsArgs.setCount(2000);

		while (true) {
			InputStream stream = job.getResultsPreview(resultsArgs);
			String line = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(
					stream, "UTF-8"));
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
			stream.close();
			Thread.sleep(500);
		}
	}

	public void testSearchWithArguments() throws IOException,
			InterruptedException {
		Service service = new Service("localhost", 8089);
		service.login("admin", "changeme");

		Job job = null;
		// search query for NL1 is
		// "index=_internal earliest=$args.mysourcetype$"
		SavedSearch savedSearch = service.getSavedSearches().get("NL1");
		Args dispatchArgs = new Args();
		// dispatchArgs.add("dispatch.earliest_time", "-120m@m");
		// dispatchArgs.add("span", "5min");
		dispatchArgs.add("args.earliest", "-10m@m");
		job = savedSearch.dispatch(dispatchArgs);

		while (!job.isDone()) {
			Thread.sleep(500);
		}
		Map<String, Object> outputArgs = new HashMap<String, Object>();
		outputArgs.put("count", 0);// To get more than 100 results
		outputArgs.put("output_mode", "json");

		InputStream stream = job.getResults(outputArgs);
		System.out.println(job.getResultCount());
	}

	public void testbasicAuthTest() throws InterruptedException {
		Service service = new Service("localhost", 8089);
		String creds = "admin:changeme";
		String basicAuthHeader = Base64.encode(creds.getBytes());
		service.setToken("Basic " + basicAuthHeader);
		System.out.println(service.getSavedSearches().size());
		Job job = service.getJobs().create(
				"search index=oidemo | stats count by mdn");
		Thread.sleep(3000);
		System.out.println("job created" + job.getSid());
		if (!job.isReady()) {
			job.cancel();
			System.out.println("job cancelled" + job.getSid());
		} else {
			System.out.println("job already ready");
		}
	}

	public void testTag() throws IOException {
		Service service = new Service("localhost", 8089);
		service.login("admin", "changeme");
		Args args = new Args();
		args.add("add", "my-tag-name");
		args.add("value", "my-tag-value");
		ResponseMessage responseAddTag = service.post(
				"search/fields/host/tags", args);
		// TODO: check for errors, and response code 200 etc.

		// now lets verify by listing all tags on the field
		ResponseMessage responseListTags = service
				.get("search/fields/host/tags");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				responseListTags.getContent(), "UTF8"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
	}

	public void test() throws Exception {
		// instantiate Service and connect
		Args args = new Args();
		args.add("username", "admin");
		args.add("password", "changeme");
		args.add("host", "localhost");
		args.add("port", 8089);
		Service service = Service.connect(args);

		service.getSavedSearches().remove("my_saved_search");

		SavedSearch ss = service.getSavedSearches().create("my_saved_search",
				"index=_internal | stats count by sourcetype");
		Job job = ss.dispatch();
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		InputStream results = job.getResultsPreview();

		ResultsReaderXml resultsReader = new ResultsReaderXml(results);
		Event event = null;
		while ((event = resultsReader.getNextEvent()) != null) {
			for (String key : event.keySet()) {
				System.out.println(key + ":" + event.get(key));
			}
		}
	}

	private void readBuffered(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		InputStream results = job.getResults(new Args("output_mode", "json"));

		BufferedReader br = new BufferedReader(new InputStreamReader(results));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	private void readXML(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}

		InputStream results = job.getResults();

		ResultsReaderXml resultsReader = new ResultsReaderXml(results);
		Event event = null;
		while ((event = resultsReader.getNextEvent()) != null) {
			for (String key : event.keySet()) {
				System.out.println(key + ":" + event.get(key));
			}
		}
	}

	private void readJSON(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}

		InputStream results = job.getResults(new Args("output_mode", "json"));

		ResultsReaderJson resultsReader = new ResultsReaderJson(results);
		Event event = null;
		while ((event = resultsReader.getNextEvent()) != null) {
			for (String key : event.keySet()) {
				System.out.println(key + ":" + event.get(key));
			}
		}
	}

	private Service getService() {
		ServiceArgs args = new ServiceArgs();
		args.setUsername("admin");
		args.setPassword("changeme");
		// Service service = Service.connect(args);
		Service service = new Service("localhost", 8089);
		service.setToken("Basic " + Base64.encode("admin:changeme".getBytes()));
		return service;
	}
}
