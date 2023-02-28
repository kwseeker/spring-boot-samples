package top.kwseeker.boot.transaction.config;

import org.springframework.context.annotation.Configuration;

//2 事务管理基本配置：数据源(配置文件中配置属性值)、事务管理器、JDBC连接模板
//  这些在Spring Boot中会由自动装配机制自动配置（创建Bean实例），如果需要定制修改在这里修改，否则什么都不需要做
@Configuration
public class TransactionConfiguration {
}
