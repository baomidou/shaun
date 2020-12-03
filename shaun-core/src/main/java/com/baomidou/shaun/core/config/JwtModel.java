package com.baomidou.shaun.core.config;

/**
 * jwt 模式
 *
 * @author miemie
 * @since 2020-12-03
 */
public enum JwtModel {
    /**
     * 签名 和 加密
     * 默认,如果只存入一个长度 32 的 id,生成的 jwt 长度大概为 456
     */
    SIGNATURE_ENCRYPTION,
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
