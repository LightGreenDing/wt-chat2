package com.chat.wtchat.controller;

import com.chat.wtchat.model.MessageInfo;
import com.chat.wtchat.result.Result;
import com.chat.wtchat.result.ResultGenerator;
import com.chat.wtchat.service.RoomService;
import com.chat.wtchat.task.ThreadPoolTaskExecutorEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private ThreadPoolTaskExecutorEx threadPoolTaskExecutorEx;


    /**
     * 测试显示
     */
    @RequestMapping("/index")
    public String index() {
        return "--------微投聊天室启动成功--------";
    }

    /**
     * 将推送消息发送到到房间
     *
     * @param message 要推送到聊天室内容
     */
    @RequestMapping("/sendMessageToRoom")
    public Result sendMessageToRoom(MessageInfo message) {
        if (null == message.getRoomId() || null == message.getPlatform()) {
            return ResultGenerator.failResult("roomId或者platform为null");
        }
        threadPoolTaskExecutorEx.execute(() -> roomService.sendMessageToRoom(message));
        return ResultGenerator.successResult();
    }

    /**
     * 将中奖消息发送到到房间
     *
     * @param message 要推送到聊天室内容
     */
    @RequestMapping("/sendWinningToRoom")
    public Result sendWinningToRoom(@RequestBody MessageInfo message) {
        if (null == message.getRoomId() || null == message.getPlatform()) {
            return ResultGenerator.failResult("roomId或者platform为null");
        }
        threadPoolTaskExecutorEx.execute(() -> roomService.sendWinningToRoom(message));
        return ResultGenerator.successResult();
    }
}
