package top.kwseeker.samples.gracefulshutdown.hook;

/**
 * 程序正常退出（最后一个非守护线程退出）
 * 调用System.exit()
 * 终端使用Ctrl+C触发的中断（SIGINT）
 * 用户注销，系统关闭
 * 使用Kill pid命令关闭进程（kill -9 不会触发关闭钩子执行而是立即关闭进程）
 */
public class JVMShutdownHook {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Exec shutdown hook 1, then exec release!")));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Exec shutdown hook 2, then exec release!")));
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Exec shutdown hook 3, then exec release!");
            }
        }));
        System.out.println("main done!");
    }
}
