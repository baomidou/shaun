package com.baomidou.shaun.core.compress;

import com.baomidou.shaun.core.util.Base64Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * zlib
 * <p>
 * DEFLATE是同时使用了LZ77算法与哈夫曼编码（Huffman Coding）的一个无损数据压缩算法，
 * DEFLATE压缩与解压的源代码可以在自由、通用的压缩库zlib上找到，zlib官网：http://www.zlib.net/
 * </p>
 * <p>
 * 默认配置压缩率大概 77%, 既 100 压缩后 77, 耗时 2 毫秒左右
 * </p>
 *
 * @author miemie
 * @since 2020-12-02
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ZlibCompressor implements Compressor {

    private int level = Deflater.BEST_COMPRESSION;
    private boolean nowrap = false;
    private int forSize = 1024;

    public ZlibCompressor(int level) {
        this.level = level;
    }

    @Override
    public String compress(String str) {
        Deflater compress = new Deflater(level, nowrap);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(forSize)) {
            compress.setInput(str.getBytes(StandardCharsets.UTF_8));
            compress.finish();
            final byte[] buf = new byte[forSize];
            while (!compress.finished()) {
                int len = compress.deflate(buf);
                bos.write(buf, 0, len);
            }
            return Base64Util.encode(bos.toByteArray());
        } catch (Exception e) {
            log.error("compress error", e);
            return str;
        } finally {
            compress.end();
        }
    }

    @Override
    public String decompress(String str) {
        Inflater decompress = new Inflater(nowrap);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(forSize)) {
            decompress.setInput(Base64Util.decode(str));
            final byte[] buf = new byte[forSize];
            while (!decompress.finished()) {
                int len = decompress.inflate(buf);
                bos.write(buf, 0, len);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decompress error", e);
            return str;
        } finally {
            decompress.end();
        }
    }
}
