   * [Java集合](#java集合)
   * [NIO](#nio)
      * [NIO特点](#nio特点)
      * [BIO的特点以及缺点](#bio的特点以及缺点)
         * [特点](#特点)
         * [缺点](#缺点)
      * [NIO 文件读写](#nio-文件读写)
      * [NIO聊天室](#nio聊天室)
         * [NIO Server](#nio-server)
         * [Nio Client](#nio-client)
      * [NIO零拷贝问题](#nio零拷贝问题)
         * [mmap](#mmap)
         * [sendFile](#sendfile)
         * [mmap 和 sendFile 的区别](#mmap-和-sendfile-的区别)
      * [Netty](#netty)
         * [前言](#前言)
            * [Reactor模型](#reactor模型)
         * [Netty流程及源码解读](#netty流程及源码解读)
            * [启动流程](#启动流程)
               * [总体流程](#总体流程)
               * [启动流程源码解读](#启动流程源码解读)
            * [接受连接OP_ACCEPT请求流程](#接受连接op_accept请求流程)
               * [总体流程](#总体流程-1)
               * [相关源码](#相关源码)
            * [ChannelSocket的PipeLine调度handler流程](#channelsocket的pipeline调度handler流程)
               * [总体流程](#总体流程-2)
               * [相关源码](#相关源码-1)
            * [心跳流程](#心跳流程)
               * [总体流程](#总体流程-3)
               * [相关源码](#相关源码-2)
               * [总结](#总结)
            * [handler 中加入线程池和 Context 中添加线程池的源码剖析](#handler-中加入线程池和-context-中添加线程池的源码剖析)
               * [两种解决方式](#两种解决方式)
               * [要如何选择两种方式？](#要如何选择两种方式)
         * [实现类讲解](#实现类讲解)
            * [BootStrap、ServerBootStrap](#bootstrapserverbootstrap)
               * [常用的方法](#常用的方法)
                  * [服务器端：](#服务器端)
                  * [客户端](#客户端)
            * [Future、ChannelFuture](#futurechannelfuture)
               * [常用方法](#常用方法)
            * [Channel](#channel)
            * [Selector](#selector)
            * [ChannelHandler极其实现类](#channelhandler极其实现类)
            * [Pipeline和ChannelPipeline](#pipeline和channelpipeline)
               * [常用方法](#常用方法-1)
            * [ChannelHandlerContext](#channelhandlercontext)
            * [ChannelOption](#channeloption)
               * [TCP参数表](#tcp参数表)
            * [EventGroupLoop极其实现类NioEventGroupLoop](#eventgrouploop极其实现类nioeventgrouploop)
               * [EventLoop](#eventloop)
            * [Unpooled](#unpooled)
         * [TCP的粘包/拆包问题](#tcp的粘包拆包问题)
            * [什么是TCP的粘包拆包？](#什么是tcp的粘包拆包)
            * [为什么会发生TCP粘包、拆包？](#为什么会发生tcp粘包拆包)
            * [粘包、拆包解决办法](#粘包拆包解决办法)
            * [Netty解决粘包拆包的方法](#netty解决粘包拆包的方法)
               * [自定义协议包](#自定义协议包)
               * [编码器](#编码器)
               * [解码器](#解码器)
         * [简单的服务器端与客户端交互](#简单的服务器端与客户端交互)
            * [服务端](#服务端)
            * [客户端](#客户端-1)
            * [效果图](#效果图)
         * [Http服务器](#http服务器)
            * [效果图](#效果图-1)
         * [聊天室](#聊天室)
            * [服务端](#服务端-1)
            * [客户端](#客户端-2)
            * [效果图](#效果图-2)
         * [简易RPC](#简易rpc)
            * [通用类模块](#通用类模块)
            * [生产者模块(服务被调用者)](#生产者模块服务被调用者)
               * [步骤](#步骤)
                  * [1. 实现通用类下面的接口](#1-实现通用类下面的接口)
                  * [2. 实现Netty以及处理类](#2-实现netty以及处理类)
            * [消费者模块(服务调用者)](#消费者模块服务调用者)
               * [步骤](#步骤-1)
                  * [1. 暴露接口级注解实现](#1-暴露接口级注解实现)
                  * [2. 实现Netty以及处理类](#2-实现netty以及处理类-1)
                  * [3. 代理类实现](#3-代理类实现)
                  * [4. 处理类实现](#4-处理类实现)
            * [效果图](#效果图-3)
   * [Thread](#thread)
   * [BigData](#bigdata)
      * [Hive](#hive)
      * [HBase](#hbase)
      * [Spark](#spark)
      * [Flink](#flink)



# Java集合

待更新

# NIO

## NIO特点

> JavaNIO :同步非阻塞， 服务器实现模式为一个线程处理多个请求(连接)，即客户端发送的连接请求都会注册到多路复用器上，多路复用器轮询到连接有I/O请求就进行处理

## BIO的特点以及缺点

> 同步并阻塞(传统阻塞型)，服务器实现模式为一个连接一一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销

### 特点

BIO的特点就是每个请求都要开一个线程去进行与客户端的通信，进行数据的读写操作，其工作图如下：

![](http://image.tinx.top/img20210730110452.png)



### 缺点

+ 当并发数较大的时候，需要创建大量的线程来处理连接，系统资源占用比较大。
+ 连接建立后，如果当前线程暂时没有数据可以读取，那么这个线程就会被**阻塞**到读取这个命令下，造成资源浪费。

## NIO 文件读写

[文件读](./src/main/java/com/wills/nio/example/ReadExample.java)

[文件先读后写](./src/main/java/com/wills/nio/example/WriteExample.java)

## NIO聊天室

### NIO Server

> 大致流程就是：
>
> 1. 创建一个服务端类NioChatGroupServer，构造方法用于初始化 ServerSocketChannel、Selector、InetSocketAddress，然后注册Select的OP_ACCEPT连接行为
> 2. 创建一个listen方法，用于接收/转发客户端发送的消息
> 3. Listen方法内，首先先调用 selector的select方法，拿到已经准备好io的keys，如果准备好的数量大于0
> 4. 就进行遍历这些key，如果key的状态是 isAcceptable 说明是刚刚连接，就进行客户端通知，xxx上线了
> 5. 如果key的状态时 isReadable 说明是发送了消息，就进行消息读取/转发的操作

效果图：

![](http://image.tinx.top/img20210730115330.png)

[多人聊天室-服务端](./src/main/java/com/wills/nio/example/chat/NioChatGroupServer.java)

### Nio Client

> 大致流程：
>
> 1. 创建一个聊天客户端类NioChatGroupClient，构造方法用于初始化 Selector、SocketChannel，注册selector的消息读取的事件 SelectionKey.OP_READ
> 2. 创建一个 sendSay 方法，该方法用于发送自己说的话，具体的流程就是调用socketChannel的write方法，进行消息的发送
> 3. 创建一个 receiveSay 方法，该方法用于读取别人说的话，具体的流程就是 先调用 selector的select方法，，如果触发了SelectionKey.OP_READ事件的个数大于0，那么就进行channel.read 读取消息，然后显示在屏幕上

客户端上线时，服务器以及另一个客户端的效果图：

![](http://image.tinx.top/img20210730115952.png)

聊天的效果图：

![](http://image.tinx.top/img20210730120052.png)

[多人聊天室-客户端](./src/main/java/com/wills/nio/example/chat/NioChatGroupClient.java)



## NIO零拷贝问题

初学 Java 时，我们在学习 IO 和 网络编程时，会使用以下代码：

```java
File file = new File("index.html");
RandomAccessFile raf = new RandomAccessFile(file, "rw");

byte[] arr = new byte[(int) file.length()];
raf.read(arr);

Socket socket = new ServerSocket(8080).accept();
socket.getOutputStream().write(arr);
```

我们会调用 read 方法读取 index.html 的内容—— 变成字节数组，然后调用 write 方法，将 index.html 字节流写到 socket 中，那么，我们调用这两个方法，在 OS 底层发生了什么呢？以下这个过程的概述：

![](http://image.tinx.top/img20210730122815.png)



上图中，上半部分表示用户态和内核态的上下文切换。下半部分表示数据复制操作。下面说说他们的步骤：

1. read 调用导致用户态到内核态的一次变化，同时，第一次复制开始：DMA（Direct Memory Access，直接内存存取，即不使用 CPU 拷贝数据到内存，而是 DMA 引擎传输数据到内存，用于解放 CPU） 引擎从磁盘读取 index.html 文件，并将数据放入到内核缓冲区。

2. 发生第二次数据拷贝，即：将内核缓冲区的数据拷贝到用户缓冲区，同时，发生了一次用内核态到用户态的上下文切换。

3. 发生第三次数据拷贝，我们调用 write 方法，系统将用户缓冲区的数据拷贝到 Socket 缓冲区。此时，又发生了一次用户态到内核态的上下文切换。

4. 第四次拷贝，数据异步的从 Socket 缓冲区，使用 DMA 引擎拷贝到网络协议引擎。这一段，不需要进行上下文切换。

5. write 方法返回，再次从内核态切换到用户态。



通过图/步骤我们可以可以得知，一次这样的读取写入操作经历了多次的从用户态到内核态的上下文转换，学过操作系统这门本科必修课的知道，用户态转换为内核态/内核态转换为用户态是极其耗费时间、性能的，那么我们如何要优化呢？其实有两个解决办法：

### mmap

> mmap 通过内存映射，将文件映射到内核缓冲区，同时，用户空间可以共享内核空间的数据。这样，在进行网络传输时，就可以减少内核空间到用户控件的拷贝次数。

如下图：

![](http://image.tinx.top/img20210730123654.png)



如上图，user buffer 和 kernel buffer 共享 index.html。如果你想把硬盘的 index.html 传输到网络中，再也不用拷贝到用户空间，再从用户空间拷贝到 Socket 缓冲区。

现在，mmap的方式从内核缓冲区拷贝到 Socket 缓冲区即可，这将减少一次内存拷贝（从 4 次变成了 3 次，通过shared方式共享这个index.html），但不减少上下文切换次数。

### sendFile

Linux 2.1 版本 提供了 sendFile 函数，其基本原理如下：数据根本不经过用户态，直接从内核缓冲区进入到 Socket Buffer，同时，由于和用户态完全无关，就减少了一次上下文切换。具体步骤如下图所示：

![](http://image.tinx.top/img20210730123954.png)

如上图，我们进行 sendFile 系统调用时，数据被 DMA 引擎从文件复制到内核缓冲区，然后调用，然后掉一共 write 方法时，从内核缓冲区进入到 Socket，这时，是没有上下文切换的，因为在一个用户空间。

最后，数据从 Socket 缓冲区进入到协议栈。

此时，数据经过了 3 次拷贝，3 次上下文切换。

那么，还能不能再继续优化呢？ 例如直接从内核缓冲区拷贝到网络协议栈？

实际上，Linux 在 2.4 版本中，做了一些修改，避免了从内核缓冲区拷贝到 Socket buffer 的操作，直接拷贝到协议栈，从而再一次减少了数据拷贝。具体如下图：

![](http://image.tinx.top/img20210730124024.png)

现在，index.html 要从文件进入到网络协议栈，只需 2 次拷贝：第一次使用 DMA 引擎从文件拷贝到内核缓冲区，第二次从内核缓冲区将数据拷贝到网络协议栈；<font color=red>**内核缓存区只会拷贝一些 offset 和 length 信息到 SocketBuffer，基本无消耗。**</font>

等一下，不是说零拷贝吗？为什么还是要 2 次拷贝？

答：首先我们说零拷贝，是从操作系统的角度来说的。因为内核缓冲区之间，没有数据是重复的（只有 kernel buffer 有一份数据，**sendFile 2.1 版本实际上有 2 份数据，算不上零拷贝**）。例如我们刚开始的例子，内核缓存区和 Socket 缓冲区的数据就是重复的。

而零拷贝不仅仅带来更少的数据复制，还能带来其他的性能优势，例如更少的上下文切换，更少的 CPU 缓存伪共享以及无 CPU 校验和计算。

###  mmap 和 sendFile 的区别

1. mmap 适合小数据量读写，sendFile 适合大文件传输,因为减少拷贝次数，就意味着大文件传输的效率加快接近一倍。

2. mmap 需要 4 次上下文切换，3 次数据拷贝；sendFile 需要 3 次上下文切换，最少 2 次数据拷贝。

3. sendFile 可以利用 DMA 方式，减少 CPU 拷贝，mmap 则不能（必须从内核拷贝到 Socket 缓冲区）。

在这个选择上：rocketMQ 在消费消息时，使用了 mmap。kafka 使用了 sendFile。



## Netty

### 前言

在说Netty之前，我们要先复习I/O服务模型：

#### Reactor模型

+ 传统I/O模型

  > 传统的I/O模型设计是 每一次I/O请求，都会开一个线程，涉及到进行I/O的时候，会阻塞线程，这样就会造成资源浪费，架构图如下:
  >
  > ![](http://image.tinx.top/img20210731114744.png)

+ Reactor模型(I/O复用的线程池)

  > 1. Reactor模式，通过一个或多个输入同时传递给服务处理器的模式(基于事件驱动)
  >
  > 2. 服务器端程序处理传入的多个请求，并将它们同步分派到相应的处理线程，因此Reactor模式也叫Dispatcher
  >
  > 3. Reactor 模式使用IO复用监听事件，收到事件后，分发给某个线程(进程)，这点就是网络服务器高并发处理关键

  + 单Reactor单线程

    > ![](http://image.tinx.top/img20210731115344.png)
    >
    > 讲解：客户端请求应用程序进行I/O操作时，会先由Reactor监听相关的事件请求，如果是刚刚建立连接(Nio的OP_ACCEPT事件)，会进行事件注册Selector，后续进行读取/写入操作的时候会自动Dispatcher到相关的Handler
    >
    > 优点：
    >
    > + 模型简单，没有多线程、进程通信、竞争的问题，全部都在一个线程中完成
    >
    > 缺点：
    >
    > + 性能问题，只有一个线程，无法完全发挥多核CPU的性能。Handler 在处理某个连接上的业务时，整个进程无法处理其他连接事件，很容易导致性能瓶颈
    > + 可靠性问题，线程意外终止，或者进入死循环，会导致整个系统通信模块不可用，不能接收和处理外部消息，造成节点故障

  + 单Reactor多线程

    > ![](http://image.tinx.top/img20210731121019.png)
    >
    > 单Reactor多线程是单Reactor单线程的升级版，升级的就是之前Handler直接处理业务，而单Reactor多线程将业务处理放在了多线程进行处理
    >
    > 优点:可以充分的利用多核cpu的处理能力
    >
    > 缺点:多线程数据共享和访问比较复杂，reactor 处理所有的事件的监听和响应，在单线程运行，在高并发场景容易出现性能瓶颈.

  + 主从Reator多线程

    > ![](http://image.tinx.top/img20210731121253.png)
    >
    > 通过名称 "主从Reator多线程" 可以得知，这个架构模型最主要的就是从上面的 单Reactor多线程的模型升级版，只是Reactor出现了主线程和子线程
    >
    > 1) Reactor 主线程MainReactor 对象通过select监听连接事件，收到事件后，通过Acceptor处理连接事件
    > 2) 当Acceptor 处 理连接事件后，MainReactor 将连接分配给SubReactor
    > 3) subreactor将连接加入到连接队列进行监听，并创建handler进行各种事件处理
    > 4) 当有新事件发生时，subreactor就会调用对应的handler处理
    > 5)  handler 通过read读取数据，分发给后面的worker线程处理
    > 6) worker 线程池分配独立的worker线程进行业务处理，并返回结果



### Netty流程及源码解读

#### 启动流程

##### 总体流程

1) 创建 2 个 EventLoopGroup 线程池数组。数组默认大小 CPU*2，方便 chooser 选择线程池时提高性能 

2) BootStrap 将 boss 设置为 group 属性，将 worker 设置为 childer 属性 

3) 通过 bind 方法启动，内部重要方法为 initAndRegister 和 dobind 方法 

4) initAndRegister 方法会反射创建 NioServerSocketChannel 及其相关的 NIO 的对象， pipeline ， unsafe，同时也为 pipeline 初始了 head 节点和 tail 节点。 

5) 在 register0 方法成功以后调用在 dobind 方法中调用 doBind0 方法，该方法会 调用 NioServerSocketChannel的 doBind 方法对 JDK 的 channel 和端口进行绑定，完成 Netty 服务器的所有启动，并开始监听连接事件

##### 启动流程源码解读

```java
// dobind方法
private ChannelFuture doBind(final SocketAddress localAddress) {
  			// 调用下方的 initAndRegister 初始化并且注册方法
        final ChannelFuture regFuture = initAndRegister();
  			// 拿到这个 future 下的 channel 
        final Channel channel = regFuture.channel();
  			// 如果有错误，直接返回
        if (regFuture.cause() != null) {
            return regFuture;
        }
				// 如果完成了 没有错误
        if (regFuture.isDone()) {
            // 创建一个 Promise
            ChannelPromise promise = channel.newPromise();
          	// 遍历这个 channel 的 EventLoop
          	// 以下就是这个方法的内容，就是进行端口绑定，然后再添加一个在遇到错误关闭的监听器
          	/*
          		if (regFuture.isSuccess()) {channel.bind(localAddress,promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    promise.setFailure(regFuture.cause());
                }
          	*/
            doBind0(regFuture, channel, localAddress, promise);
            return promise;
        } else {
          	// 如果没有完成,就手动创建一个 promise
            final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
          	// 然后给这个  future添加一个监听器
            regFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Throwable cause = future.cause();
                    if (cause != null) {
                      	// 遇到错误就设置错误的 Throwable
                        promise.setFailure(cause);
                    } else {
                        // 如果错误的 cause为Null，就重新注册一次，和上面代码一样
                        promise.registered();
												// 重新绑定
                        doBind0(regFuture, channel, localAddress, promise);
                    }
                }
            });
            return promise;
        }
    }

// initAndRegister 方法
final ChannelFuture initAndRegister() {
  			// 初始化一个 channel 对象
        Channel channel = null;
        try {
          	// 从工厂方法中 获取一个 channel
            channel = channelFactory.newChannel();
            init(channel);
        } catch (Throwable t) {
            // 初始化channel 进行相关的处理 代码略
        }

  			// 注册 channel 
        ChannelFuture regFuture = config().group().register(channel);
  			// 如果出错，就进行 关闭通道 等操作
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }
				// 不出错就返回这个future，futrure可以进行异步sync()操作
        return regFuture;
    }

// dobind0方法就是上面的 dobind方法会调用的，这个是 netty启动的关键方法
private static void doBind0(final ChannelFuture regFuture, final Channel channel,
            final SocketAddress localAddress, final ChannelPromise promise) {
        // 直接低啊用 eventLoop （默认是NioEventLoop）的execute方法
        channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                if (regFuture.isSuccess()) {
                    channel.bind(localAddress, promise).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    promise.setFailure(regFuture.cause());
                }
            }
        });
    }

// 最终上面的dobind0方法会绕回到 NioEventLoop里面的 自旋 run方法
protected void run() {
  			// 自旋算法
        for (;;) {
            try {
                try {
                  	// 判断计算策略
                    switch (selectStrategy.calculateStrategy(selectNowSupplier, hasTasks())) {
                    // 继续策略进行自旋continue
                    case SelectStrategy.CONTINUE:
                        continue;
                    case SelectStrategy.BUSY_WAIT:
                        // 由于NIO不支持忙等待，所以切换到SELECT
                    case SelectStrategy.SELECT:
                        // CAS原子方法设置状态值 唤醒状态位 设置为 false 表示不唤醒，因为是繁忙等待状态
                        select(wakenUp.getAndSet(false));
                        // 唤醒这个 有任务的 wakenUp线程
                        if (wakenUp.get()) {
                            selector.wakeup();
                        }
                    default:
                    }
                } catch (IOException e) {
                    // 错误处理，代码略
                }
								
              	// 后续的步骤就是 判断是否要运行所有的任务 还是特定的selectKeys
                cancelledKeys = 0;
                needsToSelectAgain = false;
              	/*
              		当 processSelectedKeys 方法执行结束后，则按照 ioRatio 的比例执行runAllTasks 方法，默认是 IO 任务时间 和非 IO 任务时间是相同的，你也可以根据你的应用特点进行调优 。比如 非 IO 任务比较多，那么你就将ioRatio 调小一点，这样非 IO 任务就能执行的长一点。防止队列积攒过多的任务。
              	*/
                final int ioRatio = this.ioRatio;
              	// 如果 io 比率是 100 ，就全力处理 选定 / 所有任务，默认是 50
                if (ioRatio == 100) {
                    try {
                      	// 运行所选任务p
                        processSelectedKeys();
                    } finally {
                        // 运行所有的任务
                        runAllTasks();
                    }
                } else {
                  	// 带超时时间的处理任务
                    final long ioStartTime = System.nanoTime();
                    try {
                        processSelectedKeys();
                    } finally {
                        // Ensure we always run tasks.
                        final long ioTime = System.nanoTime() - ioStartTime;
                        runAllTasks(ioTime * (100 - ioRatio) / ioRatio);
                    }
                }
            } catch (Throwable t) {
            }
            try {
              	// 采用二次确认的方法，判断是否关闭了所有处理的任务
                if (isShuttingDown()) {
                    closeAll();
                    if (confirmShutdown()) {
                        return;
                    }
                }
            } catch (Throwable t) {
            }
        }
    }
```



#### 接受连接OP_ACCEPT请求流程

##### 总体流程

接受连接----->创建一个新的 NioSocketChannel----------->注册到一个 worker EventLoop 上--------> 注册 selecot Read 事件。 

1) 服务器轮询 Accept 事件，获取事件后调用 unsafe 的 read 方法，这个 unsafe 是 ServerSocket 的内部类，该方法内部由 2 部分组成 

2) doReadMessages 用于创建 NioSocketChannel 对象，该对象包装 JDK 的 Nio Channel 客户端。该方法会像创建 ServerSocketChanel 类似创建相关的 pipeline ， unsafe，config 

3) 随后执行 执行 pipeline.fireChannelRead 方法，并将自己绑定到一个 chooser 选择器选择的 workerGroup 中的 一个 EventLoop。并且注册一个 0，表示注册成功，但并没有注册读（1）事件 

##### 相关源码

说明：

1) 检查该 eventloop 线程是否是当前线程。assert eventLoop().inEventLoop() 

2) 执行 doReadMessages 方法，并传入一个 readBuf 变量，这个变量是一个 List，也就是容器。 

3) 循环容器，执行 pipeline.fireChannelRead(readBuf.get(i)); 

4) doReadMessages 是读取 boss 线程中的 NioServerSocketChannel 接受到的请求。并把这些请求放进容器

5) 循环遍历 容器中的所有请求，调用 pipeline 的 fireChannelRead 方法，用于处理这些接受的请求或者其他事件，在 read 方法中，循环调用 ServerSocket 的 pipeline 的 fireChannelRead 方法, 开始执行 管道中的 handler 的 ChannelRead 方法

```java
// 判断java的Nio的 OP_READ / OP_ACCEPT 与 readyOps 进行 与位运算(&) 是否不等于0
if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
    unsafe.read();
}

// read 方法
public void read() {
    assert eventLoop().inEventLoop();
		// 获取基础信息 配置信息、pipeLine
    final ChannelConfig config = config();
    final ChannelPipeline pipeline = pipeline();
    final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
    allocHandle.reset(config);

    boolean closed = false;
    Throwable exception = null;
    try {
        try {
            do {
                int localRead = doReadMessages(readBuf);
                if (localRead == 0) {
                    break;
                }
                if (localRead < 0) {
                    closed = true;
                    break;
                }

                allocHandle.incMessagesRead(localRead);
            } while (allocHandle.continueReading());
        } catch (Throwable t) {
            exception = t;
        }

      	// 读取 buf信息
        int size = readBuf.size();
        for (int i = 0; i < size; i ++) {
            readPending = false;
          	// ChannelInBoundHandler的read方法
            pipeline.fireChannelRead(readBuf.get(i));
        }
        readBuf.clear();
        allocHandle.readComplete();
        pipeline.fireChannelReadComplete();
				
      	// 判断是否出现错误
        if (exception != null) {
            closed = closeOnReadError(exception);

            pipeline.fireExceptionCaught(exception);
        }
				// 判断 close 关闭符号
        if (closed) {
            inputShutdown = true;
            if (isOpen()) {
                close(voidPromise());
            }
        }
    } finally {
        // 检查这里是否没有被读处理
        if (!readPending && !config.isAutoRead()) {
            removeReadOp();
        }
    }
}
```



#### ChannelSocket的PipeLine调度handler流程

每当创建一个ChannelSocket时，服务器会同步为这个ChannelSocket创建一个pipeLine链表(会自动创建一个tail节点(outBound)和head节点(inBound),形成最初的链表)，用于操作PipeLine的是 HandlerContext（处理器上下文），操作的对象就是handler，用于处理所有的IO操作

[相关实现类点此跳转](#Pipeline和ChannelPipeline)

##### 总体流程

说明：

1) pipeline 首先会调用 Context 的静态方法 fireXXX，并传入 Context 

2) 然后，静态方法调用 Context 的 invoker 方法，而 invoker 方法内部会调用该 Context 所包含的 Handler 的真正的 XXX 方法，调用结束后，如果还需要继续向后传递，就调用 Context 的 fireXXX2 方法，循环往复。
3) Context 包装 handler，多个 Context 在 pipeline 中形成了双向链表，入站方向叫 inbound，由 head 节点开始， 出站方法叫 outbound ，由 tail 节点开始。 
4) 而节点中间的传递通过 AbstractChannelHandlerContext 类内部的 fire 系列方法，找到当前节点的下一个节点不断的循环传播。是一个过滤器形式完成对 handler 的调度

##### 相关源码

创建pipeLine 会调用 AbstractChannel的带参(channel)的构造方法，然后带参构造方法会最终调用DefaultChannelPipeline的构造方法，源码如下：

说明：

1）将 channel 赋值给 channel 字段，用于 pipeline 操作 channel。

2）创建一个 future 和 promise，用于异步回调使用。 

3）创建一个 inbound 的 tailContext，创建一个既是 inbound 类型又是 outbound 类型的 headContext. 

4）最后，**将两个 Context 互相连接，形成双向链表**。 

5）tailContext 和 HeadContext 非常的重要，所有 pipeline 中的事件都会流经他们，

```java
protected DefaultChannelPipeline(Channel channel) {
  	// 检查这个 channel是否为 null
    this.channel = ObjectUtil.checkNotNull(channel, "channel");
  	// 创建一个成功的 ChannelFuture
  	/*
  		SucceededChannelFuture(Channel channel, EventExecutor executor) {
        super(channel, executor);
    	}
  	*/
    succeededFuture = new SucceededChannelFuture(channel, null);
  	/*
  		 public VoidChannelPromise(final Channel channel, boolean fireException) {
    		    this.channel = channel;
    		    if (fireException) {
    		        fireExceptionListener = new ChannelFutureListener() {
    		            @Override
    		            public void operationComplete(ChannelFuture future){
    		                Throwable cause = future.cause();
    		                if (cause != null) {
    		                    fireException0(cause);
    		                }
    		            }
    		        };
    		    } else {
    		        fireExceptionListener = null;
    		    }
    		}
  	*/
    voidPromise =  new VoidChannelPromise(channel, true);
		
  	// ☆☆☆☆☆重点！！！ 分别创建两个Context，一个是tail结尾的，一个是头部的 head☆☆☆☆
    tail = new TailContext(this);
    head = new HeadContext(this);
		// 组成双向链表
    head.next = tail;
    tail.prev = head;
}
```



#### 心跳流程

> Netty提供了三个Handler用于检测连接的有效性，分别为：
>
> + IdleStateHandler
>
>   > 当连接的空闲时间(读 / 写) ，将会触发一个 IdleStateEvent时间，可以通过ChannelInBoundHandler的userEventTrigger方法来处理这个事件
>
> + ReadTimeoutHandler
>
>   > 如果在指定的时间没有发生读取事件，就会抛出这个异常，并且自动关闭连接，可以在exceptionCaught来处理这个异常
>
> + WriteTimeoutHandler
>
>   > 当一个写操作不能在一定时间内完成时，就会抛出这个异常，并且关闭关闭连接，可以在exceptionCaught来处理这个异常

##### 总体流程

1) 得到用户设置的超时时间。 

2) 如果读取操作结束了（执行了 channelReadComplete 方法设置） ，就用当前时间减去给定时间和最后一次读（执操作的时间行了 channelReadComplete 方法设置），如果小于 0，就触发事件。反之，继续放入队 列。间隔时间是新的计算时间。 

3) 触发的逻辑是：首先将任务再次放到队列，时间是刚开始设置的时间，返回一个 promise 对象，用于做取消操作。然后，设置 first 属性为 false ，表示，下一次读取不再是第一次了，这个属性在 channelRead 方法会被改成 true。 

4) 创建一个 IdleStateEvent 类型的写事件对象，将此对象传递给用户的 UserEventTriggered 方法。完成触发事件的操作。 

5) 总的来说，每次读取操作都会记录一个时间，定时任务时间到了，会计算当前时间和最后一次读的时间的间隔，如果间隔超过了设置的时间，就触发 UserEventTriggered 方法。

##### 相关源码

IdleStateHandler：

```java
private final boolean observeOutput; //是否考虑出站时较慢的情况。默认值是 false 
private final long readerIdleTimeNanos;//读事件空闲时间，0 则禁用事件 
private final long writerIdleTimeNanos;//写事件空闲时间，0 则禁用事件
private final long allIdleTimeNanos;//读或写空闲时间，0 则禁用事件

// 在 handlerAdded等一系列ChannelInBoundHandler的常用方法付中会判断channel是否被注册是否活跃，如果满足条件会调用以下的方法
private void initialize(ChannelHandlerContext ctx) {
    // 避免 destroy() 在延时超时前被调用
    switch (state) {
    case 1:
    case 2:
        return;
    }

    state = 1;
    initOutputChanged(ctx);

  	// 根据条件 调用不同的 定时方法
    lastReadTime = lastWriteTime = ticksInNanos();
    if (readerIdleTimeNanos > 0) {
        readerIdleTimeout = schedule(ctx, new ReaderIdleTimeoutTask(ctx),
                readerIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
    if (writerIdleTimeNanos > 0) {
        writerIdleTimeout = schedule(ctx, new WriterIdleTimeoutTask(ctx),
                writerIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
    if (allIdleTimeNanos > 0) {
        allIdleTimeout = schedule(ctx, new AllIdleTimeoutTask(ctx),
                allIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
}
```

##### 总结

**Netty** **的心跳机制** 

1) IdleStateHandler 可以实现心跳功能，当服务器和客户端没有任何读写交互时，并超过了给定的时间，则会 触发用户 handler 的 userEventTriggered 方法。用户可以在这个方法中尝试向对方发送信息，如果发送失败，则关闭连接。

2) IdleStateHandler 的实现基于 EventLoop 的定时任务，每次读写都会记录一个值，在定时任务运行的时候， 通过计算当前时间和设置时间和上次事件发生时间的结果，来判断是否空闲。 

3) 内部有 3 个定时任务，分别对应读事件，写事件，读写事件。通常用户监听读写事件就足够了。 

4) 同时，IdleStateHandler 内部也考虑了一些极端情况：客户端接收缓慢，一次接收数据的速度超过了设置的空闲时间。Netty 通过构造方法中的 observeOutput 属性来决定是否对出站缓冲区的情况进行判断。 

5) 如果出站缓慢，Netty 不认为这是空闲，也就不触发空闲事件。但第一次无论如何也是要触发的。因为第一次无法判断是出站缓慢还是空闲。当然，出站缓慢的话，可能造成 OOM , OOM 比空闲的问题更大。 

6) 所以，当你的应用出现了内存溢出，OOM 之类，并且写空闲极少发生（使用了 observeOutput 为 true），那么就需要注意是不是数据出站速度过慢。

#### handler 中加入线程池和 Context 中添加线程池的源码剖析

##### 两种解决方式

1) 在 Netty 中做耗时的，不可预料的操作，比如数据库，网络请求，会严重影响 Netty 对 Socket 的处理速度。 

2) 而解决方法就是将耗时任务添加到异步线程池中。但就添加线程池这步操作来讲，可以有 2 种方式，而且这 2 种方式实现的区别也蛮大的。 

3) 处理耗时业务的第一种方式---handler 中加入线程池 

   ```java
   public class EventExcutorHandler extends SimpleChannelInboundHandler<Object> {
   		// 使用线程池
       private final EventExecutorGroup group = new DefaultEventLoopGroup(16);
   
       @Override
       protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
   
       }
   
   
       @Override
       public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
           group.submit(() -> {
               ByteBuf buf = (ByteBuf) msg;
               byte[] data = new byte[buf.readableBytes()];
               buf.readBytes(data);
               try {
                   String dataStr = new String(data,"utf-8");
                   Thread.sleep(10 * 1000);
                   ctx.channel().writeAndFlush(dataStr);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           });
       }
   
       @Override
       public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
           ctx.flush();
       }
   
       @Override
       public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
           ctx.close();
       }
   }
   
   // AbstractChannelHandlerContext类下的 write方法
   //1) 当判定下个 outbound 的 executor 线程不是当前线程的时候，会将当前的工作封装成 task ，然后放入 mpsc 队列中，等待 IO 任务执行完毕后执行队列中的任务。 
   //2) 当我们使用了 group.submit(new Callable<Object>(){} 在 handler 中 加 入 线 程 池 ， 就 会 进 入 到 safeExecute(executor, task,promise, m); 如果去掉这段代码，而使用普通方式来执行耗时的业务，那么就不会进入到 safeExecute(executor,task, promise, m);
   private void write(Object msg, boolean flush, ChannelPromise promise) {
       AbstractChannelHandlerContext next = findContextOutbound();
       final Object m = pipeline.touch(msg, next);
       EventExecutor executor = next.executor();
       if (executor.inEventLoop()) {
           if (flush) {
               next.invokeWriteAndFlush(m, promise);
           } else {
               next.invokeWrite(m, promise);
           }
       } else {
           final AbstractWriteTask task;
           if (flush) {
               task = WriteAndFlushTask.newInstance(next, m, promise);
           }  else {
               task = WriteTask.newInstance(next, m, promise);
           }
           if (!safeExecute(executor, task, promise, m)) {
               task.cancel();
           }
       }
   }
   ```

4) 处理耗时业务的第二种方式---Context 中添加线程池

   ```java
   // pipeline.addLast(group,new EventExcutorHandler()); 重点！
   public class NettyServer {
   
       private static final EventExecutorGroup group = new DefaultEventLoopGroup(16);
       private static final Integer PORT = 7777;
   
       public static void main(String[] args) {
           NioEventLoopGroup boss = new NioEventLoopGroup(1);
           NioEventLoopGroup worker = new NioEventLoopGroup();
   
           try{
               ServerBootstrap bootstrap = new ServerBootstrap();
               bootstrap.group(boss,worker)
                       .channel(ServerSocketChannel.class)
                       .option(ChannelOption.SO_BACKLOG,128)
                       .childOption(ChannelOption.SO_KEEPALIVE,true)
                       .childHandler(new ChannelInitializer<SocketChannel>() {
                           @Override
                           protected void initChannel(SocketChannel ch){
                               ChannelPipeline pipeline = ch.pipeline();
                               pipeline.addLast(group,new EventExcutorHandler());
                           }
                       });
               ChannelFuture sync = bootstrap.bind(PORT).sync();
               sync.channel().closeFuture().sync();
           }catch (Exception e){
               e.printStackTrace();
           }finally {
               worker.shutdownGracefully();
               boss.shutdownGracefully();
           }
       }
   }
   
   // 在handler中加入group线程池，会在executor.inEventLoop()中判断是否含有线程池，是的话会进入next.invokeChannelRead(m);
   static void invokeChannelRead(final AbstractChannelHandlerContext next, Object msg) {
       final Object m = next.pipeline.touch(ObjectUtil.checkNotNull(msg, "msg"), next);
       EventExecutor executor = next.executor();
       if (executor.inEventLoop()) {
           next.invokeChannelRead(m);
       } else {
           executor.execute(new Runnable() {
               @Override
               public void run() {
                   next.invokeChannelRead(m);
               }
           });
       }
   }
   ```

##### 要如何选择两种方式？

1) 第一种方式在 handler 中添加异步，可能更加的自由，比如如果需要访问数据库，那我就异步，如果不需 要，就不异步，异步会拖长接口响应时间。因为需要将任务放进 mpscTask 中。如果 IO 时间很短，task 很多，可能一个循环下来，都没时间执行整个 task，导致响应时间达不到指标。 

2) 第二种方式是 Netty 标准方式(即加入到队列)，但是，这么做会将整个 handler 都交给业务线程池。不论耗时不耗时，都加入到队列里，不够灵活。 

3) 各有优劣，从灵活性考虑，第一种较好

### 实现类讲解

#### BootStrap、ServerBootStrap

以上两个类的区别就是ServerBootStrap是服务器端的引导类，而BootStrap是客户端的引导类，他们的目的是构建一个完成的Netty程序。

##### 常用的方法

###### 服务器端： 

```java
// 该方法用于设置主的Reactor和子Reactor做一个关联
public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) ;
// 用于设置服务器端的通道实现
public B channel(Class<? extends C> channelClass);
// 用于添加 ServerChannel 配置
public <T> B option(ChannelOption<T> option, T value); 
// 用于添加 接收到的通道 配置
public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value);
// 用于设置 接收到的通道进行添加处理器
public ServerBootstrap childHandler(ChannelHandler childHandler);
// 用于设置本机服务端绑定的端口
public ChannelFuture bind(int inetPort);
```

###### 客户端

```java
// 设置客户端绑定的 事件组
public B group(EventLoopGroup group);
// 用于设置服务器端的通道实现
public B channel(Class<? extends C> channelClass);
// 用于添加 ServerChannel 配置
public <T> B option(ChannelOption<T> option, T value); 
// 设置通道的处理器
public B handler(ChannelHandler handler);
// 设置客户端连接的地址和端口
public ChannelFuture connect(String inetHost, int inetPort);
```

#### Future、ChannelFuture

Netty中所有的I0操作都是异步的，不能立刻得知消息是否被正确处理。但是可以过一会等它执行完成或者直接注册一个监听，具体的实现就是通过Future 和ChannelFutures, 他们可以注册一个监听，当操作执行成功或失败时监听会自动触发注册的监听事件

##### 常用方法

```java
// 返回当前具有IO的channel
Channel channel();
// 等待操作异步执行完毕
ChannelFuture sync();
// 同步
ChannelFuture syncUninterruptibly();
// 等待执行完毕
ChannelFuture await() throws InterruptedException;
// 添加一个处理的监听事件
ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener);
// 移除一个监听事件
ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener);
```

#### Channel

1) Netty网络通信的组件，能够用于执行网络I/O 操作。
2) 通过Channel可获得当前网络连接的通道的状态
3) 通过 Channel可获得网络连接的配置参数( 例如接收缓冲区大小)
4) Channel 提供异步的网络I/O 操作(如建立连接，读写，绑定端口)，异步调用意味着任何I/O 调用都将立即返
   回，并且不保证在调用结束时所请求的I/O 操作已完成
5) 调用 立即返回一个ChannelFuture 实例，通过注册监听器到ChannelFuture上， 可以I/O操作成功、失败或取
   消时回调通知调用方
6) 支持关联I/O操作与对应的处理程序
7) 不同协议、 不同的阻塞类型的连接都有不同的Channel 类型与之对应，常用的Channel 类型:
   NioSocketChannel,异步的客户端TCP Socket连接。
   NioServerSocketChannel,异步的服务器端TCP Socket连接。
   NioDatagramChannel,异步的UDP连接。
   NioSctpChannel,异步的客户端Sctp 连接。
   NioSctpServerChannel,异步的Sctp 服务器端连接，这些通道涵盖了UDP 和TCP网络IO以及文件IO。

#### Selector

1) Netty 基于Selector 对象实现I/O 多路复用，通过Selector 一个线程可以监听多个连接的Channel 事件。
2) 当向一个Selector中注册Channel后，Selector内部的机制就可以自动不断地查询(Select)这些注册的Channel是否有已就绪的I/O 事件(例如可读，可写，网络连接完成等)，这样程序就可以很简单地使用一个线程高效地管理多个Channel

#### ChannelHandler极其实现类

ChannelHandler是-一个接口，处理I/O事件或拦截I/O操作，并将其转发到其ChannelPipeline(业 务处理链)中的下一个处理程序。

其实现类如下图：

![](http://image.tinx.top/img20210731134903.png)

+ ChannelInBoundHandler 用于处理进站的IO请求
+ ChannelInBoundHandlerAdapter 用于处理进站的IO请求适配器
+ ChannelOutBoundHandler 用于处理出站的IO请求
+ ChannelOutBoundHandlerAdapter 用于处理出站的IO请求适配器
+ ChannelDuplexBoundHandler 用于处理出/入站的IO请求

我们一般业务会继承 ChannelInBoundHandler 类，重写相关方法，达到业务需求，常用的方法有:

```java
// 通道就绪事件
void channelActive(ChannelHandlerContext ctx) throws Exception;
// 通道空闲状态
void channelInactive(ChannelHandlerContext ctx) throws Exception;
// 通道事件被触发 - 读取事件
void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;
// 通道读取事件完毕
void channelReadComplete(ChannelHandlerContext ctx) throws Exception;
// 用户事件被触发
void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;
// 出现异常
void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
// 信息每一次读取事件
protected abstract void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception;
```

#### Pipeline和ChannelPipeline

1) ChannelPipeline 是一一个Handler 的集合，它负责处理和拦截inbound 或者outbound 的事件和操作，相当于
一个贯穿Netty 的链。(也可以这样理解: ChannelPipeline 是保存ChannelHandler 的List, 用于处理或拦截
Channel的入站事件和出站操作)
2) ChannelPipeline 实现了一种高级形式的拦截过滤器模式，使用户可以完全控制事件的处理方式，以及Channel
中各个的ChannelHandler 如何相互交互
3) 在Netty 中每个Channel 都有且仅有一个ChannelPipeline 与之对应，它们的组成关系如下

![](http://image.tinx.top/img20210731141932.png)

一个Channel包含了-个ChannelPipeline,而ChannelPipeline 中又维护了一个 由ChannelHandlerContext组成的双向链表，并且每个ChannelHandlerContext中又关联着一个ChannelHandler
入站事件和出站事件在一个双向链表中，入站事件会从链表head往后传递到最后一个入站的handler,出站事件会从链表tail往前传递到最前一一个出站的handler,两种类型的handler互不干扰

##### 常用方法

ChannelPipeline addirt(ChanneHande... handlers), 把一个业 务处理类( handler) 添加到链中的第一个位置
ChannelPipeline addL ast(ChannelHandler.. handlers)，把一个业 务处理类(handler)添加到链中的最后 -一个位 置

#### ChannelHandlerContext

1. 保存Channel 相关的所有上下文信息，同时关联一个ChannelHandler 对象
2. ChannelHandlerContext中包含一个具体的事件处理器ChannelHandler，同时ChannelHandlerContext中也绑定了对应的pipeline 和Channel 的信息，方便对ChannelHandler 进行调用.

常用方法：

```java
// 刷新
ChannelHandlerContext flush();
// 获取处理器链路
ChannelPipeline pipeline();
// 刷新写入
public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise);
```

#### ChannelOption

Netty 在创建Channel 实例后，一 般都需要设置ChannelOption 参数

相关参数：

> 1、ChannelOption.SO_BACKLOG
>
> ​     ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数，函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
>
> 2、ChannelOption.SO_REUSEADDR
>
> ​      ChanneOption.SO_REUSEADDR对应于套接字选项中的SO_REUSEADDR，这个参数表示允许重复使用本地地址和端口，
>
> ​      比如，某个服务器进程占用了TCP的80端口进行监听，此时再次监听该端口就会返回错误，使用该参数就可以解决问题，该参数允许共用该端口，这个在服务器程序中比较常使用，
>
> ​      比如某个进程非正常退出，该程序占用的端口可能要被占用一段时间才能允许其他进程使用，而且程序死掉以后，内核一需要一定的时间才能够释放此端口，不设置SO_REUSEADDR就无法正常使用该端口。
>
> 3、ChannelOption.SO_KEEPALIVE
>
> ​      Channeloption.SO_KEEPALIVE参数对应于套接字选项中的SO_KEEPALIVE，该参数用于设置TCP连接，当设置该选项以后，连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。当设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
>
> 4、ChannelOption.SO_SNDBUF和ChannelOption.SO_RCVBUF
>
> ​      ChannelOption.SO_SNDBUF参数对应于套接字选项中的SO_SNDBUF，ChannelOption.SO_RCVBUF参数对应于套接字选项中的SO_RCVBUF这两个参数用于操作接收缓冲区和发送缓冲区的大小，接收缓冲区用于保存网络协议站内收到的数据，直到应用程序读取成功，发送缓冲区用于保存发送数据，直到发送成功。
>
> 5、ChannelOption.SO_LINGER
>
> ​      ChannelOption.SO_LINGER参数对应于套接字选项中的SO_LINGER,Linux内核默认的处理方式是当用户调用close（）方法的时候，函数返回，在可能的情况下，尽量发送数据，不一定保证会发生剩余的数据，造成了数据的不确定性，使用SO_LINGER可以阻塞close()的调用时间，直到数据完全发送
>
> 6、ChannelOption.TCP_NODELAY
>
> ​      ChannelOption.TCP_NODELAY参数对应于套接字选项中的TCP_NODELAY,该参数的使用与Nagle算法有关,Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次,因此在数据包不足的时候会等待其他数据的到了，组装成大的数据包进行发送，虽然该方式有效提高网络的有效负载，但是却造成了延时，而该参数的作用就是禁止使用Nagle算法，使用于小数据即时传输，于TCP_NODELAY相对应的是TCP_CORK，该选项是需要等到发送的数据量最大的时候，一次性发送数据，适用于文件传输。
>
> 7、IP_TOS
>
> IP参数，设置IP头部的Type-of-Service字段，用于描述IP包的优先级和QoS选项。
>
> 8、ALLOW_HALF_CLOSURE
>
> Netty参数，一个连接的远端关闭时本地端是否关闭，默认值为False。值为False时，连接自动关闭；为True时，触发ChannelInboundHandler的userEventTriggered()方法，事件为ChannelInputShutdownEvent。

##### TCP参数表

![](http://image.tinx.top/img20210731143842.png)

![](http://image.tinx.top/img20210731143923.png)

#### EventGroupLoop极其实现类NioEventGroupLoop

1) EventLoopGroup 是一组EventLoop 的抽象, Netty 为了更好的利用多核CPU资源，一般会有多个EventLoop同时工作，每个EventLoop 维护着一个Selector 实例。
2) EventLoopGroup 提供next 接口，可以从组里面按照一 定规则获取其中一个EventLoop来处理任务。在Netty服务器端编程中，我们一般都需要提供两个EventLoopGroup， 例如: BossEventLoopGroup 和WorkerEventL oopGroup。
3) 通常一个服务 端口即一个ServerSocketChannel 对应一个 Selector 和一个EventL oop线程。BossEventLoop负责接收客户端的连接并将SocketChannel 交给WorkerEventL oopGroup来进行I0处理，如下图所示

![](http://image.tinx.top/img20210731144251.png)

+ BossEventLoopGroup通常是一个 单线程的EventLoop, EventLoop 维护着一个注册了ServerSocketChannel的Selector实例BossEventLoop不断轮询Selector将连接事件分离出来
+ 通常是OP ACCEPT事件，然后将接收到的SocketChannel交给WorkerEventLoopGroup
+ WorkerEventLoopGroup会由next选择其中一个EventLoop来将这个SocketChannel注册到其维护的Selector并对其后续的I/O事件进行处

##### EventLoop

**EventLoop** **作为** **Netty** **的核心的运行机制 小结** 

1) 每次执行 ececute 方法都是向队列中添加任务。当第一次添加时就启动线程，执行 run 方法，而 run 方法 

是整个 EventLoop 的核心，就像 EventLoop 的名字一样，Loop Loop ，不停的 Loop ，Loop 做什么呢？做 3 件 

事情。 

+ 调用 selector 的 select 方法，默认阻塞一秒钟，如果有定时任务，则在定时任务剩余时间的基础上在加上 0.5 秒进行阻塞。当执行 execute 方法的时候，也就是添加任务的时候，唤醒 selecor，防止 selecotr 阻塞时间过 长。 

+ 当 selector 返回的时候，回调用 processSelectedKeys 方法对 selectKey 进行处理。 
+ 当 processSelectedKeys 方法执行结束后，则按照 ioRatio 的比例执行 runAllTasks 方法，默认是 IO 任务时间 和非 IO 任务时间是相同的，你也可以根据你的应用特点进行调优 。比如 非 IO 任务比较多，那么你就将ioRatio 调小一点，这样非 IO 任务就能执行的长一点。防止队列积攒过多的任务。

#### Unpooled

这个类是用于专门操作缓冲区的工具类

常用的方法：

```java
// 创建一个新的 big-endian Java 堆缓冲区，初始容量相当小，可按需无限扩展其容量。
public static ByteBuf buffer() ;
// 创建一个新的大端缓冲区，用于包装指定的 {@code array}。对指定数组内容的修改将对返回的缓冲区可见。
public static ByteBuf wrappedBuffer(byte[] array);
// 创建一个新的 big-endian 缓冲区，内容指定的是 array 方法参数的的副本
public static ByteBuf copiedBuffer(byte[] array)
```



### TCP的粘包/拆包问题

#### 什么是TCP的粘包拆包？

> 因为网络存在着各种不确定性，在网络传输的数据存在着断断续续的问题，如下图：
>
> 正常状态：
>
> 接收端正常收到两个数据包，即没有发生拆包和粘包的现象。
>
> ![](http://image.tinx.top/img20210801113733.png)
>
> 粘包：
>
> 接收端只收到一个数据包，由于TCP是不会出现丢包的，所以这一个数据包中包含了发送端发送的两个数据包的信息，这种现象即为粘包。这种情况由于接收端不知道这两个数据包的界限，所以对于接收端来说很难处理。
>
> ![](http://image.tinx.top/img20210801113749.png)
>
> 拆包：
>
> 这种情况有两种表现形式，如下图。接收端收到了两个数据包，但是这两个数据包要么是不完整的，要么就是多出来一块，这种情况即发生了拆包和粘包。这两种情况如果不加特殊处理，对于接收端同样是不好处理的。
>
> ![](http://image.tinx.top/img20210801113815.png)

#### 为什么会发生TCP粘包、拆包？

发生TCP粘包、拆包主要是由于下面一些原因：

- **应用程序写入的数据大于socket缓冲区大小**，这将会发生**拆包。**
- 进行MSS（最大报文长度）大小的TCP分段，当TCP报文长度-TCP头部长度>MSS的时候将发生**拆包。**
- **应用程序写入数据小于socket缓冲区大小，网卡将应用多次写入的数据发送到网络上**，这将会发生**粘包。**
- 接收方法不及时读取socket缓冲区数据，这将发生**粘包。**

#### 粘包、拆包解决办法

TCP本身是面向流的，作为网络服务器，如何从这源源不断涌来的数据流中拆分出或者合并出有意义的信息呢？通常会有以下一些常用的方法：

- 1、发送端给每个数据包添加包首部，首部中应该至少包含数据包的长度，这样接收端在接收到数据后，通过读取包首部的长度字段，便知道每一个数据包的实际长度了。
- 2、发送端将每个数据包封装为固定长度（不够的可以通过补0填充），这样接收端每次从接收缓冲区中读取固定长度的数据就自然而然的把每个数据包拆分开来。
- 3、可以在数据包之间设置边界，如添加特殊符号，这样，接收端通过这个边界就可以将不同的数据包拆分开。

#### Netty解决粘包拆包的方法

[相关源码](./src/main/java/com/wills/netty/chapter5_tcp_coder)

##### 自定义协议包

Netty解决拆包粘包的方法就是自己创建一个 **编码**、**解码**，然后编码解码的对象就是一个带长度头的对象的协议包，示例如下：

```java
@Data
public class WillsProtocol {
    // 关键点！用于对代表这个对象的字节长度，在服务器解析时会首先读取这个len，然后读取特定len长度的字节
    private int len;
    // 协议包的数据，指代要发送的数据本题
    private byte[] content;
}
```

##### 编码器

```java
@Slf4j
public class MyObjectEncoder extends MessageToByteEncoder<WillsProtocol> {


    @Override
    protected void encode(ChannelHandlerContext ctx, WillsProtocol msg, ByteBuf out) throws Exception {
        log.info("编码方法被调用了！");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());
    }
}
```

##### 解码器

```java
@Slf4j
public class MyObjectDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int len = in.readInt();
        byte[] content = new byte[len];
        in.readBytes(content);

        WillsProtocol protocol = new WillsProtocol(len,content);

        out.add(protocol);

        log.info("调用了解码方法");
    }
}
```





### 简单的服务器端与客户端交互

#### 服务端

[服务端](./src/main/java/com/wills/netty/chapter1/server/NettyServer.java)

[服务端处理器](./src/main/java/com/wills/netty/chapter1/server/NettyServerHandler.java)

#### 客户端

[客户端](./src/main/java/com/wills/netty/chapter1/client/NettyClient.java)

[客户端处理器](./src/main/java/com/wills/netty/chapter1/client/NettyClientHandler.java)

#### 效果图

![](http://image.tinx.top/img20210731122119.png)

![](http://image.tinx.top/img20210731122135.png)

### Http服务器

[服务端](./src/main/java/com/wills/netty/chapter2_http/NettyServer.java)

[服务端处理器](./src/main/java/com/wills/netty/chapter2_http/MySolutionHandler.java)

#### 效果图

![](http://image.tinx.top/img20210731122406.png)

### 聊天室

#### 服务端

[服务端](./src/main/java/com/wills/netty/chapter3_chat/server/NettyChatServer.java)

[服务端处理器](./src/main/java/com/wills/netty/chapter3_chat/server/ChatServerHandler.java)

[心跳处理器](./src/main/java/com/wills/netty/chapter3_chat/server/MyIdleHandler.java)

#### 客户端

[客户端](./src/main/java/com/wills/netty/chapter3_chat/client/NettyChatClient.java)

[客户端处理器](./src/main/java/com/wills/netty/chapter3_chat/client/NettyChatClientHandler.java)

#### 效果图

![](http://image.tinx.top/img20210731124253.png)



### 简易RPC

> 本案例是基于 springboot 网络项目来设计，分为三个模块：
>
> + rpc-common
>
>   > 通用类/工具集的模块
>
> + rpc-producer
>
>   > 服务被调用者模块
>
> + rpc-consumer
>
>   > 服务调用者模块

#### 通用类模块

>  本类中主要写了一些公用的类，比如说api包下的调用的 HandlerService接口，以及entity下使用的通用的实体类等。

#### 生产者模块(服务被调用者)

> Annotation: 模块中使用的注解
>
> entity： 模块使用的实体类
>
> Netty: RPC模块的基石
>
> Service: 实现了rpc-common通用模块api包下的HandlerService类

##### 步骤

> 基本步骤就是，使用@Rpc注解(作用范围是类上)，实现rpc-common包下面的HandlerService接口，重写其方法，然后将@Rpc注解写到这个类上面，别的东西就按springboot开发项目来搞定，特别就是 服务器netty的处理类继承ApplicationContextAware(当容器就绪以后，调用这个接口实现类下的setApplicationContext方法)，用途是缓存@Rpc类的bean，然后在接收到客户端发送的消息时，调用自己写的handler方法，该方法就是使用反射创建上面缓存的bean类中的方法，进行值返回,然后回馈客户端([见后文](#消费者模块(服务调用者)))

###### 1. 实现通用类下面的接口

[HandlerServiceImpl](./rpc-provider/src/main/java/com/wills/rpc/provider/service/impl/HandlerServiceImpl.java)

[实现接口类下用到的注解- @Rpc](./rpc-provider/src/main/java/com/wills/rpc/provider/annotation/RPC.java)

[项目临时存储(使用自旋锁实现的单例模式实体类)](./rpc-provider/src/main/java/com/wills/rpc/provider/entity/Singleton.java)

###### 2. 实现Netty以及处理类

[服务器类](./rpc-provider/src/main/java/com/wills/rpc/provider/netty/NettyServer.java)

[服务器处理类](./rpc-provider/src/main/java/com/wills/rpc/provider/netty/handler/ServerHandler.java)

#### 消费者模块(服务调用者)

> Annotation: 模块中使用的注解
>
> entity： 模块使用的实体类
>
> Netty: RPC模块的基石
>
> Service: 实现了rpc-common通用模块api包下的HandlerService类
>
> Controller: 暴露到web上的接口，提供用户增加/查询功能
>
> proxy: 代理类的实现，用于构造代理类，使用注解后，自动添加代理向 服务提供者进行相关请求
>
> processor: 注入Bean处理，在每次调用方法时，会自动调用这个postProcessAfterInitialization重写的方法

##### 步骤

> 基本的步骤就是，使用@RpcReturn注解(作用范围是类中的字段)，然后在处理类中 实现 BeanPostProcessor 接口，该接口的意义就是bean在注入后调用的方法，将添加了@RpcReturn注解的bean中的字段，进行代理，代理以后进行字段注入, 在代理类中创建一个 getProxy方法，进行相关的判断，如果代理类没有被缓存过，那么就进行使用 Proxy.newProxyInstance方法进行代理创建，如果被调用 invoke 了以后，就会进行发送 rpc请求，达到了进行rpc请求远程类，达到了无感请求rpc的效果

###### 1. 暴露接口级注解实现

[暴露接口](./rpc-consumer/src/main/java/com/wills/rpc/consumer/controller/UserController.java)

[注解@RpcReturn](./rpc-consumer/src/main/java/com/wills/rpc/consumer/annotation/RpcReturn.java)

###### 2. 实现Netty以及处理类

[客户端类](./rpc-consumer/src/main/java/com/wills/rpc/consumer/netty/NettyClient.java)

[客户端处理类](./rpc-consumer/src/main/java/com/wills/rpc/consumer/netty/handler/ClientHandler.java)

###### 3. 代理类实现

[代理类RpcProxy](./rpc-consumer/src/main/java/com/wills/rpc/consumer/proxy/RpcProxy.java)

###### 4. 处理类实现

[处理类](./rpc-consumer/src/main/java/com/wills/rpc/consumer/prossor/WillsProcessor.java)

#### 效果图

![](http://image.tinx.top/20210809152356.png)



![](http://image.tinx.top/20210809152435.png)



![](http://image.tinx.top/20210809152457.png)



# Thread

待更新

# BigData

待更新

## Hive

待更新

## HBase

待更新

## Spark

待更新

## Flink

待更新