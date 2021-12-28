package com.github.dc.im.constant;

/**
 * <p>
 *     常量参数
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 9:59
 */
public interface ConstantArgs {

    /**
     * ws会话参数
     */
    interface WebSocketSession {
        /**
         * 用户信息
         */
        String USER_INFO = "userInfo";
        /**
         * 凭证
         */
        String KEY = "openId";
    }

    /**
     * 消息参数
     */
    interface TextMessage {
        /**
         * Payload参数
         */
        interface Payload {
            /**
             * 目标用户
             */
            String TO = "to";
            /**
             * 目标对象
             */
            interface To {
                /**
                 * 目标：服务器
                 */
                String SERVER = "server";
            }
            /**
             * 动作类型
             */
            String ACTION = "action";
            /**
             * 动作
             */
            interface Action {
                /**
                 * 获取用户信息
                 */
                String GET_USERS = "getUsers";
            }
            /**
             * 主体内容
             */
            String CONTENT = "content";
        }
    }

    /**
     * 同步缓存
     */
    interface SyncCache {
        /**
         * 操作
         */
        interface Opt {
            String PUT = "put";
            String REMOVE = "remove";
        }
    }

}
