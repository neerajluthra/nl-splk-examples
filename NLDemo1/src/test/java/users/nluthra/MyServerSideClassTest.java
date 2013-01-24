package users.nluthra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import com.splunk.Args;
import com.splunk.Event;
import com.splunk.Job;
import com.splunk.ResultsReaderXml;
import com.splunk.SavedSearch;
import com.splunk.Service;

public class MyServerSideClassTest extends TestCase {

	public void test() throws Exception {
		// instantiate Service and connect
		Args args = new Args();
		args.add("username", "admin");
		args.add("password", "changeme");
		args.add("host", "localhost");
		args.add("port", 8089);
		Service service = Service.connect(args);
		
		service.getSavedSearches().remove("my_saved_search");

		SavedSearch ss = service.getSavedSearches().create("my_saved_search", "search index=_internal | head 2");
		Job job = ss.dispatch();
		while (!job.isReady()) {
			Thread.sleep(1000);
		}

		//readXML(job);
		readBuffered(job);
	}

	
	private void readBuffered(Job job) throws IOException {
		InputStream results = job.getResults();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(results));
		String line = null;
		while ((line=br.readLine()) != null) {
			System.out.println(line);
		}
	}
	
	private void readXML(Job job) throws IOException {
		InputStream results = job.getResultsPreview();
		
		ResultsReaderXml resultsReader = new ResultsReaderXml(results);
		Event event = null;
		while ((event=resultsReader.getNextEvent()) != null) {
		    for (String key: event.keySet()) {
		        System.out.println(key + ":" + event.get(key));
		    }
		}
	}
}
