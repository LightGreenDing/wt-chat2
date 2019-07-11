package com.chat.wtchat.model;


import com.corundumstudio.socketio.store.pubsub.PubSubMessage;

import java.util.Date;

/**
 * 聊天消息
 *
 * @author Zed
 */
public class MessageInfo extends PubSubMessage {
    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 消息内容
     */
    private Object message;
    /**
     * 时间
     */
    private Date time;
    /**
     * 平台
     */
    private String platform;
    /**
     * 消息类型
     */
    private String type;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public MessageInfo() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "roomId='" + roomId + '\'' +
                ", message='" + message + '\'' +
                ", time=" + time +
                ", platform='" + platform + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
