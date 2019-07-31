package com.baomidou.shaun.autoconfigure.properties;

import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.properties.CommonProperties;
import com.baomidou.shaun.core.properties.Cookie;
import com.baomidou.shaun.core.properties.Header;
import com.baomidou.shaun.core.properties.Parameter;
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
public class ShaunProperties extends CommonProperties {
    /**
     * jwt 加密盐值(默认加密方式只支持 32 位字符)
     */
    private String salt = UUID.randomUUID().toString().replace("-", "");
    /**
     * token 的存放位置
     * <p>
     * 非前后分离下,使用 cookie 方式,且只支持 cookie 方式
     * </p>
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
