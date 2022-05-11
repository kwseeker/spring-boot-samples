package top.kwseeker.bugfix.demo1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class StartupRunner implements CommandLineRunner {

    @Resource
    private IService service;

    @Override
    public void run(String... args) throws Exception {
        boolean ret = service.checkCondition();
        System.out.println(ret);
    }
}