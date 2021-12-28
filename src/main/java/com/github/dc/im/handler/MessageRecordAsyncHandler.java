package com.github.dc.im.handler;

import com.github.dc.im.pojo.ChatContentData;
import com.github.dc.im.pojo.DcImApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private DcImApplicationContext dcImApplicationContext;

    @Async("dcImAsync")
    public void recordData(ChatContentData chatContentData) {
        log.trace("聊天数据持久化处理：{}", chatContentData);
        RecordChatContentHandler recordChatContentHandler = dcImApplicationContext.getRecordChatContentHandler();
        recordChatContentHandler.handle(chatContentData);
    }
}
