/*
 * Copyright 2019-2024 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.util;

import com.baomidou.shaun.core.exception.ShaunException;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @author miemie
 * @since 2019-07-22
 */
public abstract class ExpireTimeUtil {

    private static final String SECOND = "s";
    private static final String MINUTE = "m";
    private static final String HOUR = "h";
    private static final String DAY = "d";

    /**
     * 获取超时时间 Date
     *
     * @param expireTime 表达式
     * @return 到期时间
     */
    @NonNull
    public static Date getTargetDate(final String expireTime) {
        return getTargetDate(new Date(), expireTime);
    }

    /**
     * 获取超时时间 Date
     * <p>
     * 10s : 表示10秒有效
     * 10m 结尾: 表示10分钟有效
     * 10h 结尾: 表示10小时有效
     * <p>
     * 1d : 表示有效时间到第二天 00:00:00
     * 2d1h : 表示有效时间到第三天 01:00:00 <br>
     * `d` 后面 只支持上面三个(`s`,`m`,`h`)之一
     *
     * @param expireTime 表达式
     * @return 到期时间
     */
    @NonNull
    public static Date getTargetDate(Date beginTime, final String expireTime) {
        try {
            int index = 0;
            if ((index = expireTime.indexOf(DAY)) < 1) {
                /* 不包含 d */
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(beginTime);
                if ((index = expireTime.indexOf(SECOND)) > 0) {
                    calendar.add(Calendar.SECOND, Integer.parseInt(expireTime.substring(0, index)));
                    return calendar.getTime();
                } else if ((index = expireTime.indexOf(MINUTE)) > 0) {
                    calendar.add(Calendar.MINUTE, Integer.parseInt(expireTime.substring(0, index)));
                    return calendar.getTime();
                } else if ((index = expireTime.indexOf(HOUR)) > 0) {
                    calendar.add(Calendar.HOUR, Integer.parseInt(expireTime.substring(0, index)));
                    return calendar.getTime();
                }
            } else {
                /* 包含 d */
                int d = Integer.parseInt(expireTime.substring(0, index));
                LocalDateTime now = beginTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MIN);
                if (index == (expireTime.length() - 1)) {
                    if (d > 0) {
                        now = now.plusDays(d);
                    }
                    return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
                }
                if (d > 0) {
                    now = now.plusDays(d);
                }
                final String subEx = expireTime.substring(index + 1);
                int index2 = 0;
                if ((index2 = subEx.indexOf(SECOND)) > 0) {
                    now = now.plusSeconds(Integer.parseInt(subEx.substring(0, index2)));
                } else if ((index2 = subEx.indexOf(MINUTE)) > 0) {
                    now = now.plusMinutes(Integer.parseInt(subEx.substring(0, index2)));
                } else if ((index2 = subEx.indexOf(HOUR)) > 0) {
                    now = now.plusHours(Integer.parseInt(subEx.substring(0, index2)));
                }
                return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
            }
        } catch (Exception e) {
            // ignore
        }
        throw new ShaunException("cannot resolving this expireTime: [" + expireTime + "] , please check it");
    }

    /**
     * 获取超时时间有效秒数
     *
     * @param expireTime 表达式
     * @return 到期时间
     */
    public static int getTargetSecond(final String expireTime) {
        try {
            int index = 0;
            if ((index = expireTime.indexOf(DAY)) < 1) {
                /* 不包含 d */
                if ((index = expireTime.indexOf(SECOND)) > 0) {
                    return Integer.parseInt(expireTime.substring(0, index));
                } else if ((index = expireTime.indexOf(MINUTE)) > 0) {
                    return Integer.parseInt(expireTime.substring(0, index)) * 60;
                } else if ((index = expireTime.indexOf(HOUR)) > 0) {
                    return Integer.parseInt(expireTime.substring(0, index)) * 60 * 60;
                }
            } else {
                /* 包含 d */
                final LocalDateTime dateTime = LocalDateTime.now();
                int d = Integer.parseInt(expireTime.substring(0, index));
                LocalDateTime now = LocalDate.now().atTime(LocalTime.MIN);
                if (d > 0) {
                    now = now.plusDays(d);
                }
                if (index == (expireTime.length() - 1)) {
                    return (int) dateTime.until(now, ChronoUnit.SECONDS);
                }
                final String subEx = expireTime.substring(index + 1);
                int index2 = 0;
                if ((index2 = subEx.indexOf(SECOND)) > 0) {
                    now = now.plusSeconds(Integer.parseInt(subEx.substring(0, index2)));
                } else if ((index2 = subEx.indexOf(MINUTE)) > 0) {
                    now = now.plusMinutes(Integer.parseInt(subEx.substring(0, index2)));
                } else if ((index2 = subEx.indexOf(HOUR)) > 0) {
                    now = now.plusHours(Integer.parseInt(subEx.substring(0, index2)));
                }
                return (int) dateTime.until(now, ChronoUnit.SECONDS);
            }
        } catch (Exception e) {
            // ignore
        }
        throw new ShaunException("cannot resolving this expireTime: [" + expireTime + "] , please check it");
    }
}
