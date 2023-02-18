package top.kwseeker.web.pressure;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/greet")
public class GreetController {
    @GetMapping("/hi")
    public String greet() {
        Random random = ThreadLocalRandom.current();
        int costTime = random.nextInt(500) + 100;
        return "Hello, cost:" + costTime;
    }
}
