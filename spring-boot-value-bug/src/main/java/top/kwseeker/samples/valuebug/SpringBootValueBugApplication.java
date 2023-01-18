package top.kwseeker.samples.valuebug;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class SpringBootValueBugApplication implements ApplicationContextAware {

    private static ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootValueBugApplication.class, args);
        SomeBean bean = context.getBean(SomeBean.class);
        System.out.println("rateLimiter.on: " + bean.getOn());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
