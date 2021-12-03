package com.github.dc.im.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author PeiYuan
 */
@Slf4j
@UtilityClass
public class EntityUtil {


    /**
     * 根据class实例化bean
     *
     * @param eClass bean的class
     * @param <E>    bean类型
     * @return bean
     */
    public static <E> E instance(Class<E> eClass) {
        if (eClass == null) {
            return null;
        }

        E entity = null;
        try {
            entity = eClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.warn("instance error:".concat(String.valueOf(eClass)), e);
        }

        return entity;
    }
}
