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
import com.splunk.ResponseMessage;
import com.splunk.ResultsReaderXml;
import com.splunk.SavedSearch;
import com.splunk.Service;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class MyServerSideClassTest extends TestCase {

	public void testSearchWithArguments() throws IOException, InterruptedException {
		Service service = new Service("localhost", 8089);
		service.login("admin", "changeme");
		
		Job job = null;
		// search query for NL1 is "index=_internal sourcetype=$args.mysourcetype$"
		SavedSearch savedSearch = service.getSavedSearches().get("NL1");
		Args dispatchArgs = new Args();
		dispatchArgs.add("dispatch.earliest_time", "-20m@m");
		dispatchArgs.add("latest", "now");
		dispatchArgs.add("span", "5min");
		dispatchArgs.add("args.mysourcetype", "splunkd");
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
		args.add("add", "decommissioned");
		args.add("value", "njrarltcdd0008.ams1907.com");
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
		args.add("password", "sk8free");
		args.add("host", "demos.splunk.com");
		args.add("port", 8089);
		Service service = Service.connect(args);

		service.getSavedSearches().remove("my_saved_search");

		SavedSearch ss = service.getSavedSearches().create("my_saved_search",
				"index=internal | head 2");
		Job job = ss.dispatch();
		while (!job.isReady()) {
			Thread.sleep(1000);
		}

		// readXML(job);
		// readBuffered(job);
	}

	private void readBuffered(Job job) throws IOException {
		InputStream results = job.getResults();

		BufferedReader br = new BufferedReader(new InputStreamReader(results));
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	private void readXML(Job job) throws IOException {
		InputStream results = job.getResultsPreview();

		ResultsReaderXml resultsReader = new ResultsReaderXml(results);
		Event event = null;
		while ((event = resultsReader.getNextEvent()) != null) {
			for (String key : event.keySet()) {
				System.out.println(key + ":" + event.get(key));
			}
		}
	}
}
