package com.learn.bookstore.controller;

import com.learn.bookstore.config.WebSocketConfig;
import com.learn.bookstore.utils.msgutils.Msg;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@ServerEndpoint(value = "/chatRoom/{userName}",configurator = WebSocketConfig.class)
@RestController
public class webSocket {

    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    private static  Map<String, Session> clients = new ConcurrentHashMap<String, Session>();
    public static Map<String,String> onlineUser = new ConcurrentHashMap<String, String>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {

        int cnt = onlineCount.incrementAndGet();
        clients.put(session.getId(),session);
        onlineUser.put(session.getId(),userName);
        broadcast("join",userName);
        log.info("有新连接加入：{}，当前在线人数为：{},在线用户有{}", session.getId(), cnt,onlineUser);

    }


    @OnClose
    public void onClose(Session session) {

        int cnt = onlineCount.decrementAndGet();
        clients.remove(session.getId());
        onlineUser.remove(session.getId());

        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), cnt);
    }


    @OnMessage
    public void onMessage(String message, Session session)  {
        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);

        JSONObject jsonObject = JSONObject.fromObject(message);
        String msg = (String) jsonObject.get("msg");
        String type = (String) jsonObject.get("type");
        try {
            switch (type){
                case "leave":  {
                    clients.remove(session.getId());
                    onlineUser.remove(session.getId());
                }
                    break;
                case "msg":  {
                    msg = onlineUser.get(session.getId()) + ":" + msg;
                }
                    break;

                default:
                    return;

            }
            //广播
            broadcast(type,msg);
        }catch (Exception e){
            log.info("发生错误");
            e.printStackTrace();
        }
    }

    private void broadcast(String type, String msg ) {

        switch (type){
            case "join" : {
                msg += "加入了聊天室";
            }   break;
            case "leave" : {
                msg += "离开了聊天室";
            }   break;
            default:
                break;
        }
        for(Map.Entry<String,Session> client:clients.entrySet()){
            sendMessageBack(msg,client.getValue());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端单独返回消息消息给请求的客户端
     */
    private void sendMessageBack(JSONObject message, Session toSession) {
        try {
            toSession.getBasicRemote().sendText(message.toString());
            log.info("服务端给客户端[{}]发送消息:{}", toSession.getId(), message.toString());
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：", e);
        }
    }

    private void sendMessageBack(String message, Session toSession) {
        try {
            toSession.getBasicRemote().sendText(message);
            log.info("服务端给客户端[{}]发送消息:{}", toSession.getId(), message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败：", e);
        }
    }

    public class ExceptionMessage extends Exception {
        public ExceptionMessage(String message) {
            super(message);
        }
    }

}