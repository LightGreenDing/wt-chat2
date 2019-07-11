package com.chat.wtchat.handler;

import com.chat.wtchat.config.NettySocketConfig;
import com.chat.wtchat.model.MessageInfo;
import com.chat.wtchat.redis.RedisService;
import com.chat.wtchat.service.MessageInfoService;
import com.chat.wtchat.task.ThreadPoolTaskExecutorEx;
import com.chat.wtchat.utils.SocketConstant;
import com.chat.wtchat.utils.SpringBeanFactoryUtil;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;


/**
 * 聊天室交互服务
 * @author Zed
 */
@Component
public class ChatEventHandler {
    @Autowired
    private NettySocketConfig nettySocketConfig;

    private Logger logger = LoggerFactory.getLogger(ChatEventHandler.class);

    @Autowired
    private SocketIOServer server;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private ThreadPoolTaskExecutorEx threadPoolTaskExecutorEx;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MessageInfoService messageInfoService;

    @PostConstruct
    public void init() {

        RTopic topic = redissonClient.getTopic(SocketConstant.BROADCAST, new SerializationCodec());
        topic.addListener(MessageInfo.class, (channel, msg) -> {
            BroadcastOperations roomOperations = server.getRoomOperations(msg.getPlatform() + "_" + msg.getRoomId());
            roomOperations.sendEvent(msg.getType(), msg);
            logger.info("当前服务器端口:{},订阅服务器其他服务信息:{}", nettySocketConfig.getPort(), msg.getMessage());
        });

    }

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String platform = client.getHandshakeData().getSingleUrlParam(SocketConstant.PLATFORM);
        String roomId = client.getHandshakeData().getSingleUrlParam(SocketConstant.ROOM_ID);
        if (null == roomId || null == platform) {
            client.disconnect();
        }
        if (redisService == null) {
            redisService = SpringBeanFactoryUtil.getBean(RedisService.class);
        }
        Object mapField = redisService.getMapField(SocketConstant.SOCKET_ROOM, roomId);
        Object platformField = redisService.getMapField(SocketConstant.PLATFORM, platform);
        if (null == mapField && null == platformField) {
            client.disconnect();
        }
        String s = platform + "_" + roomId;
        client.joinRoom(s);
        //异步获取历史记录发送记录到房间
        threadPoolTaskExecutorEx.execute(() -> {
            List<MessageInfo> historyByRoomId = messageInfoService.findHistoryByRoomId(platform, roomId, 30);
            client.sendEvent(SocketConstant.GET_HISTORY_MESSAGE, historyByRoomId);
        });
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Set<String> allRooms = client.getAllRooms();
        allRooms.forEach(client::leaveRoom);
        //客户端离开房间,关闭连接
        client.disconnect();
    }

}
