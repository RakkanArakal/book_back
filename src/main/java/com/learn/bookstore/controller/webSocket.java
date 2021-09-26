package com.learn.bookstore.controller;

import com.learn.bookstore.config.WebSocketConfig;
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
@ServerEndpoint(value = "/game/test/{token}",configurator = WebSocketConfig.class)
@RestController
public class webSocket {

    private static final AtomicInteger onlineCount = new AtomicInteger(0);

    private static  Map<String, Session> clients = new ConcurrentHashMap<String, Session>();
    public static Map<Session,String> onlineUser = new ConcurrentHashMap<Session, String>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userName") String userName) {

        int cnt = onlineCount.incrementAndGet();
        clients.put(session.getId(),session);
        onlineUser.put(session,userName);
        log.info("有新连接加入：{}，当前在线人数为：{}", session.getId(), cnt);

    }


    @OnClose
    public void onClose(Session session) {

        int cnt = onlineCount.decrementAndGet(); // 在线数减1
        clients.remove(session.getId());
        onlineUser.remove(session);

        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), cnt);
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
        String[] splitMessage=message.split("#");
        int playerNow = 0;
        int argu1 = 0;
        if(splitMessage.length > 1 && !splitMessage[0].equals("endTurn"))
            argu1 = Integer.parseInt(splitMessage[1]);
        try {
//            switch (splitMessage[0]){
//                case "createRoom":  createRoom(session);
//                    return;
//                case "searchRoom":  searchRoom(session);
//                    return;
//                case "exitRoom":    exitRoom(argu1,session);
//                    return;
//                case "joinRoom":    joinRoom(argu1,session);
//                    return;
////                case "getCard":     gameRun(rid,session,seat,1);
////                    return;
//                case "useCard": {
//                    playerNow = allGames.get(rid).getPlayerNow();
//                    if (seat != playerNow){
//                        sendMessageBack(MsgUtil.makeMsg(-100,"操作失败"),session);
//                        break;
//                    }
//                    gameRun(rid, session, seat, 200 + argu1);
//                    return;
//                }
//                case "endTurn": {
//                    playerNow = allGames.get(rid).getPlayerNow();
//                    if(splitMessage.length > 1){
//                        List<Integer> discardList = com.alibaba.fastjson.JSONArray.parseArray(splitMessage[1],Integer.class);
//                        disCarded(rid,session,seat,discardList);
//                    }
//                    if (seat != playerNow){
//                        sendMessageBack(MsgUtil.makeMsg(-100,"操作失败"),session);
//                        break;
//                    }
//                    gameRun(rid, session, seat, 3);
//                    return;
//                }
////                case "update":
////                    gameRun(rid,session,seat,3);
////                    return;
//                case "useSkill": useSkill(rid,session,seat);
//                    return;
//                default:
//                    break;
//            }
        }catch (Exception e){
            log.info("发生错误");
            e.printStackTrace();
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
    private void sendMessageBack(JSONObject message, Session fromSession) {
        try {
            fromSession.getBasicRemote().sendText(message.toString());
            log.info("服务端给客户端[{}]发送消息:{}", fromSession.getId(), message.toString());
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