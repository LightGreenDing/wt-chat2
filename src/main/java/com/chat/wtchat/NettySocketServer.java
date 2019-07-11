package com.chat.wtchat;

import com.chat.wtchat.config.NettySocketConfig;
import com.chat.wtchat.exception.MessageExceptionListener;
import com.chat.wtchat.redis.RedisService;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.store.RedissonStoreFactory;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * nettySocket 服务
 *
 * @author Zed
 */
@Component
public class NettySocketServer implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(NettySocketServer.class);
    private SocketIOServer server;
    private PubSubStore pubSubStore;
//    private SocketIONamespace chat1namespace;

    @Autowired
    private NettySocketConfig nettySocketConfig;
    @Autowired
    private RedissonStoreFactory redissonStoreFactory;
    @Autowired
    private RedisService redisService;


    @Bean
    public SocketIOServer socketServer() {
        /*
         * 创建Socket，并设置监听端口
         */
        Configuration config = new Configuration();
        //设置主机名
        config.setHostname(nettySocketConfig.getHost());
        //设置监听端口
        config.setPort(nettySocketConfig.getPort());
        config.setStoreFactory(redissonStoreFactory);
        config.setExceptionListener(new MessageExceptionListener());
        // 协议升级超时时间（毫秒），默认10秒。HTTP握手升级为ws协议超时时间
//        config.setUpgradeTimeout(10000);
        // Ping消息超时时间（毫秒），默认60秒，这个时间间隔内没有接收到心跳消息就会发送超时事件
//        config.setPingTimeout(180000);
        // Ping消息间隔（毫秒），默认25秒。客户端向服务器发送一条心跳消息间隔
//        config.setPingInterval(60000);
        config.setAuthorizationListener(handshakeData -> true);
        //工作线程数据
        config.setWorkerThreads(50);
        config.setTransports(Transport.POLLING, Transport.WEBSOCKET);
        //跨域
        config.setOrigin(null);
        config.getSocketConfig().setReuseAddress(true);
        config.getSocketConfig().setSoLinger(0);
        config.getSocketConfig().setTcpKeepAlive(true);
        server = new SocketIOServer(config);
        pubSubStore = server.getConfiguration().getStoreFactory().pubSubStore();
        return server;
    }

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(nettySocketConfig.getSingleRedis());
        return Redisson.create(config);
    }

    @Bean
    RedissonStoreFactory redissonStoreFactory(RedissonClient redissonClient) {
        return new RedissonStoreFactory(redissonClient);
    }

    @Bean
    public PubSubStore pubSubStore() {
        return pubSubStore;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }

    @PreDestroy
    public void destory() {
        redissonStoreFactory.shutdown();
        server.stop();
    }

    @Override
    public void run(String... args) {
        //缓存房间信息
        redisService.roomCache();
        //缓存平台
        redisService.platformCache();

        server.start();
        System.out.println("=====微投聊天室启动成功,Port:" + nettySocketConfig.getPort() + "=====");
    }
}
