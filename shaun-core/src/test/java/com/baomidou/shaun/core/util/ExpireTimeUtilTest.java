package com.baomidou.shaun.core.util;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

/**
 * @author miemie
 * @since 2019-08-05
 */
class ExpireTimeUtilTest {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void getTargetDate() {
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate(60)));
    }
}