package com.baomidou.shaun.core.compress;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DecimalFormat;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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

    @Test
    void lz4() {
        Compressor compressor = new Lz4Compressor();
        doIt(compressor);
    }

    void doIt(Compressor compressor) {
        Result result = new Result();
        for (int i = 0; i < forSize; i++) {
            String str = str();
            result.upSize(str.length());
            long begin = System.currentTimeMillis();
            String nStr = compressor.compress(str);
            long end = System.currentTimeMillis();
            result.upCompress(end - begin);
            result.upNewSize(nStr.length());

            begin = System.currentTimeMillis();
            String os = compressor.decompress(nStr);
            end = System.currentTimeMillis();
            result.upDecompress(end - begin);
            assertThat(os).isEqualTo(str);
        }
        double time = (double) result.getCompress() / forSize;
        double time2 = (double) result.getDecompress() / forSize;
        double lv = (double) result.getNewSize() / (double) result.getSize();
        log.info("目标字符串(每次都不一样)长度: {}, 压缩解压缩: {}次, 平均压缩时长: {}毫秒, 解压时长: {}毫秒, 压缩率: {}",
                strLen, forSize, format.format(time), format.format(time2), format.format(lv));
    }

    String str() {
        StringBuilder s = new StringBuilder(uuid());
        while (s.length() < strLen) {
            s.append(uuid());
        }
        if (s.length() > strLen) {
            s.setLength(strLen);
        }
        return s.toString();
    }

    @Data
    private static class Result {

        private long compress;
        private long decompress;
        private long size;
        private long newSize;

        public void upCompress(long compress) {
            this.compress += compress;
        }

        public void upDecompress(long decompress) {
            this.decompress += decompress;
        }

        public void upSize(long size) {
            this.size += size;
        }

        public void upNewSize(long newSize) {
            this.newSize += newSize;
        }
    }

    String uuid() {
        return UUID.randomUUID().toString();
    }
}