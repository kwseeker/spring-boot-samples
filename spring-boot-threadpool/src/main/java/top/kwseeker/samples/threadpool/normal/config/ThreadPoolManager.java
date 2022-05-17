package top.kwseeker.samples.threadpool.normal.config;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolExecutor 线程池使用最佳实现
 *
 * 1 使用 ThreadPoolExecutor 的构造函数声明线程池
 *      考虑因素：避免OOM
 * 2 应该显示地给我们的线程池命名，这样有助于我们定位问题
 * 3 监测线程池运行状态
 *      任务队列容量报警
 * 4 建议不同类别的业务用不同的线程池
 * 5 根据业务合理配置参数
 * 6 做好线程池善后
 */
@Component
public class ThreadPoolManager {

    private static final int CORE_POOL_SIZE = 0;        //测试没有核心线程，没有任务时，线程池是否会自动退出
    private static final int MAX_POOL_SIZE = 4;
    private static final int KEEP_ALIVE_TIME = 20;
    private static final int QUEUE_CAPACITY = 100;
    private static final int WAIT_TERMINATED = 10;

    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
            new ThreadPoolExecutor.DiscardPolicy());

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("threadPool shutdown ..." + System.currentTimeMillis());
        threadPool.shutdown();
        try {
            boolean terminated = threadPool.awaitTermination(WAIT_TERMINATED, TimeUnit.SECONDS);
            if (!terminated) {
                List<Runnable> undoneTasks = threadPool.shutdownNow();
                //... other handle
                System.out.println("undoneTasks size: " + undoneTasks.size());
            }
            System.out.println("threadPool terminated ..." + System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
