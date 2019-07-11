package com.chat.wtchat.exception;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 聊天室异常
 */
public class MessageExceptionListener extends ExceptionListenerAdapter {
    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
        client.disconnect();
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        System.out.println("clientId:[{}]用户断开连接发生异常" + e.getMessage());
        client.disconnect();
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        System.out.println("用户连接发生异常" + e.getMessage());
        client.disconnect();
    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        System.out.println("信道异常" + e.getMessage());
        ctx.close();
        return true;
    }
}
