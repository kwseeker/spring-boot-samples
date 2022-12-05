# Spring Boot 优雅关闭方案

相关内容：

+ Java 关闭钩子
+ 进程、线程退出控制原理 & Linux kill 信号机制
+ 各Web框架优雅关闭方案（如：Netty、Tomcat等）
+ Spring Bean生命周期
+ Spring Boot Async 线程池优雅关闭原理
+ Spring Boot 2.3.0 之前的 Actuator

+ Spring Boot 2.3.0 之后自带的优雅关闭方案

+ 拓展：一些中间件的优雅关闭方案（中间件都有自己的优雅关闭处理）
+ 拓展：K8S 服务更新是如何触发关闭pod的



##  Java关闭钩子 ShutdownHook

详细参考：https://docs.oracle.com/javase/8/docs/api/java/lang/Runtime.html

关闭钩子只是一个初始化但未启动的线程。当虚拟机开始其关机序列时，它将以某种未指定的顺序启动所有注册的关机钩子，并让它们并发运行。当所有的钩子都执行完时，进程就会停止。注意，守护线程将在关闭序列期间继续运行，如果通过调用exit方法启动shutdown，则非守护线程也将继续运行。

ShutdownHook 需要尽快执行结束（不要在 ShutdownHook 执行可能被阻塞代码，如 I/0 读写）。

JVM虚拟机在发生下面事件后执行关闭钩子并退出：

+ 程序正常退出（最后一个非守护线程退出）
+ 调用System.exit()
+ 终端使用Ctrl+C触发的中断（SIGINT）
+ 用户注销，系统关闭
+ 使用Kill pid命令关闭进程（kill -9 不会触发关闭钩子执行而是立即关闭进程）

相关接口：

```java
//注册关闭钩子
public void addShutdownHook(Thread hook)
//注销关闭钩子
public boolean removeShutdownHook(Thread hook)
//强制终止当前运行的 Java 虚拟机。此方法永远不会正常返回。
public void halt(int status)
```



##  Linux kill 信号机制 & 进程、线程退出控制

这部分参考《UNIX环境高级编程》第10章。

### Linux kill 信号机制

信号（signal，软件中断）用于通知进程发生了某种情况。

```shell
kill -l 	#显示所有信号（这里是之母l,不是数字1）, Linux mint 只展示了前31种
1) SIGHUP       2) SIGINT       3) SIGQUIT      4) SIGILL
 5) SIGTRAP      6) SIGABRT      7) SIGBUS       8) SIGFPE
 9) SIGKILL     10) SIGUSR1     11) SIGSEGV     12) SIGUSR2
13) SIGPIPE     14) SIGALRM     15) SIGTERM     16) SIGSTKFLT
17) SIGCHLD     18) SIGCONT     19) SIGSTOP     20) SIGTSTP
21) SIGTTIN     22) SIGTTOU     23) SIGURG      24) SIGXCPU
25) SIGXFSZ     26) SIGVTALRM   27) SIGPROF     28) SIGWINCH
29) SIGIO       30) SIGPWR      31) SIGSYS      34) SIGRTMIN
35) SIGRTMIN+1  36) SIGRTMIN+2  37) SIGRTMIN+3  38) SIGRTMIN+4
39) SIGRTMIN+5  40) SIGRTMIN+6  41) SIGRTMIN+7  42) SIGRTMIN+8
43) SIGRTMIN+9  44) SIGRTMIN+10 45) SIGRTMIN+11 46) SIGRTMIN+12
47) SIGRTMIN+13 48) SIGRTMIN+14 49) SIGRTMIN+15 50) SIGRTMAX-14
51) SIGRTMAX-13 52) SIGRTMAX-12 53) SIGRTMAX-11 54) SIGRTMAX-10
55) SIGRTMAX-9  56) SIGRTMAX-8  57) SIGRTMAX-7  58) SIGRTMAX-6
59) SIGRTMAX-5  60) SIGRTMAX-4  61) SIGRTMAX-3  62) SIGRTMAX-2
63) SIGRTMAX-1  64) SIGRTMAX
```

**信号产生方式：**

+ 终端按键

+ 硬件异常

+ 函数（kill/raise/sigqueue/alarm等）、kill命令 （kill 命令用于从终端发送信号）

  ```shell
  kill -<SIG> <pid> [...]
  ```

+ 软件条件（网络数据就绪SIGURG、写管道SIGPIPE）

**Linux进程对信号的处理方式**：

+ 忽略信号
+ 按系统默认方式处理（大多数信号默认处理动作是终止该进程）
+ 提供一个信号处理函数，信号发生时回调该函数

**Web应用中常用信号**：

| Linux  信号 | 说明                                                         |
| ----------- | ------------------------------------------------------------ |
| 1 HUP       | 终端断线，在用户终端连接(正常或非正常)结束时发出             |
| 2 **INT**   | 中断（同 Ctrl + C）， 通知前台进程组终止进程                 |
| 3 QUIT      | 退出（同 Ctrl + \），进程在收到 SIGQUIT 退出时会产生core文件, 在这个意义上类似于一个程序错误信号 |
| 9 **KILL**  | 强制终止，用来立即结束程序的运行. 本信号不能被阻塞、不能被函数接收处理、不能被忽略 |
| 15 **TERM** | 终止，程序结束(terminate)信号, 与SIGKILL不同的是该信号可以被阻塞和处理。通常用来要求程序自己正常退出，shell命令kill缺省产生这个信号。 |
| 18 CONT     | 继续（与STOP相反， fg / bg命令）                             |
| 19 STOP     | 暂停（同 Ctrl + Z）                                          |

> SIGINT 信号只能结束前台进程，信号被当前进程树接收到，也就是说，不仅当前进程会收到信号，它的子进程也会收到；
>
> SIGTERM 可以被阻塞，kill不加任何参数的时候发送的就是这个信号，只有当前进程能收到信号，子进程不会收到。如果当前进程被kill了，那么它的子进程的父进程将会是init，也就是pid为1的进程；

**SIGTERM 测试**：

参考 linux-signal-process。

### 对Linux kill 信号的处理是怎么调用到Java关闭钩子的

> Linux上，猜测 JVM （JVM就是一个Linux进程）中也是通过 signal() 注册关闭钩子的，信号产生后回调执行关闭钩子的逻辑。

在钩子方法中加断点，可以看到有两个关键线程：`DestroyJavaVM` 和 `Signal Dispatcher` , 然后可以查JDK和JVM相关源码。

但是JVM源码相当复杂。但是间接找到了篇文章（[Revelations on Java signal handling and termination](https://web.archive.org/web/20090214230330/http://ibm.com:80/developerworks/java/library/i-signalhandling)）简述了当操作系统向 JVM 发出信号时信号分发器线程（Signal Dispatcher）如何将信号传递给适当的处理程序（关闭钩子）执行的。不过是基于JVM1.3的。

参考文章中”How the JVM processes signals“这部分。

还有一篇文章：[关于 Signal Dispatcher](https://blog.csdn.net/u011039332/article/details/105930146)。



## 各Web框架优雅关闭方案

**Web框架优雅停机需要做到**：

+ 不允许新的请求进入，或直接响应503等
+ 预留一点时间使容器内部业务线程执行完毕



## Spring Bean 生命周期 之 销毁

### 回顾Spring Bean生命周期



### Spring Bean 生命周期末端 - 销毁机制

+ DisposableBean接口 
+ @PreDestroy注解
+ destory-method 方法



## Spring Boot Async 线程池优雅关闭原理

+ /actuator/shutdown 请求



## Spring Boot 2.3.0 之前的 Actuator



## Spring Boot 2.3.0 之后自带的优雅关闭方案

### 配置

参考官网: [Graceful Shutdown](https://docs.spring.io/spring-boot/docs/2.6.14/reference/html/web.html#web.graceful-shutdown)。

支持四种嵌入式web服务器(Jetty、Reactor Netty、Tomcat和Undertow)以及响应式和基于servlet的web应用程序的优雅关机。

它作为关闭应用程序上下文的一部分发生，并在停止 **SmartLifecycle bean** 的最早阶段执行。**此关闭处理使用一个超时，该超时提供了一个宽限期，在此期间允许完成现有请求继续执行完毕，但不允许新请求进入**。不允许新请求的确切方式取决于所使用的web服务器。Jetty、Reactor Netty和Tomcat将停止接收网络层的请求。Undertow将接受请求，但立即响应服务不可用(503)响应。

> Spring Boot 优雅关机依赖内部组件自身的优雅关闭实现。

只需要加两行配置：

```yaml
server:
  shutdown: graceful 	#开启优雅停机，GRACEFUL：优雅停机 （限期停机），IMMEDIATE：立即停机
spring:
  lifecycle:
    timeout-per-shutdown-phase: 20s #设置关闭缓冲时间默认30s，超时无论线程任务是否执行完毕都会立即停机处理
```

### 源码实现原理





## 拓展：一些中间件的优雅关闭方案



## 拓展：K8S 服务更新是如何触发关闭pod的



## 参考资料

+ https://docs.spring.io/spring-boot/docs/2.6.14/reference/html/web.html#web.graceful-shutdown

+ [A Study of Graceful Shutdown for Spring Boot Applications](https://www.springcloud.io/post/2022-02/spring-boot-graceful-shutdown/#gsc.tab=0)

+ 《Unix环境高级编程》

  