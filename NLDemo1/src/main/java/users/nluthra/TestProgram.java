package users.nluthra;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.splunk.Args;
import com.splunk.Job;
import com.splunk.Service;

public class TestProgram {

	public static String getSearchResults(String query) throws Throwable {
	    
	    // service connection set up
		Args loginArgs = new Args();
		loginArgs.add("username", "admin");
		loginArgs.add("password", "changeme");
		loginArgs.add("host", "localhost");
		loginArgs.add("port", 8089);
		
		Service service = Service.connect(loginArgs);
		
		// specify the format for output mode
		Map<String, String> outputArgs = new HashMap<String, String>();
		outputArgs.put("output_mode", "json_cols");

		// convert the job results' InputStream to a JSON String
		InputStream stream = service.oneshot(query, outputArgs);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		return sb.toString();
	}

	public static String getOneshotSearchResults(String query) throws Throwable {
		
		// service connection set up
		Args loginArgs = new Args();
		loginArgs.add("username", "admin");
		loginArgs.add("password", "changeme");
		loginArgs.add("host", "localhost");
		loginArgs.add("port", 8089);
		
		Service service = Service.connect(loginArgs);
		
		// specify the format for output mode
		Map<String, String> outputArgs = new HashMap<String, String>();
		outputArgs.put("output_mode", "json_rows");

		// convert the job results' InputStream to a JSON String
		InputStream stream = service.oneshot(query, outputArgs);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		return sb.toString();
	}
}
