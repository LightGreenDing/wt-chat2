package com.chat.wtchat.service;

import com.chat.wtchat.model.MessageInfo;
import com.chat.wtchat.model.Room;

import java.util.List;

/**
 * 房间接口
 *
 * @author Zed
 */
public interface RoomService {
    /**
     * 获取所有房间
     *
     * @return 房间集合
     */
    List<Room> getAll();

    /**
     * 推送注单消息到聊天室
     *  @param message        消息内容封装
     *
     */
    void sendMessageToRoom(MessageInfo message);

    /**
     * 推送中奖消息到聊天室
     *  @param message        消息内容封装
     *
     */
    void sendWinningToRoom(MessageInfo message);
}
