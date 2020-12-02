package com.baomidou.shaun.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author miemie
 * @since 2020-12-02
 */
public abstract class Base64Util {

    public static String encode(byte[] bytes) {
        return new String(Base64.getEncoder().encode(bytes), StandardCharsets.UTF_8);
    }

    public static byte[] decode(String str) {
        return Base64.getDecoder().decode(str);
    }
}
