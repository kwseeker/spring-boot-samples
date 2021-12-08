package top.kwseeker.samples.async.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.kwseeker.samples.async.model.User;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class GitHubLookupService {

    private final RestTemplate restTemplate;

    public GitHubLookupService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        log.info("Looking up " + user);
        //String url = String.format("https://api.github.com/users/%s", user);
        //User results = restTemplate.getForObject(url, User.class);
        User results = new User(user, "https://" + user + ".io");
        if ("Spring-Projects".equals(user)) {
            throw new RuntimeException("模拟的异常");
        }
        Thread.sleep(1000L);
        return CompletableFuture.completedFuture(results);
    }

}
