import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class FutureTaskAndExecutor {

	public static void main(String[] args) {
		List<FutureTask<Integer>> list = new ArrayList<FutureTask<Integer>>();
		// 创建线程池，线程池的大小和List.size没有啥必然的关系，一般的原则是<=list.size,多出来浪费不好
		ExecutorService exec = Executors.newFixedThreadPool(5);
		for (int i = 10; i < 20; i++) {
			// 创建对象
			FutureTask<Integer> ft = new FutureTask<Integer>(new GetSum(i));
			// 添加到list,方便后面取得结果
			list.add(ft);
			// 一个个提交给线程池，当然也可以一次性的提交给线程池，exec.invokeAll(list);
			exec.submit(ft);
		}

		// 开始统计结果
		Integer total = 0;
		for (FutureTask<Integer> tempFt : list) {
			try {
				total = total + tempFt.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		// 处理完毕，一定要记住关闭线程池，这个不能在统计之前关闭，因为如果线程多的话,执行中的可能被打断
		exec.shutdown();
		System.out.println("多线程计算后的总结果是:" + total);

	}
}

/**
 * 这个类很简单，就是统计下简单的加法（从1 到total)
 * 
 * @author Administrator
 * 
 */
class GetSum implements Callable<Integer> {
	private Integer total;
	private Integer sum = 0;

	public GetSum(Integer total) {
		this.total = total;
	}

	public Integer call() throws Exception {
		for (int i = 1; i < total + 1; i++) {
			sum = sum + i;
		}
		System.out.println(Thread.currentThread().getName() + " sum:" + sum);
		return sum;
	}
}
