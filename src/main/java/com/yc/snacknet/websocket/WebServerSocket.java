package com.yc.snacknet.websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@Component
@ServerEndpoint("/websocket/{id}")
public class WebServerSocket {
	private static int onlineCount = 0;
	private static CopyOnWriteArraySet<WebServerSocket> webSocketSet = new CopyOnWriteArraySet<WebServerSocket>();
	private Session session; // WebSocket中的Session
	private String usid;

	/**
	 * 上线时
	 * @param session
	 * @param usid
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("id")String usid) {
		this.session = session;
		this.usid = usid;

		webSocketSet.add(this);
		addOnlineCount(); // 在线人数+1
		sendMessage("连接websocket服务器成功..."); // 给客户端一个响应
		System.out.println("有一个新用户 " + usid + "连接上来了...，当前在线用户人数为：" + onlineCount);	
	}

	/**
	 * 下线时
	 */
	@OnClose
	public void onClose() {
		webSocketSet.remove(this);
		subOnlineCount(); // 在线人数-1
		System.out.println("用户 " + usid + "下线了...，当前在线用户人数为：" + onlineCount);	

	}

	/**
	 * 接收来自客户端的信息
	 * @param msg
	 * @param session
	 */
	@OnMessage
	public void onMessage(String msg, Session session) {
		System.out.println("收到来自 " + usid + " 的信息，内容为：" + msg);
		
		for (WebServerSocket wss : webSocketSet) {
			wss.sendMessage(msg);
		}
	}
	
	@OnError
	public void onError(Session session, Throwable error) {
		error.printStackTrace();
	}

	/**
	 * 发送信息的方法
	 * @param msg
	 */
	public void sendMessage(String msg) {
		try {
			this.session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void subOnlineCount() {
		WebServerSocket.onlineCount --;
	}

	private synchronized void addOnlineCount() {
		WebServerSocket.onlineCount ++;
	}
	
	/**
	 * 根据用户id获取会话对象
	 * @param usid
	 * @return
	 */
	public static WebServerSocket getWebSocket(String usid) {
		if (webSocketSet.isEmpty()) {
			return null;
		}
		
		for (WebServerSocket wss : webSocketSet) {
			if (usid.equals(wss.usid)) {
				return wss;
			}
		}
		return null;
	}
}
