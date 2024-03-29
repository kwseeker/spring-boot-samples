package top.kwseeker.boot.transaction;

/*
* 从源码角度看多线程数据库操作 @Transactional 注解为何会失效？以及多线程一个事务的管理
*
* 之前有分析过编程式事务中多线程操作事务控制失败的原因，参考spring-analysis SpringTransactionAsyncExample。
* 简述：创建到某数据源的连接后都会将连接额外包装上事务控制参数（最终即事务对象）缓存到线程本地的NamedThreadLocal中；
* 后面某线程中执行数据操作都会先查线程自己NamedThreadLocal中有没有已缓存的事务对象，有的话直接使用，否则重新建连接（即新建事务），
* 而如果是多线程，每个线程都有自己的NamedThreadLocal，多个线程之间不会共享事务对象，这时就已经跨多个事务了，
* 因此事务中执行多线程操作，一定是会使事务失效的。
*
* 声明式事务底层原理和编程式事务是一样的，这里再过一下多线程声明式事务失效的原理流程。
*
* 解决方案
* 有两个思路：
* 1）使用编程式事务，每个线程一个事务，所有线程监听一个共享的状态，所有线程预执行成功则设置状态为true, 状态为true才提交，否则所有事务全部回滚。
*   这个网上案例很多。
* 2）其实我想是不是也可以共享事务对象呢？不过就算可以，两个线程使用一个连接，好像就变成串行了，多线程就没有意义了。
*
* 官方资料：
* 事务管理：
* https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#transaction
* JDBC数据访问：
* https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc
* 数据源配置：
* https://docs.spring.io/spring-boot/docs/2.6.14/reference/html/data.html#data.sql.datasource.configuration
* */