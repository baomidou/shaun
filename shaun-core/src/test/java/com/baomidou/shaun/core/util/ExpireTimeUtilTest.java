package com.baomidou.shaun.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.Test;

/**
 * @author miemie
 * @since 2019-08-05
 */
class ExpireTimeUtilTest {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final Date begin = formatter.parse("2020-01-01 12:12:12");
    private final int allDay = 24 * 60 * 60;

    ExpireTimeUtilTest() throws ParseException {
    }

    @Test
    void s() {
        String expireTime = "10s";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-01 12:12:22");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(10);
    }

    @Test
    void m() {
        String expireTime = "10m";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-01 12:22:12");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(10 * 60);
    }

    @Test
    void h() {
        String expireTime = "10h";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-01 22:12:12");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(10 * 60 * 60);
    }

    @Test
    void d() {
        String expireTime = "1d";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-02 00:00:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS));

        expireTime = "2d";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-03 00:00:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + allDay);

        // 多余的未识别的后缀则不计算
        expireTime = "1d111";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-02 00:00:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS));
    }

    @Test
    void ds() {
        String expireTime = "1d10s";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-02 00:00:10");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + 10);

        expireTime = "2d10s";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-03 00:00:10");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + allDay + 10);
    }

    @Test
    void dm() {
        String expireTime = "1d10m";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-02 00:10:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + 10 * 60);

        expireTime = "2d10m";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-03 00:10:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + allDay + 10 * 60);
    }

    @Test
    void dh() {
        String expireTime = "1d3h";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-02 03:00:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + 3 * 60 * 60);

        expireTime = "2d3h";
        assertThat(formatter.format(ExpireTimeUtil.getTargetDate(begin, expireTime))).isEqualTo("2020-01-03 03:00:00");
        assertThat(ExpireTimeUtil.getTargetSecond(expireTime)).isEqualTo(LocalTime.now().until(LocalTime.MAX, ChronoUnit.SECONDS) + allDay + 3 * 60 * 60);
    }
}