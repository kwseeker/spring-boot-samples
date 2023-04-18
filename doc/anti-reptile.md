# 反爬虫接口防刷组件

Demo 中测试的是 kk-anti-reptile, [kk-anti-reptile](https://github.com/kekingcn/kk-anti-reptile) 是适用于基于 spring-boot 开发的分布式系统的开源反爬虫接口防刷组件。

原理和代码都很简单，配置拦截器拦截请求，解析请求来源等信息并统计，根据IP和UA规则结合统计数据进行过滤。

统计数据存储在redis, 使用的 Redisson 客户端。

> 这里记录下方便日后用到时查找。
