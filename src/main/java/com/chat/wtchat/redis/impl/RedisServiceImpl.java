package com.chat.wtchat.redis.impl;

import com.chat.wtchat.model.Platform;
import com.chat.wtchat.model.Room;
import com.chat.wtchat.redis.RedisService;
import com.chat.wtchat.service.PlatformService;
import com.chat.wtchat.service.RoomService;
import com.chat.wtchat.utils.SocketConstant;
import com.chat.wtchat.utils.SpringBeanFactoryUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis接口实现类
 *
 * @author Zed
 */
@Service
public class RedisServiceImpl implements RedisService<String, Object> {
    @Resource
    private RoomService roomService;
    @Resource
    private PlatformService platformService;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void roomCache() {
        if (roomService == null) {
            roomService = SpringBeanFactoryUtil.getBean(RoomService.class);
        }
        List<Room> list = roomService.getAll();
        Map<String, Object> roomMap = new HashMap<>(list.size());
        list.forEach(room -> {
            roomMap.put(String.valueOf(room.getGameType()), room);
        });
        addMap(SocketConstant.SOCKET_ROOM, roomMap);
    }

    @Override
    public void platformCache() {
        if (platformService == null) {
            platformService = SpringBeanFactoryUtil.getBean(PlatformService.class);
        }
        List<Platform> list = platformService.getAll();
        Map<String, Object> platformMap = new HashMap<>(list.size());
        list.forEach(platform -> {
            platformMap.put(platform.getCode(), platform);
        });
        addMap(SocketConstant.PLATFORM, platformMap);
    }

    @Override
    public void addMap(String key, Map<String, Object> map) {
        redisTemplate.boundHashOps(key).putAll(map);
    }

    @Override
    public <T> T getMapField(String key, String field) {
        return (T) redisTemplate.boundHashOps(key).get(field);
    }

}
