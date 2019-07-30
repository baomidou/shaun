package com.baomidou.mipac4j.stateless.autoconfigure.properties;

import com.baomidou.mipac4j.core.enums.TokenLocation;
import com.baomidou.mipac4j.core.properties.CommonProperties;
import com.baomidou.mipac4j.core.properties.Cookie;
import com.baomidou.mipac4j.core.properties.Header;
import com.baomidou.mipac4j.core.properties.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties("mipac4j")
public class MIPac4jProperties extends CommonProperties {
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
