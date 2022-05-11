package top.kwseeker.bugfix.demo1.condition;

import java.util.ArrayList;
import java.util.List;

public class ConditionChain implements ICondition {

    private List<ICondition> conditionChain = new ArrayList<>();

    public void compose(ICondition condition) {
        conditionChain.add(condition);
    }

    @Override
    public boolean checkCondition() {
        for (ICondition condition : conditionChain) {
            boolean ret = condition.checkCondition();
            if (!ret) {
                return false;
            }
        }
        return true;
    }
}
