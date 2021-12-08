package top.kwseeker.samples.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.kwseeker.samples.async.model.User;
import top.kwseeker.samples.async.service.GitHubLookupService;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class AppRunner implements CommandLineRunner {

    private final GitHubLookupService gitHubLookupService;

    public AppRunner(GitHubLookupService gitHubLookupService) {
        this.gitHubLookupService = gitHubLookupService;
    }

    /**
     * 如何实现
     * 1） A组先执行 B组后执行
     * 2） 异常重试
     * 3）
     */
    @Override
    public void run(String... args) throws Exception {
        // Start the clock
        long start = System.currentTimeMillis();

        //CompletableFuture<User> page1 = CompletableFuture.runAsync(user -> gitHubLookupService.findUser(user));
        CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
        CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
        //CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");    //如果抛异常
        CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects")
                .handle((ret, ex) -> ret != null ? ret : new User());                         //加上异常处理

        // Wait until they are all done
        CompletableFuture.allOf(page1, page2, page3).join();
        //1）
        //CompletableFuture<Void> allCf = CompletableFuture.allOf(page1, page2, page3);
        //allCf.join();
        //2）
        //allCf.whenComplete((ret, ex) -> {
        //    //System.out.println(ex.getMessage() + ex.getCause());
        //});
        //3）异常处理
        //allCf.exceptionally();
        //allCf.handle()
        //Stream.of(page1, page2, page3)
        ////Stream.of(page1, page2)
        //        .map(userCompletableFuture -> {
        //            User ret = userCompletableFuture.join();
        //            System.out.println(ret.toString());
        //            return ret.toString();
        //        }).collect(Collectors.toList());


        CompletableFuture<User> page4 = gitHubLookupService.findUser("kwseeker");

        // Print results, including elapsed time
        log.info("Elapsed time: " + (System.currentTimeMillis() - start));
        log.info("--> " + page1.get());
        log.info("--> " + page2.get());
        log.info("--> " + page3.get());
        log.info("--> " + page4.get());

        Thread.sleep(100000);
    }
}
