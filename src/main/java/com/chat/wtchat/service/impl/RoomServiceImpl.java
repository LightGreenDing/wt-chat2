package com.chat.wtchat.service.impl;

import com.chat.wtchat.dao.RoomDao;
import com.chat.wtchat.model.MessageInfo;
import com.chat.wtchat.model.Room;
import com.chat.wtchat.service.MessageInfoService;
import com.chat.wtchat.service.RoomService;
import com.chat.wtchat.utils.SocketConstant;
import com.chat.wtchat.utils.SpringBeanFactoryUtil;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 房间接口实现类
 *
 * @author Zed
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Resource
    private RoomDao roomDao;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private MessageInfoService messageInfoService;

    @Override
    public List<Room> getAll() {
        return roomDao.findAll();
    }

    @Override
    public void sendMessageToRoom(MessageInfo message) {
        message.setType(SocketConstant.C_Chat_C);
        message.setTime(new Date());
        messageInfoService.save(message);
        //然后发送给订阅的其他客户端
        if (redissonClient == null) {
            redissonClient = SpringBeanFactoryUtil.getBean(RedissonClient.class);
        }
        RTopic topic = redissonClient.getTopic(SocketConstant.BROADCAST, new SerializationCodec());
        topic.publish(message);
    }

    @Override
    public void sendWinningToRoom(MessageInfo message) {
        message.setType(SocketConstant.WINNING);
        message.setTime(new Date());
        //然后派送给订阅的其他服务器
        if (redissonClient == null) {
            redissonClient = SpringBeanFactoryUtil.getBean(RedissonClient.class);
        }
        RTopic topic = redissonClient.getTopic(SocketConstant.BROADCAST, new SerializationCodec());
        topic.publish(message);
    }
}
