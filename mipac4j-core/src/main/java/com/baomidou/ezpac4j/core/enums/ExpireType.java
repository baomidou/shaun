package com.baomidou.ezpac4j.core.enums;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author miemie
 * @since 2019-07-22
 */
@Getter
@AllArgsConstructor
public enum ExpireType {

    SECOND(Pattern.compile("^[1-9]\\d?s$")),
    MINUTE(Pattern.compile("^[1-9]\\d?m$")),
    HOUR(Pattern.compile("^[1-9]\\d?h$")),
    DAY(Pattern.compile("^[1-9]\\d?d$"));

    private final Pattern pattern;

    public static ExpireType chooseType(String value) {
        if (StringUtils.hasText(value)) {
            for (ExpireType type : ExpireType.values()) {
                if (type.getPattern().matcher(value).matches()) {
                    return type;
                }
            }
            throw new RuntimeException("can not resolver expireTime from [" + value + "]");
        }
        return null;
    }

    public Integer getSecond(String value) {
        switch (this) {
            case SECOND:
                return Integer.valueOf(value.substring(0, value.length() - 1));
            case MINUTE:
                return Integer.valueOf(value.substring(0, value.length() - 1)) * 60;
            case HOUR:
                return Integer.valueOf(value.substring(0, value.length() - 1)) * 60 * 60;
            case DAY:
                return Integer.valueOf(value.substring(0, value.length() - 1)) * 60 * 60 * 60;
            default:
                return 0;
        }
    }
}
