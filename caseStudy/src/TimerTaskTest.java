import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskTest {

	public void startTask() {
		Timer timer = new Timer();
		TimerTask taskSubmitReques = new TaskSubmitRequest();
		TimerTask regRequests = new RegisterRequest(1);

		timer.schedule(taskSubmitReques, 2000, 3000);
		timer.schedule(regRequests, 4000, 1000);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timer.cancel();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TimerTaskTest().startTask();
	}

	class RegisterRequest extends TimerTask {
		private int i;

		public RegisterRequest(int i) {
			this.i = i;
		}

		@Override
		public void run() {
			System.out.println(i);
			i++;
		}
	}

	class TaskSubmitRequest extends TimerTask {
		@Override
		public void run() {
			System.out.println("[Task Submit Request!]");
		}

	}
}
