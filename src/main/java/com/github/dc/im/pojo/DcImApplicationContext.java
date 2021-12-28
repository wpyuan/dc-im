package com.github.dc.im.pojo;

import com.github.dc.im.handler.DefaultRecordChatContentHandler;
import com.github.dc.im.handler.DefaultSocketAuthHandler;
import com.github.dc.im.handler.RecordChatContentHandler;
import com.github.dc.im.handler.WebSocketAuthHandler;
import com.github.dc.im.send.DefaultOfflineMessageSender;
import com.github.dc.im.send.OfflineMessageSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *     dc-im应用上下文
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 8:46
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DcImApplicationContext implements Serializable {
    /**
     * 用户认证处理器
     */
    @Builder.Default
    private WebSocketAuthHandler webSocketAuthHandler = new DefaultSocketAuthHandler();
    /**
     * 聊天数据持久化处理器
     */
    @Builder.Default
    private RecordChatContentHandler recordChatContentHandler = new DefaultRecordChatContentHandler();
    /**
     * 离线消息发送器
     */
    @Builder.Default
    private OfflineMessageSender offlineMessageSender = new DefaultOfflineMessageSender();
}
