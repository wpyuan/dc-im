package com.github.dc.im.handler;

import com.github.dc.im.pojo.ChatContentData;

/**
 * <p>
 *     聊天内容持久化处理器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/9 17:19
 */
public interface RecordChatContentHandler {
    /**
     * 聊天内容持久化处理
     * @param chatContentData 聊天内容
     */
    void handle(ChatContentData chatContentData);
}
