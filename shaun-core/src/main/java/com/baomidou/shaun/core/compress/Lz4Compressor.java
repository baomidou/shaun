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

import java.io.ByteArrayOutputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * bzip2是Julian Seward开发并按照自由软件／开源软件协议发布的数据压缩算法及程序
 * bzip2比传统的gzip的压缩效率更高，但是它的压缩速度较慢
 * <p>
 * 默认配置下测试: <br>
 * <p>
 * 系统: win10, CPU: AMD 1700 8核16线程 3.2GHz, RAM: 8G*2 2666MHz
 * blockSize为9时: 目标字符串(每次都不一样)长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 2.15毫秒, 解压时长: 0.42毫秒, 压缩率: 0.71
 * blockSize为1时: 目标字符串(每次都不一样)长度: 5000, 压缩解压缩: 10000次, 平均压缩时长: 0.98毫秒, 解压时长: 0.33毫秒, 压缩率: 0.71
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
public class Lz4Compressor extends CompressorSupport implements Compressor {

    private LZ4Factory factory = LZ4Factory.fastestInstance();

    @Override
    public String compress(String str) {
        LZ4Compressor compressor = factory.fastCompressor();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(forSize);
             LZ4BlockOutputStream los = new LZ4BlockOutputStream(out, 2048, compressor)) {
            los.write(string2Byte(str));
            los.close();
            return encodeBase64(out.toByteArray());
        } catch (Exception e) {
            log.error("compress error", e);
            return str;
        }
    }

    @Override
    public String decompress(String str) {
        LZ4FastDecompressor decompressor = factory.fastDecompressor();
        final byte[] buf = new byte[forSize];
//        decompressor.decompress()
        return str;
    }
}
