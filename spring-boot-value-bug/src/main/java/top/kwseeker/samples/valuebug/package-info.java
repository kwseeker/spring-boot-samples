package top.kwseeker.samples.valuebug;

/*
* @Value 失效原因源码分析：
*
* 现象：
* 一个Bean中使用@Value分别注入application.yml中官方定义的属性(spring.application.name)和自定义的属性(rateLimiter.on)，
* 结果官方定义的属性注入正常，自定义属性注入失败（多层）;
* 如果自定义属性只有一层的话也可以注入成功。
* 使用application.properties则都可以成功注入。
*
* 分析：
*
* 经过分析源码可知，@Value处理过程主要分为两部分：
* 0）Bean容器启动时，属性装载到数据源;
* 1）读取@Value注解的占位符，从数据源获取此占位符对应的属性值； 问题出现这一步
* 2）将属性值填充到Bean字段
*
* 发现读取官方定义的属性可以获取到属性值，但是读取自定义属性无法获取到属性值；
* 然后对比官方属性和自定义属性读取差异。
* 最终发现：yaml 中 rateLimiter:on:true 属性key竟然解析成了"rateLimiter[true]", 而不是预期的"rateLimiter.on"
*   "spring.application.name" -> {OriginTrackedValue$OriginTrackedCharSequence@4950} "ValueAnnoDemo"
*   "rateLimiter[true]" -> {OriginTrackedValue@4952} "true"
* 然后怀疑on对yaml文件是不是个保留字段(并不是) 或 spring是否对yaml中的on做了特殊处理。
* 然后查看yaml配置解析到PropertySource的相关源码。
*
* */