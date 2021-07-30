

[toc]

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

待更新

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