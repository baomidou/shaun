package com.baomidou.shaun.autoconfigure.properties;

import com.baomidou.shaun.core.enums.TokenLocation;
import com.baomidou.shaun.core.properties.Cookie;
import com.baomidou.shaun.core.properties.Header;
import com.baomidou.shaun.core.properties.Parameter;
import lombok.Data;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.DefaultAuthorizationChecker;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.UUID;

/**
 * @author miemie
 * @since 2019-07-01
 */
@Data
@ConfigurationProperties("shaun")
public class ShaunProperties {
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
    /**
     * 登出 url
     */
    private String logoutUrl;
    /**
     * jwt 超时时间
     * <li> 以数字开头,以 "s" 结尾: 秒 , 例: 100s = 100秒 </li>
     * <li> 以数字开头,以 "m" 结尾: 分 , 例: 100m = 100分钟 </li>
     * <li> 以数字开头,以 "h" 结尾: 小时 , 例: 100h = 100小时 </li>
     * <li> 以数字开头,以 "d" 结尾: 天 , 例: 30d = 30天 </li>
     * <li> 以 "o" 开头(不是零),中间是数字,以上面的结尾: 特殊的超时时间.
     * "o" 后面的表示宽容时间(还有什么好的描述吗?)
     * 例2: o30m = 如果生成 jwt 时的时间在 23:29:59 及之前则 jwt 有效时间到今天 23:59:59 为止,否则顺延一天到明天的 23:59:59 为止
     * 例2: o5h = 如果生成 jwt 时的时间在 18:59:59 及之前则 jwt 有效时间到今天 23:59:59 为止,否则顺延一天到明天的 23:59:59 为止
     * </li>
     */
    private String expireTime;
    /**
     * authorizers,多个以逗号分隔(不包含自己注入的 {@link Authorizer})
     * 默认支持的一些参考 {@link DefaultAuthorizationChecker}
     */
    private String authorizers = "";
    /**
     * 排除的 url
     */
    private List<String> excludePath;
    /**
     * 排除的 url 的统一前缀
     */
    private List<String> excludeBranch;
    /**
     * 排除的 url 的 正则表达式
     */
    private List<String> excludeRegex;
}
