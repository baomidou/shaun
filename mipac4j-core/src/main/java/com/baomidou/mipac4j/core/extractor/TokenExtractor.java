package com.baomidou.mipac4j.core.extractor;

import com.baomidou.mipac4j.core.enums.TokenLocation;
import com.baomidou.mipac4j.core.properties.Cookie;
import com.baomidou.mipac4j.core.properties.Header;
import com.baomidou.mipac4j.core.properties.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.http.credentials.extractor.CookieExtractor;

/**
 * 定义了从 WebContext 取 token 的方式
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
@AllArgsConstructor
public class TokenExtractor implements CredentialsExtractor<TokenCredentials> {

    private final TokenLocation tokenLocation;
    private final HeaderExtractor headerExtractor;
    private final ParameterExtractor parameterExtractor;
    private final CookieExtractor cookieExtractor;

    public TokenExtractor(TokenLocation tokenLocation, Header header, Parameter parameter, Cookie cookie) {
        this.tokenLocation = tokenLocation;
        this.headerExtractor = new HeaderExtractor(header.getHeaderName(), header.getPrefixHeader());
        this.headerExtractor.setTrimValue(header.isTrimValue());
        this.parameterExtractor = new ParameterExtractor(parameter.getParameterName(),
                parameter.isSupportGetRequest(), parameter.isSupportPostRequest());
        this.cookieExtractor = new CookieExtractor(cookie.getName());
    }

    @Override
    public TokenCredentials extract(WebContext context) {
        switch (tokenLocation) {
            case HEADER:
                return headerExtractor.extract(context);
            case PARAMETER:
                return parameterExtractor.extract(context);
            case COOKIE:
                return cookieExtractor.extract(context);
            case HEARDER_OR_PARAMETER:
                TokenCredentials credentials = headerExtractor.extract(context);
                if (credentials == null) {
                    credentials = parameterExtractor.extract(context);
                }
                return credentials;
            default:
                return null;
        }
    }
}
