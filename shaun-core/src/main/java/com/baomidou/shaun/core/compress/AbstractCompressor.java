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

import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author miemie
 * @since 2020-12-02
 */
@Setter
public abstract class AbstractCompressor implements Compressor {
    protected final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected boolean infoLog = false;
    protected int forSize = 1024;
    /**
     * 触发压缩的字符串长度
     */
    protected int strLengthThreshold = 1024 * 3;

    @Override
    public boolean needCompress(String str) {
        return str.length() > strLengthThreshold;
    }

    @Override
    public boolean needDecompress(String str) {
        int length = str.length();
        if (length > strLengthThreshold) {
            return true;
        }
        double v = (double) length / compressionRatio();
        return (long) v > strLengthThreshold;
    }

    /**
     * 压缩比, 需要高估
     */
    protected abstract double compressionRatio();

    protected byte[] decodeBase64(String str) {
        return Base64.getDecoder().decode(string2Byte(str));
    }

    protected String encodeBase64(byte[] bytes) {
        return byte2String(Base64.getEncoder().encode(bytes));
    }

    protected byte[] string2Byte(String str) {
        return str.getBytes(DEFAULT_CHARSET);
    }

    protected String byte2String(byte[] bytes) {
        return new String(bytes, DEFAULT_CHARSET);
    }

    protected String byte2String(ByteArrayOutputStream out) {
        return byte2String(out.toByteArray());
    }
}
