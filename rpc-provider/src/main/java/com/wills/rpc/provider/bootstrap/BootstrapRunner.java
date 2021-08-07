package com.wills.rpc.provider.bootstrap;

import com.wills.rpc.provider.netty.NettyServer;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目开始运行后的执行动作
 */
@Component
@Slf4j
@Order(value = 1)
public class BootstrapRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        new Thread(new NettyServer()).start();
    }

}


