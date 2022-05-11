package top.kwseeker.bugfix.demo1;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class BizService extends AbstractService {

    public BizService(ApplicationContext applicationContext) {
        super(3, "some service", applicationContext);
        //super(3, "some service", false, applicationContext);
    }
}
