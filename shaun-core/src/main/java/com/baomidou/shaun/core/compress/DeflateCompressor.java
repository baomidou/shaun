/*
 * Copyright 2019-2020 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.compress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * deflate 是同时使用了LZ77算法与哈夫曼编码（Huffman Coding）的一个无损数据压缩算法
 * <p>
 * 性能测试: <br>
 * <p>
 * 系统: win10, CPU: AMD 1700 8核16线程 3.2GHz, RAM: 8G*2 2666MHz
 * 目标字符串(每次都不一样)长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 0.16毫秒, 解压时长: 0.07毫秒, 压缩率: 0.77
 * </p>
 * <p>
 * 机器 Mac mini, CPU: i5 6核 3GHz, RAM: 8G*2 2666MHz
 * 目标字符串长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 0.12毫秒, 解压时长: 0.05毫秒, 压缩率: 0.77
 * </p>
 *
 * @author miemie
 * @since 2020-12-02
 */
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeflateCompressor extends AbstractCompressor {

    private int level = Deflater.BEST_COMPRESSION;
    private boolean nowrap = false;

    public DeflateCompressor(int level) {
        this.level = level;
    }

    public DeflateCompressor(boolean nowrap) {
        this.nowrap = nowrap;
    }

    @Override
    protected double compressionRatio() {
        return 0.75;
    }

    @Override
    public String compress(String str) {
        Deflater deflater = new Deflater(level, nowrap);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(forSize)) {
            deflater.setInput(string2Byte(str));
            deflater.finish();
            final byte[] buf = new byte[forSize];
            while (!deflater.finished()) {
                int len = deflater.deflate(buf);
                out.write(buf, 0, len);
            }
            return encodeBase64(out.toByteArray());
        } catch (Exception e) {
            if (infoLog) {
                log.error("compress error", e);
            }
            return str;
        } finally {
            deflater.end();
        }
    }

    @Override
    public String decompress(String str) {
        Inflater inflater = new Inflater(nowrap);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(forSize)) {
            inflater.setInput(decodeBase64(str));
            final byte[] buf = new byte[forSize];
            while (!inflater.finished()) {
                int len = inflater.inflate(buf);
                out.write(buf, 0, len);
            }
            return byte2String(out);
        } catch (Exception e) {
            if (infoLog) {
                log.error("decompress error", e);
            }
            return str;
        } finally {
            inflater.end();
        }
    }
}
