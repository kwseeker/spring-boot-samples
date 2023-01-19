package top.kwseeker.samples.valuebug;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
//@PropertySource("classpath:application.yml")
public class SomeBean {
    //官方的定义的属性，注入成功
    @Value("${spring.application.name}")
    private String appName;
    //自定义的属性，注入失败
    @Value("${rateLimiter.on}")
    //@Value("${rateLimiter}")
    private String on;
}
