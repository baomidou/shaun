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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * bzip2是Julian Seward开发并按照自由软件／开源软件协议发布的数据压缩算法及程序
 * bzip2比传统的gzip的压缩效率更高，但是它的压缩速度较慢 <br>
 * 依赖 org.apache.commons:commons-compress
 * <p>
 * 性能测试: <br>
 * <p>
 * 系统: win10, CPU: AMD 1700 8核16线程 3.2GHz, RAM: 8G*2 2666MHz
 * blockSize为1时: 目标字符串(每次都不一样)长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 0.98毫秒, 解压时长: 0.33毫秒, 压缩率: 0.71
 * blockSize为9时: 目标字符串(每次都不一样)长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 2.15毫秒, 解压时长: 0.42毫秒, 压缩率: 0.71
 * </p>
 * <p>
 * 机器 Mac mini, CPU: i5 6核 3GHz, RAM: 8G*2 2666MHz
 * blockSize为1时: 目标字符串长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 0.86毫秒, 解压时长: 0.30毫秒, 压缩率: 0.71
 * blockSize为9时: 目标字符串长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 1.58毫秒, 解压时长: 0.34毫秒, 压缩率: 0.71
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
public class Bzip2Compressor extends AbstractCompressor {

    private int blockSize = BZip2CompressorOutputStream.MIN_BLOCKSIZE;

    @Override
    protected double compressionRatio() {
        return 0.69;
    }

    @Override
    public String compress(String str) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(forSize);
             BZip2CompressorOutputStream bzip2 = new BZip2CompressorOutputStream(out, blockSize)) {
            bzip2.write(string2Byte(str));
            bzip2.close();
            return encodeBase64(out.toByteArray());
        } catch (Exception e) {
            if (infoLog) {
                log.error("compress error", e);
            }
            return str;
        }
    }

    @Override
    public String decompress(String str) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(decodeBase64(str));
             BZip2CompressorInputStream bis = new BZip2CompressorInputStream(in);
             ByteArrayOutputStream out = new ByteArrayOutputStream(forSize)) {
            byte[] buffer = new byte[forSize];
            int n;
            while ((n = bis.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return byte2String(out);
        } catch (Exception e) {
            if (infoLog) {
                log.error("decompress error", e);
            }
            return str;
        }
    }
}
