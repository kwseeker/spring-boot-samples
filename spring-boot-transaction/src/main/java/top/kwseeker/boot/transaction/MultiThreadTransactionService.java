package top.kwseeker.boot.transaction;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试多线程声明式事务失效原理流程，以及解决方案
 */
@Service
public class MultiThreadTransactionService {

    private final JdbcTemplate jdbcTemplate;

    public MultiThreadTransactionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void simpleTxOperate() {
        System.out.println("test simple tx operation ...");

    }

    public void multiThreadOperate() {
        System.out.println("test multi thread tx operation ...");
    }
}
