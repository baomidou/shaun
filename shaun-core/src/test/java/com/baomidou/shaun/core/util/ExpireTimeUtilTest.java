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
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("10s")));
        System.out.println(ExpireTimeUtil.getTargetSecond("10s"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("10m")));
        System.out.println(ExpireTimeUtil.getTargetSecond("10m"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("11h")));
        System.out.println(ExpireTimeUtil.getTargetSecond("11h"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("1d")));
        System.out.println(ExpireTimeUtil.getTargetSecond("1d"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("1d111")));
        System.out.println(ExpireTimeUtil.getTargetSecond("1d111"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("2d10s")));
        System.out.println(ExpireTimeUtil.getTargetSecond("2d10s"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("3d10m")));
        System.out.println(ExpireTimeUtil.getTargetSecond("3d10m"));
        System.out.println(formatter.format(ExpireTimeUtil.getTargetDate("4d10h")));
        System.out.println(ExpireTimeUtil.getTargetSecond("4d10h"));
    }
}