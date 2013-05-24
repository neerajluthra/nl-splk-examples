package users.nluthra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.splunk.Args;
import com.splunk.CollectionArgs;
import com.splunk.Event;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobArgs.ExecutionMode;
import com.splunk.JobArgs.SearchMode;
import com.splunk.JobCollection;
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

	public void test() throws Exception {
		// instantiate Service and connect
		Service service = getService();
		CollectionArgs args = new CollectionArgs();
		args.put("count", "2");
		JobCollection jobs = service.getJobs(args);
		System.out.println("Job count:" + jobs.size());
		Job job = jobs.create("search index=_internal | head 2");
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		// readXML(job);
	}

	public void testOneshotWithTimeBoundaries() throws Exception {
		Service service = getService();
		Args oneshotSearchArgs = new Args();
		oneshotSearchArgs.put("earliest_time", "2013-04-10T12:00:00.000-07:00");
		oneshotSearchArgs.put("latest_time", "2014-04-20T12:00:00.000-07:00");
		oneshotSearchArgs.put("output_mode", "csv");
		String searchQuery = "search index=_internal | head 2";

		System.out.println("oneshot search results without the CSV parser ...");
		InputStream stream = service.oneshotSearch(searchQuery,
				oneshotSearchArgs);
		readBuffered(stream);

		System.out.println("oneshot search results with the CSV parser ...");
		stream = service.oneshotSearch(searchQuery, oneshotSearchArgs);
		readCSV(stream);
	}

	public void testWriteDataSimple() throws Exception {
		Service service = getService();
		service.getReceiver().log("foo1");
	}

	public void testGetLastRunJobFromHistory() throws Exception {
		Service service = getService();
		SavedSearch ss = service.getSavedSearches().get("ODBCDemoSavedSearch");
		if (ss.history().length > 0) {
			readBuffered(ss.history()[0]);
		} else {
			readBuffered(ss.dispatch());
		}
	}

	public void testRealtime() throws IOException, InterruptedException {
		Service service = getService();

		JobArgs jobArgs = new JobArgs();
		jobArgs.setExecutionMode(ExecutionMode.NORMAL);
		jobArgs.setEarliestTime("rt-1m");
		jobArgs.setLatestTime("rt");
		jobArgs.setSearchMode(SearchMode.REALTIME);
		jobArgs.setStatusBuckets(300);

		Job job = service.search("search index=_internal", jobArgs);

		while (!job.isReady()) {
			Thread.sleep(500);
		}
		System.out.println("Is realtime search? " + job.isRealTimeSearch());

		JobResultsPreviewArgs resultsArgs = new JobResultsPreviewArgs();
		resultsArgs.setCount(2000);

		while (true) {
			InputStream stream = job.getResultsPreview(resultsArgs);
			readBuffered(stream);
			Thread.sleep(500);
		}
	}

	public void testSearchWithArguments() throws IOException,
			InterruptedException {
		Service service = getService();

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
		readJSON(stream);
	}

	public void testTag() throws IOException {
		Service service = getService();
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

	private void readBuffered(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		InputStream stream = job.getResults(new Args("output_mode", "json"));
		readBuffered(stream);
	}

	private void readBuffered(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	private void readXML(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		readXML(job.getResults());
	}

	private void readXML(InputStream stream) throws IOException {
		ResultsReaderXml resultsReader = new ResultsReaderXml(stream);
		Event event = null;
		while ((event = resultsReader.getNextEvent()) != null) {
			for (String key : event.keySet()) {
				System.out.println(key + ":" + event.get(key));
			}
		}
	}

	private void readCSV(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		InputStream stream = job.getResults(new Args("output_mode", "json"));
		readCSV(stream);
	}

	private void readCSV(InputStream stream) throws IOException {
		ResultsReaderCsv rr = new ResultsReaderCsv(stream);
		Event event = null;
		while ((event = rr.getNextEvent()) != null) {
			System.out.println(event);
		}
	}

	private void readJSON(Job job) throws IOException, InterruptedException {
		while (!job.isDone()) {
			Thread.sleep(500);
		}
		InputStream stream = job.getResults(new Args("output_mode", "json"));
		readJSON(stream);
	}

	private void readJSON(InputStream stream) throws IOException {
		ResultsReaderJson resultsReader = new ResultsReaderJson(stream);
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
