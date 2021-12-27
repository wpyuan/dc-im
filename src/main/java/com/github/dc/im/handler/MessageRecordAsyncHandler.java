package com.github.dc.im.handler;

import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.pojo.ChatContentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     异步消息持久化处理器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 17:57
 */
@Component
@Slf4j
public class MessageRecordAsyncHandler {

    @Async("dcImAsync")
    public void recordData(ChatContentData chatContentData) {
        log.trace("聊天数据持久化处理：{}", chatContentData);
        RecordChatContentHandler recordChatContentHandler = ApplicationContextHelper.getBean(RecordChatContentHandler.class);
        if (recordChatContentHandler != null) {
            recordChatContentHandler.handle(chatContentData);
        }
    }
}
