package uk.ac.kcl.inf.smal.ttc2014.ant.timer;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * A task timing its contained tasks.
 * 
 * @author Steffen Zschaler
 */
public class Timer extends Task implements TaskContainer {

	/**
	 * A registry of timers that will output aggregate data at the end of a
	 * build.
	 * 
	 * @author Steffen Zschaler
	 */
	private static class TimerRegistry {
		public final static TimerRegistry instance = new TimerRegistry();

		private Map<String, List<Long>> timings = new HashMap<>();
		private Map<Project, Boolean> registered = new HashMap<>();

		public void registerEvents(Project p) {
			if (!registered.containsKey(p)) {
				registered.put(p, true);

				p.addBuildListener(new SubBuildListener() {
					@Override
					public void taskStarted(BuildEvent e) {
					}

					@Override
					public void taskFinished(BuildEvent e) {
					}

					@Override
					public void targetStarted(BuildEvent e) {
					}

					@Override
					public void targetFinished(BuildEvent e) {
					}

					@Override
					public void messageLogged(BuildEvent e) {
					}

					@Override
					public void buildStarted(BuildEvent e) {
					}

					@Override
					public void buildFinished(BuildEvent e) {
						printAggregateResults(System.out);
					}

					@Override
					public void subBuildFinished(BuildEvent arg0) {
						printAggregateResults(System.out);
					}

					@Override
					public void subBuildStarted(BuildEvent arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}

		protected void printAggregateResults(PrintStream out) {
			out.println("Aggregate timer results:");
			out.println("---------------------------------------------------------------------------");
			out.println("| Name                     | Minimum time   | Average time | Maximum time |");
			out.println("---------------------------------------------------------------------------");
			for (Map.Entry<String, List<Long>> timerEntry : timings.entrySet()) {
				long minimum = Long.MAX_VALUE;
				long maximum = Long.MIN_VALUE;
				long average = 0;
				for (long l : timerEntry.getValue()) {
					if (l < minimum) {
						minimum = l;
					}
					if (l > maximum) {
						maximum = l;
					}
					average += l;
				}
				average = average / timerEntry.getValue().size();
				
				out.printf("| %-24.24s | %14d | %12d | %12d |%n", timerEntry.getKey(), minimum, average, maximum);
			}
			out.println("---------------------------------------------------------------------------");
		}

		public void addTiming(Timer t, long timing) {
			registerEvents(t.getProject());
			List<Long> timerData = timings.get(t.name);

			if (timerData == null) {
				timerData = new LinkedList<>();
				timings.put(t.name, timerData);
			}

			timerData.add(timing);

			long millis = timing % 1000;
			long seconds = timing / 1000;
			long minutes = seconds / 60;
			seconds = seconds % 60;
			long hours = minutes / 60;
			minutes = minutes % 60;

			t.getProject().log(
					t,
					"Timer " + t.name + ", elapsed time: " + hours + ":"
							+ minutes + ":" + seconds + "." + millis + " ("
							+ timing + " milliseconds)", Project.MSG_INFO);
		}
	}

	private List<Task> body = new LinkedList<>();
	private String name;

	@Override
	public void addTask(Task task) {
		body.add(task);
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void execute() throws BuildException {
		getProject().log(this, "Timer " + name + " started.", Project.MSG_INFO);
		long start = System.currentTimeMillis();

		for (Task t : body) {
			t.perform();
		}

		long end = System.currentTimeMillis();

		long elapsed = end - start;

		TimerRegistry.instance.addTiming(this, elapsed);
	}
}