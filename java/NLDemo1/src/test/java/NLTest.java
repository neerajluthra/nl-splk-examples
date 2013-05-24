import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.splunk.Args;
import com.splunk.Job;
import com.splunk.JobArgs;
import com.splunk.JobResultsArgs;
import com.splunk.JobResultsArgs.OutputMode;
import com.splunk.ResultsReaderJson;
import com.splunk.Service;

public class NLTest {

	public static void main(String[] args) {
		try {
			Service service = new Service("demos.splunk.com", 8089);
			service.login("admin", "sk8free");
			JobArgs jobArgs = new JobArgs();
			jobArgs.setExecutionMode(JobArgs.ExecutionMode.NORMAL);
			jobArgs.setLatestTime("now");
			jobArgs.setEarliestTime("-15m");
			Job job = service.getJobs().create(
					"search index=_internal kjhgkjhg", jobArgs);
			while (!job.isDone()) {
				Thread.sleep(500);
			}
			System.out.println(job.getResultCount());
			JobResultsArgs jobResultsArgs = new JobResultsArgs();
			jobResultsArgs.setOutputMode(OutputMode.JSON_ROWS);
			InputStream results = job.getResults(jobResultsArgs);
			/*
			 * BufferedReader br = new BufferedReader(new
			 * InputStreamReader(results)); String line = null; while ((line =
			 * br.readLine()) != null) { System.out.println(line); }
			 */
			ResultsReaderJson rr = new ResultsReaderJson(results);
			HashMap<String, String> event;
			while ((event = rr.getNextEvent()) != null) {
				for (String key : event.keySet()) {
					System.out.println(key + ":" + event.get(key));
				}
			}
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
	}
}
