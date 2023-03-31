package top.kwseeker.web.pressure;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.kwseeker.web.pressure.common.ApiRequest;
import top.kwseeker.web.pressure.common.ApiResponse;
import top.kwseeker.web.pressure.common.UserReq;
import top.kwseeker.web.pressure.common.UserRes;

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

    @GetMapping("/user")
    public ApiResponse<UserRes> user(@RequestBody @Validated ApiRequest<UserReq> req) {
        return ApiResponse.buildSuccess(new UserRes(req.getData().getUserId(), "Arvin"));
    }
}
