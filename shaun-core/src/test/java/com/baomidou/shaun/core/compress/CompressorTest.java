package com.baomidou.shaun.core.compress;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-12-02
 */
@Slf4j
class CompressorTest {

    private static final int strLen = 100;

    @Test
    void zlib() {
        Compressor compressor = new ZlibCompressor();
        doIt(compressor);
    }

    void doIt(Compressor compressor) {
        String str = str();
        int ol = str.length();
        log.info("原始字符串: {}", str);
        log.info("原始长度: {}", ol);
        long begin = System.currentTimeMillis();
        String nStr = compressor.compress(str);
        long o1 = System.currentTimeMillis();
        int nl = nStr.length();
        log.info("压缩后长度: {}, 压缩率: {}, 耗时: {} 毫秒", nl, (double) nl / (double) ol, o1 - begin);
        log.info("压缩后字符串: {}", nStr);
        String os = compressor.decompress(nStr);
        long o2 = System.currentTimeMillis();
        log.info("解压缩耗时: {} 毫秒", o2 - o1);
        log.info("压缩后字符串: {}", os);
        assertThat(os).isEqualTo(str);
    }

    String str() {
        StringBuilder s = new StringBuilder(uuid());
        for (int i = 0; i < strLen; i++) {
            s.append(uuid());
        }
        System.out.println(s.length());
        return s.toString();
    }

    String uuid() {
        return UUID.randomUUID().toString();
    }
}