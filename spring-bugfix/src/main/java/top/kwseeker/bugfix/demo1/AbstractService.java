package top.kwseeker.bugfix.demo1;

import org.springframework.context.ApplicationContext;
import top.kwseeker.bugfix.demo1.annotation.ActivityCondition;
import top.kwseeker.bugfix.demo1.condition.ConditionChain;
import top.kwseeker.bugfix.demo1.condition.ICondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AbstractService implements IService {

    // actId(1) -> ConditionType(n) -> ICondition(n)
    public static final Map<Integer, Map<ConditionType, List<ICondition>>> conditionMapCache = new HashMap<>();
    public static final boolean REPEAT_PARTAKE_NO_LIMIT = false;

    protected int activityId;
    protected String activityName;
    protected boolean limitRepeatPartake;
    protected ConditionChain conditionChain;

    public AbstractService(int activityId, String activityName, ApplicationContext applicationContext) {
        this(activityId, activityName, REPEAT_PARTAKE_NO_LIMIT, applicationContext);
    }

    public AbstractService(int activityId, String activityName, boolean limitRepeatPartake, ApplicationContext applicationContext) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.limitRepeatPartake = limitRepeatPartake;

        if (conditionMapCache.isEmpty()) {
            Map<String, ICondition> conditionMap = applicationContext.getBeansOfType(ICondition.class);
            Map<Integer, Map<ConditionType, List<ICondition>>> conditionMaps = conditionMap.values().stream()
                    .collect(Collectors.groupingBy(activityCondition -> {
                        ActivityCondition ac = activityCondition.getClass().getAnnotation(ActivityCondition.class);
                        return ac.id();
                    }, Collectors.groupingBy(activityCondition -> {
                        ActivityCondition ac = activityCondition.getClass().getAnnotation(ActivityCondition.class);
                        return ac.condType();
                    })));
            conditionMapCache.putAll(conditionMaps);
        }

        this.conditionChain = new ConditionChain();

        if (conditionMapCache.get(activityId) != null) {
            for (Map.Entry<ConditionType, List<ICondition>> entry : conditionMapCache.get(activityId).entrySet()) {
                this.conditionChain.compose(entry.getValue().get(0));
            }
        }
    }

    @Override
    public boolean checkCondition() {
        return conditionChain.checkCondition();
    }
}
