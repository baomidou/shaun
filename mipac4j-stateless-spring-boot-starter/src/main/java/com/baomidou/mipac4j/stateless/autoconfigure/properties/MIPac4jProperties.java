package com.baomidou.mipac4j.stateless.autoconfigure.properties;

import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.baomidou.mipac4j.core.enums.TokenLocation;
import com.baomidou.mipac4j.core.properties.CommonProperties;
import com.baomidou.mipac4j.core.properties.Cookie;
import com.baomidou.mipac4j.core.properties.Header;
import com.baomidou.mipac4j.core.properties.Parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("mipac4j")
public class MIPac4jProperties extends CommonProperties {

    /**
     * jwt 加密盐值(默认加密方式只支持 32 位字符)
     */
    private String salt = UUID.randomUUID().toString().replace("-", "");
    /**
     * token 的存放位置
     */
    private TokenLocation tokenLocation = TokenLocation.HEADER;
    /**
     * 取 token 的方式之 header
     */
    private Header header = new Header();
    /**
     * 取 token 的方式之 cookie
     */
    private Cookie cookie = new Cookie();
    /**
     * 取 token 的方式之 parameter
     */
    private Parameter parameter = new Parameter();
}
