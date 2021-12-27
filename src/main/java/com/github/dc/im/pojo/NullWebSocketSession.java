package com.github.dc.im.pojo;

import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.AbstractWebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     WebSocketSession默认空实现，当找不到会话时，返回此类，可通过getAttributes方法取出身份信息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/27 10:46
 */
public class NullWebSocketSession extends AbstractWebSocketSession {

    public NullWebSocketSession(String key, UserInfoData userInfoData) {
        super(new HashMap<>(2));
        if (key != null) {
            this.getAttributes().put("openId", key);
        }
        if (userInfoData != null) {
            this.getAttributes().put("userInfo", userInfoData);
        }
    }

    public NullWebSocketSession(String key) {
        super(new HashMap<>(1));
        if (key != null) {
            this.getAttributes().put("openId", key);
        }
    }

    public NullWebSocketSession(UserInfoData userInfoData) {
        super(new HashMap<>(1));
        if (userInfoData != null) {
            this.getAttributes().put("userInfo", userInfoData);
        }
    }

    public NullWebSocketSession(Map attributes) {
        super(attributes);
    }

    @Override
    protected void sendTextMessage(TextMessage textMessage) throws IOException {

    }

    @Override
    protected void sendBinaryMessage(BinaryMessage binaryMessage) throws IOException {

    }

    @Override
    protected void sendPingMessage(PingMessage pingMessage) throws IOException {

    }

    @Override
    protected void sendPongMessage(PongMessage pongMessage) throws IOException {

    }

    @Override
    protected void closeInternal(CloseStatus closeStatus) throws IOException {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public URI getUri() {
        return null;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return null;
    }

    @Override
    public Principal getPrincipal() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public String getAcceptedProtocol() {
        return null;
    }

    @Override
    public void setTextMessageSizeLimit(int i) {

    }

    @Override
    public int getTextMessageSizeLimit() {
        return 0;
    }

    @Override
    public void setBinaryMessageSizeLimit(int i) {

    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return 0;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
