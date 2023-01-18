//package top.kwseeker.samples.valuebug;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@Getter
//@Component
//public class RateLimiter {
//
//    public static final int REQUEST_COUNT_MAX_TIME = 5; //5s
//    public static final int REQUEST_COUNT_RECORD_OVERTIME = 2 * 3600; //2小时，防止反复创建Counter
//    public static final int REQUEST_MAX_COUNT_PER_SECOND = 5;
//    public static final int REQUEST_MAX_COUNT_PER_5_SECONDS = 10;
//    public static final String RATE_LIMIT_LOCK_PREFIX = "RATE_LIMIT_LOCK";
//
//    @Value("${rateLimiter.on}")
//    private Boolean on;
//
//    /**
//     * 用户请求频率统计
//     * userId -> (requestCode -> RateCounter)
//     */
//    private Map<Long, Map<Integer, RateCounter>> rateCounters;
//
//    @PostConstruct
//    public void init() {
//        rateCounters = new ConcurrentHashMap<>();
//    }
//
//    /**
//     * 请求数量是否溢出
//     */
//    public boolean requestOverflow(Long userId, Integer requestCode) {
//        if (!on) {
//            return false;
//        }
//
//        Map<Integer, RateCounter> codeCounters = rateCounters.get(userId);
//        if (codeCounters == null) {
//            return false;
//        }
//        RateCounter codeCounter = codeCounters.get(requestCode);
//        if (codeCounter == null) {
//            return false;
//        }
//
//        long currentSecond = System.currentTimeMillis() / 1000;
//        Counter lastCounter = codeCounter.getCounters().get(REQUEST_COUNT_MAX_TIME-1);
//        if (lastCounter.timestamp == currentSecond && lastCounter.count > REQUEST_MAX_COUNT_PER_SECOND) {
//            return true;
//        }
//        int totalRequestCount = 0;
//        for (Counter counter : codeCounter.getCounters()) {
//            if (currentSecond - counter.timestamp < REQUEST_COUNT_MAX_TIME) {
//                totalRequestCount += counter.count;
//                if (totalRequestCount > REQUEST_MAX_COUNT_PER_5_SECONDS) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }
//
//    @Scheduled(initialDelay = 3600 * 1000, fixedRate = 3600 * 1000)
//    public void cleanOvertimeRecords() {
//        log.info("cleanOvertimeRecords begin, user records left count:" + rateCounters.size());
//        long currentSecond = System.currentTimeMillis() / 1000;
//        for (Map.Entry<Long, Map<Integer, RateCounter>> userCodeRcs : rateCounters.entrySet()) {
//            long userId = userCodeRcs.getKey();
//            Map<Integer, RateCounter> codeRcs = userCodeRcs.getValue();
//            for (Map.Entry<Integer, RateCounter> codeRc : codeRcs.entrySet()) {
//                if (currentSecond - codeRc.getValue().lastUpdateTime > REQUEST_COUNT_RECORD_OVERTIME) {
//                    synchronized ((RATE_LIMIT_LOCK_PREFIX + ":"  + userId).intern()) {
//                        codeRcs.remove(codeRc.getKey());
//                    }
//                }
//            }
//            if (codeRcs.isEmpty()) {
//                rateCounters.remove(userId);
//            }
//        }
//        log.info("cleanOvertimeRecords done, user records left count: " + rateCounters.size());
//    }
//
//    /**
//     * 增加计数
//     */
//    public void incrCount(Long userId, Integer requestCode) {
//        if (!on) {
//            return;
//        }
//
//        long currentSecond = System.currentTimeMillis() / 1000;
//
//        Map<Integer, RateCounter> codeCounters = rateCounters.computeIfAbsent(userId, k -> new HashMap<>());
//        synchronized ((RATE_LIMIT_LOCK_PREFIX + ":"  + userId).intern()) {
//            // code -> RateCounter
//            RateCounter rateCounter = codeCounters.get(requestCode);
//            if (rateCounter == null) {
//                rateCounter = new RateCounter(currentSecond);
//                codeCounters.put(requestCode, rateCounter);
//            }
//
//            rateCounter.incrCount(currentSecond);
//        }
//    }
//
//    @Data
//    private static class RateCounter {
//        //每秒一个Counter
//        private ArrayList<Counter> counters;
//        private long lastUpdateTime;    //单位s
//
//        public RateCounter() {
//            long currentSecond = System.currentTimeMillis() / 1000;
//            new RateCounter(currentSecond);
//        }
//
//        public RateCounter(long currentSecond) {
//            this.counters = new ArrayList<>();
//            for (int i = 0; i < REQUEST_COUNT_MAX_TIME; i++) {
//                this.counters.add(i, new Counter(currentSecond - REQUEST_COUNT_MAX_TIME + i + 1, 0));
//            }
//            this.lastUpdateTime = currentSecond;
//        }
//
//        public void incrCount(long currentSecond) {
//            this.lastUpdateTime = currentSecond;
//
//            Counter last = counters.get(REQUEST_COUNT_MAX_TIME - 1);
//            if (last.timestamp == currentSecond) {
//                last.count++;
//            } else if (last.timestamp < currentSecond && currentSecond < last.timestamp + REQUEST_COUNT_MAX_TIME) { //之前的记录部分过期
//                int pastSecond = (int)(currentSecond - last.timestamp);
//                for (int i = 0; i < REQUEST_COUNT_MAX_TIME; i++) {
//                    if (i < REQUEST_COUNT_MAX_TIME - pastSecond) {
//                        counters.get(i).timestamp += pastSecond;
//                        counters.get(i).count = counters.get(i+pastSecond).count;
//                    } else if (i == REQUEST_COUNT_MAX_TIME - 1) {
//                        counters.get(i).timestamp = currentSecond;
//                        counters.get(i).count = 1;
//                    } else {
//                        counters.get(i).timestamp += pastSecond ;
//                        counters.get(i).count = 0;
//                    }
//                }
//            } else if (currentSecond >= last.timestamp + REQUEST_COUNT_MAX_TIME) {  //之前的记录全部过期
//                for (int i = 0; i < REQUEST_COUNT_MAX_TIME; i++) {
//                    counters.get(i).timestamp = currentSecond - REQUEST_COUNT_MAX_TIME + i + 1;
//                    if (i == REQUEST_COUNT_MAX_TIME - 1) {
//                        counters.get(i).count = 1;
//                    } else {
//                        counters.get(i).count = 0;
//                    }
//                }
//            }
//        }
//    }
//
//    @Data
//    @AllArgsConstructor
//    private static class Counter {
//        private long timestamp; // second
//        private int count;
//    }
//}
