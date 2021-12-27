package com.github.dc.im.enums;

import java.util.Arrays;

/**
 * <p>
 *     发送可靠性
 *
 *      • at most once：消息可能会丢，但绝不会重复传输。
 *      • at least once：消息绝不会丢，但可能会重复传输。
 *      • exactly once：每条消息肯定会被传输一次且仅传输一次。
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 17:33
 */
public enum SendReliability {

    /**
     * 消息可能会丢，但绝不会重复传输
     */
    ONCE("at most once"),
    /**
     * 消息绝不会丢，但可能会重复传输。
     */
    LEAST_ONCE("at least once"),
    /**
     * 每条消息肯定会被传输一次且仅传输一次。
     */
    EXACTLY_ONCE("exactly once");

    private String level;

    SendReliability(String level) {
        this.level = level;
    }

    public String level() {
        return this.level;
    }

    public static SendReliability getLevel(String level) {
        return Arrays.stream(SendReliability.values()).filter(t -> t.level.equals(level)).findFirst().orElseThrow(()-> new NullPointerException("无对应 发送可靠性 级别：" + level));
    }
}
