package top.kwseeker.samples.valuebug;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:application.yml")
public class SomeBean {

    @Value("${rateLimiter.on}")
    private Boolean on;
}
