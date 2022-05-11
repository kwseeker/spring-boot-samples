package top.kwseeker.bugfix.demo1.condition;

import org.springframework.stereotype.Component;
import top.kwseeker.bugfix.demo1.ConditionType;
import top.kwseeker.bugfix.demo1.annotation.ActivityCondition;

@Component
@ActivityCondition(id = 3, condType = ConditionType.WHITELIST)
public class WhiteListCondition implements ICondition {

    @Override
    public boolean checkCondition() {
        return true;
    }
}
