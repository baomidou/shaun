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

/**
 * @author miemie
 * @since 2020-12-02
 */
public interface Compressor {

    /**
     * 是否需要压缩
     *
     * @param str 字符串
     * @return 是否
     */
    boolean needCompress(String str);

    /**
     * 是否需要解压
     *
     * @param str 字符串
     * @return 是否
     */
    boolean needDecompress(String str);

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
