package com.github.dc.im.handler;

import com.github.dc.im.pojo.ChatContentData;

/**
 * <p>
 *     默认聊天记录持久化处理器，默认是不做持久化处理
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 8:59
 */
public class DefaultRecordChatContentHandler implements RecordChatContentHandler {

    @Override
    public void handle(ChatContentData chatContentData) {
    }
}
