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
      * [Unpooled](#unpooled)
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