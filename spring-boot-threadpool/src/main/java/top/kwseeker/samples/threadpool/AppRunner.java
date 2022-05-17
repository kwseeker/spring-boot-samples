package top.kwseeker.samples.threadpool;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.kwseeker.samples.threadpool.normal.config.ThreadPoolWrapper;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class AppRunner implements CommandLineRunner {

    @Resource
    private ThreadPoolWrapper wrapper;

    //private final ThreadPoolManager threadPoolManager;
    //
    //public AppRunner(ThreadPoolManager threadPoolManager) {
    //    this.threadPoolManager = threadPoolManager;
    //}

    @Override
    public void run(String... args) throws Exception {
        //ThreadPoolExecutor executor = threadPoolManager.getThreadPool();
        ThreadPoolExecutor executor = wrapper.getThreadPool();
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("task1 done ...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        executor.execute(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("task2 done ...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        printInfo(executor);
        Thread.sleep(1500);
        printInfo(executor);

        Thread.sleep(25000);
        System.out.println("all task should be done and timeout!");
        printInfo(executor);

        executor.execute(() -> {
            try {
                Thread.sleep(30000);
                System.out.println("task3 done ... " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                System.out.println("Thread has been interrupted ...");
                e.printStackTrace();
            }
        });

        Thread.sleep(3000);
        System.out.println("AppRunner exit ... " + System.currentTimeMillis());
    }

    void printInfo(ThreadPoolExecutor executor) {
        System.out.println("---------------------------------------->");
        System.out.println("corePoolSize: " + executor.getCorePoolSize());
        System.out.println("poolSize: " + executor.getPoolSize());
        System.out.println("activeCount: " + executor.getActiveCount());
        System.out.println("completedTaskCount: " + executor.getCompletedTaskCount());
        System.out.println("isShutdown: " + executor.isShutdown());
        System.out.println("isTerminated: " + executor.isTerminated());
    }
}