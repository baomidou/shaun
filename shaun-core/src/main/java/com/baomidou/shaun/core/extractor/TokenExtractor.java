package com.baomidou.shaun.core.extractor;

import java.util.Optional;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.http.credentials.extractor.CookieExtractor;

import com.baomidou.shaun.core.context.Cookie;
import com.baomidou.shaun.core.context.Header;
import com.baomidou.shaun.core.context.Parameter;
import com.baomidou.shaun.core.enums.TokenLocation;

import lombok.Data;

/**
 * 定义了从 WebContext 取 token 的方式
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
public class TokenExtractor implements CredentialsExtractor<TokenCredentials> {

    private final TokenLocation tokenLocation;
    private final HeaderExtractor headerExtractor;
    private final CookieExtractor cookieExtractor;
    private final ParameterExtractor parameterExtractor;

    public TokenExtractor(TokenLocation tokenLocation, Header header, Parameter parameter, Cookie cookie) {
        this.tokenLocation = tokenLocation;
        this.headerExtractor = new HeaderExtractor(header.getName(), header.getPrefix());
        this.headerExtractor.setTrimValue(header.isTrimValue());
        this.cookieExtractor = new CookieExtractor(cookie.getName());
        this.parameterExtractor = new ParameterExtractor(parameter.getName(),
                parameter.isSupportGetRequest(), parameter.isSupportPostRequest());
    }

    @Override
    public Optional<TokenCredentials> extract(WebContext context) {
        Optional<TokenCredentials> credentials = Optional.empty();
        switch (tokenLocation) {
            case HEADER:
                credentials = headerExtractor.extract(context);
                break;
            case COOKIE:
                credentials = cookieExtractor.extract(context);
                break;
            case PARAMETER:
                credentials = parameterExtractor.extract(context);
                break;
            case HEADER_OR_COOKIE:
                credentials = headerExtractor.extract(context);
                if (!credentials.isPresent()) {
                    credentials = cookieExtractor.extract(context);
                }
                break;
            case HEADER_OR_PARAMETER:
                credentials = headerExtractor.extract(context);
                if (!credentials.isPresent()) {
                    credentials = parameterExtractor.extract(context);
                }
                break;
            case HEADER_OR_COOKIE_OR_PARAMETER:
                credentials = headerExtractor.extract(context);
                if (!credentials.isPresent()) {
                    credentials = cookieExtractor.extract(context);
                }
                if (!credentials.isPresent()) {
                    credentials = parameterExtractor.extract(context);
                }
                break;
        }
        return credentials;
    }
}
