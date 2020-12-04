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
package com.baomidou.shaun.core.jwt;

/**
 * jwt 模式
 *
 * @author miemie
 * @since 2020-12-03
 */
public enum JwtType {
    /**
     * 签名 和 加密
     * 默认,如果只存入一个长度 32 的 id,生成的 jwt 长度大概为 456
     */
    DEFAULT,
    /**
     * 只加密
     * 可以,不会暴露 payload,如果只存入一个长度 32 的 id,生成的 jwt 长度大概为 285
     */
    ONLY_ENCRYPTION,
    /**
     * 只签名
     * 不建议,会暴露 payload,如果只存入一个长度 32 的 id,生成的 jwt 长度大概为 269
     */
    ONLY_SIGNATURE,
    /**
     * 不签名也不加密
     * 不建议,会暴露 payload,如果只存入一个长度 32 的 id,生成的 jwt 长度大概为 225
     */
    NONE;
}
