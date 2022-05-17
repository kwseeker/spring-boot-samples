package top.kwseeker.samples.threadpool.normal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadPoolConfig {

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolWrapper getThreadPoolWrapper() {
        return new ThreadPoolWrapper();
    }
}
