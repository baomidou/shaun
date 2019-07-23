package com.baomidou.ezpac4j.core.util;

import java.util.Date;

import org.springframework.lang.NonNull;

import com.baomidou.pac4jplus.enums.ExpireType;

/**
 * @author miemie
 * @since 2019-07-22
 */
public abstract class ExpireTimeUtil {

    /**
     * 获取超时时间
     *
     * @param strExp 表达式
     * @return 时间, 秒级
     */
    @NonNull
    public static Integer getExpireTime(ExpireType type, String strExp) {
        return type.getSecond(strExp);
    }

    /**
     * 获取超时时间
     *
     * @param strExp 表达式
     * @return 到期时间
     */
    @NonNull
    public static Date getTargetDate(ExpireType type, String strExp) {
        long second = (long) type.getSecond(strExp);
        return new Date(System.currentTimeMillis() + second);
    }
}
