package top.kwseeker.boot.transaction;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class TransactionApplication implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
        MultiThreadTransactionService service = applicationContext.getBean(MultiThreadTransactionService.class);
        service.multiThreadOperate();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TransactionApplication.applicationContext = applicationContext;
    }
}
