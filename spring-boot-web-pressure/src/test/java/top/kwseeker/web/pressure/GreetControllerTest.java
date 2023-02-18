package top.kwseeker.web.pressure;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

class GreetControllerTest {

    @Test
    public void testThreadLocalRandom() throws InterruptedException {
        new Thread(() -> {
            //ThreadLocalRandom依赖SecurityRandom生成真随机种子，因此ThreadLocalRandom不需要担心两个线程生成两组相同的随机数
            ThreadLocalRandom random = ThreadLocalRandom.current();
            System.out.println(random.nextInt(100));
        }).start();
        new Thread(() -> {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            System.out.println(random.nextInt(100));
        }).start();
        Thread.sleep(100);
    }
}