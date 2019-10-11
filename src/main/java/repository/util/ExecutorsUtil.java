package repository.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorsUtil {

	private int corePoolSize = 3;

	private int maximumPoolSize = 5;

	private long keepAliveTime = 10;

	private ThreadPoolExecutor threadPoolExecutor;

	public ExecutorsUtil() {
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public ExecutorsUtil(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
		this.corePoolSize = corePoolSize;
		this.maximumPoolSize = maximumPoolSize;
		this.keepAliveTime = keepAliveTime;
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public void execute(Runnable task) {
		threadPoolExecutor.execute(task);
	}

	public void shutdown(Runnable task) throws InterruptedException {
		threadPoolExecutor.shutdown();
		while (!threadPoolExecutor.awaitTermination(keepAliveTime, TimeUnit.SECONDS))
			;
		task.run();
	}

}
