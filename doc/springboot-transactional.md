# SpringBoot 事务管理流程 & 埋坑总结

前提是理解Spring编程式事务和Spring AOP的工作原理，参考spring-analysis事务相关章节。

这里梳理SpringBoot在spring编程式事务之上做了哪些封装（主要是@Transactional的原理）。

