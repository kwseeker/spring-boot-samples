package top.kwseeker.web.pressure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1 常规用法 与 @Async 对比
 * 2 展示@Async封装原理
 */
@SpringBootApplication
public class WebPressureTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebPressureTestApplication.class, args);
    }
}
