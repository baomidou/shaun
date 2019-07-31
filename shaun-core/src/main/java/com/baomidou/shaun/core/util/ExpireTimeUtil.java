package com.baomidou.shaun.core.util;

import org.springframework.lang.NonNull;

import java.util.Date;

/**
 * @author miemie
 * @since 2019-07-22
 */
public abstract class ExpireTimeUtil {

    /**
     * 获取超时时间 Date
     *
     * @param expireTime 表达式
     * @return 到期时间
     */
    @NonNull
    public static Date getTargetDate(Integer expireTime) {
        return new Date(System.currentTimeMillis() + expireTime);
    }
}