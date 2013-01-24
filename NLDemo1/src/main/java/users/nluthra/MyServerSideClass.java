package users.nluthra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.splunk.Application;
import com.splunk.Args;
import com.splunk.Service;

/**
 * @author nluthra
 *
 */
public class MyServerSideClass {

	public static String getSearchResults(String query, String outputMode) {
		// instantiate Service and connect
		Args args = new Args();
		args.add("username", "admin");
		args.add("password", "changeme");
		args.add("host", "localhost");
		args.add("port", 8089);
		Service service = Service.connect(args);
		
		// specify the format for output mode
		Args outputArgs = new Args();
		outputArgs.put("output_mode", outputMode);

		// fetch search results into an InputStream and convert it to a String
		InputStream stream = service.oneshotSearch(query, outputArgs);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// perform appropriate exception handling
			e.printStackTrace();
		}
		
		return sb.toString();
	}

	public static List<String> getAppNames() throws Throwable {
		
		// instantiate Service and connect
		Args loginArgs = new Args();
		loginArgs.add("username", "admin");
		loginArgs.add("password", "changeme");
		loginArgs.add("host", "localhost");
		loginArgs.add("port", 8089);
		
		Service service = Service.connect(loginArgs);
		List<String> appNames = new ArrayList<String>();
		for (Application app : service.getApplications().values()) {
			appNames.add(app.getName());
		}
		return appNames;
	}
}
