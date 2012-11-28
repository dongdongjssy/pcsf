import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskTest {

	public void startTask() {
		Timer timer = new Timer();
		TimerTask taskSubmitReques = new RegisterRequest(0);

		timer.schedule(taskSubmitReques, 2000, 1000);

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			System.out.println(i);
			i++;
		}
	}
}
