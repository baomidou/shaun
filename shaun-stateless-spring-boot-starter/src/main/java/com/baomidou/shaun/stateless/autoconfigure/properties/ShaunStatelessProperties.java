package com.baomidou.shaun.stateless.autoconfigure.properties;

import com.baomidou.shaun.core.properties.CommonProperties;
import com.baomidou.shaun.stateless.enums.TokenLocation;
import com.baomidou.shaun.stateless.properties.Cookie;
import com.baomidou.shaun.stateless.properties.Header;
import com.baomidou.shaun.stateless.properties.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.UUID;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("shaun")
public class ShaunStatelessProperties extends CommonProperties {

    /**
     * jwt 加密盐值(默认加密方式只支持 32 位字符)
     */
    private String salt = UUID.randomUUID().toString().replace("-", "");
    /**
     * jwt 超时时间(单位秒)
     */
    private Integer expireTime;
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
