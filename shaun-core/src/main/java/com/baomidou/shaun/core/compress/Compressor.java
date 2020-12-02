package com.baomidou.shaun.core.compress;

/**
 * @author miemie
 * @since 2020-12-02
 */
public interface Compressor {

    /**
     * 压缩
     *
     * @param str 需要压缩的字符串
     * @return 压缩后的
     */
    String compress(String str);

    /**
     * 解压缩
     *
     * @param str 需要解压的字符串
     * @return 解压缩后的
     */
    String decompress(String str);
}
