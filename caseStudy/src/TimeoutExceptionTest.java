import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TimeoutExceptionTest {
	public static void main(String[] args) {
		Callable<Integer> cn = new CountNum(0);
		// FutureTask<Integer> 这里的表示返回的是Integer
		FutureTask<Integer> ft = new FutureTask<Integer>(cn);
		Thread td = new Thread(ft);
		System.out.println("futureTask开始执行计算:" + System.currentTimeMillis());
		td.start();
		System.out.println("main 主线程可以做些其他事情:" + System.currentTimeMillis());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			// futureTask的get方法会阻塞，知道可以取得结果为止
			Integer result = ft.get(2000, TimeUnit.MILLISECONDS);
			System.out.println("计算的结果是:" + result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.err.println("");
		}
		System.out.println("取得分支线程执行的结果后，主线程可以继续处理其他事项");
	}

}

class CountNum implements Callable<Integer> {
	private Integer sum;

	public CountNum(Integer sum) {
		this.sum = sum;
	}

	public Integer call() throws Exception {
		for (int i = 0; i < 100; i++) {
			sum = sum + i;
		}
		// 休眠5秒钟，观察主线程行为，预期的结果是主线程会继续执行，到要取得FutureTask的结果是等待直至完成。
		Thread.sleep(3000);
		System.out.println("futureTask 执行完成" + System.currentTimeMillis());
		return sum;
	}
}
