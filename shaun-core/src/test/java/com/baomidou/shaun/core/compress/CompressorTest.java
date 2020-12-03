package com.baomidou.shaun.core.compress;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-12-02
 */
@Slf4j
class CompressorTest {

    private static final int strLen = 5_000;
    private static final int forSize = 10_000;
    private static final DecimalFormat format = new DecimalFormat("0.00");

    @Test
    void deflate() {
        Compressor compressor = new DeflateCompressor();
        doIt(compressor);
    }

    @Test
    void bzip2() {
        Compressor compressor = new Bzip2Compressor();
        doIt(compressor);
    }

    void doIt(Compressor compressor) {
        Result result = new Result();
        String str = str();
        result.setSize(str.length());
        String nStr = null;
        long begin = System.currentTimeMillis();
        for (int i = 0; i < forSize; i++) {
            nStr = compressor.compress(str);
        }
        long end = System.currentTimeMillis();
        result.setNewSize(nStr.length());
        result.setCompress(end - begin);
        log.info("目标字符串长度: {}, 压缩后长度: {}, 压缩后字符串: {}", str.length(), nStr.length(), nStr);
        assertThat(nStr.length()).isLessThan(str.length());

        String os = null;
        begin = System.currentTimeMillis();
        for (int i = 0; i < forSize; i++) {
            os = compressor.decompress(nStr);
        }
        end = System.currentTimeMillis();
        result.setDecompress(end - begin);
        assertThat(os).isEqualTo(str);

        double time = (double) result.getCompress() / forSize;
        double time2 = (double) result.getDecompress() / forSize;
        double lv = (double) result.getNewSize() / (double) result.getSize();
        log.info("目标字符串长度: {}, 压缩解压缩: {}次, 平均压缩时长: {}毫秒, 解压时长: {}毫秒, 压缩率: {}",
                strLen, forSize, format.format(time), format.format(time2), format.format(lv));

        String xx = null;
        for (int i = 1024 * 2; i < 10000; i++) {
            str = str(i);
            boolean needCompress = compressor.needCompress(str);
            if (needCompress) {
                xx = compressor.compress(str);
                boolean needDecompress = compressor.needDecompress(xx);
                assertThat(needDecompress).as("要解压长度:" + xx.length() + ",原始长度:" + i).isTrue();
                assertThat(compressor.decompress(xx)).as("比较要解压长度:" + xx.length() + ",原始长度:" + i).isEqualTo(str);
            } else {
                if (compressor.needDecompress(str)) {
                    String decompress = compressor.decompress(str);
                    assertThat(decompress).as("尝试解压").isEqualTo(str);
                }
            }
        }
    }

    String str() {
        return str(strLen);
    }

    String str(int len) {
        StringBuilder s = new StringBuilder(uuid());
        while (s.length() < len) {
            s.append(uuid());
        }
        if (s.length() > len) {
            s.setLength(len);
        }
        return s.toString();
    }

    @Data
    private static class Result {

        private long compress;
        private long decompress;
        private long size;
        private long newSize;
    }

    String uuid() {
        return UUID.randomUUID().toString();
    }
}