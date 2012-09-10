package users.nluthra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import com.splunk.Application;
import com.splunk.Args;
import com.splunk.InputCollection;
import com.splunk.InputKind;
import com.splunk.Job;
import com.splunk.Service;
import com.splunk.TcpInput;

public class MyServerSideClassTest {

	@Test
	public void test() throws Throwable {
		System.out.println("1");
		// instantiate Service and connect
		Args args = new Args();
		args.add("username", "admin");
		args.add("password", "changeme");
		args.add("host", "localhost");
		args.add("port", 8089);
		Service service = Service.connect(args);
		
		InputCollection myInputs = service.getInputs();

		TcpInput tcpInput = myInputs.create("8000", InputKind.Tcp);
		
		// specify the format for output mode
		Args outputArgs = new Args();
		outputArgs.add("output_mode", "json_rows");
		outputArgs.add("status_buckets", 300);

		// fetch search results into an InputStream and convert it to a String
		String searchQuery = "search index=_internal | head 1000 | stats count(host), count(source) by sourcetype";
		Job job = service.getJobs().create(searchQuery, outputArgs);
		while (!job.isDone()) {
			Thread.sleep(1000);
		}
		StringBuilder sb = new StringBuilder();
		int eventCount = job.getEventCount();
		int resultCount = job.getResultCount();
		int scannedCount = job.getScanCount();
		InputStream stream = job.getResults(outputArgs);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
				//System.out.println(line);
			}
		} catch (IOException e) {
			// perform appropriate exception handling
			e.printStackTrace();
		}
	}
	
	@Test
	public void testApps() {
		// instantiate Service and connect
		Args loginArgs = new Args();
		loginArgs.add("username", "admin");
		loginArgs.add("password", "changeme");
		loginArgs.add("host", "localhost");
		loginArgs.add("port", 8089);
		Service service = Service.connect(loginArgs);
		
		for (Application app : service.getApplications().values()) {
			//System.out.println(app.getName());
		}
	}

}
