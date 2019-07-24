package com.baomidou.mipac4j.core.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.http.credentials.extractor.CookieExtractor;

import com.baomidou.mipac4j.core.enums.TokenLocation;
import com.baomidou.mipac4j.core.properties.Cookie;
import com.baomidou.mipac4j.core.properties.Header;
import com.baomidou.mipac4j.core.properties.Parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定义了从 WebContext 取 token 的方式
 *
 * @author miemie
 * @since 2019-07-20
 */
@Data
@AllArgsConstructor
public class TokenExtractor implements CredentialsExtractor<TokenCredentials> {

    private CredentialsExtractor<TokenCredentials> extractor;

    public TokenExtractor(TokenLocation type, Header header, Parameter parameter, Cookie cookie) {
        switch (type) {
            case HEADER:
                this.extractor = new HeaderExtractor(header.getHeaderName(), header.getPrefixHeader());
                break;
            case PARAMETER:
                this.extractor = new ParameterExtractor(parameter.getParameterName(),
                        parameter.isSupportGetRequest(), parameter.isSupportPostRequest());
                break;
            case COOKIE:
                this.extractor = new CookieExtractor(cookie.getName());
                break;
            default:
                this.extractor = context -> {
                    String token = context.getRequestHeader(header.getHeaderName());
                    if (token == null) {
                        token = context.getRequestHeader(header.getHeaderName().toLowerCase());
                        if (token != null && !token.startsWith(header.getPrefixHeader())) {
                            throw new CredentialsException("Wrong prefix for token: " + header.getHeaderName());
                        }
                    }
                    if (token == null) {
                        final String method = context.getRequestMethod();
                        if (HttpConstants.HTTP_METHOD.GET.name().equalsIgnoreCase(method) && !parameter.isSupportGetRequest()) {
                            throw new CredentialsException("GET requests not supported");
                        } else if (HttpConstants.HTTP_METHOD.POST.name().equalsIgnoreCase(method) && !parameter.isSupportPostRequest()) {
                            throw new CredentialsException("POST requests not supported");
                        }

                        token = context.getRequestParameter(parameter.getParameterName());
                        if (token == null) {
                            return null;
                        }
                    }
                    return new TokenCredentials(token);
                };
                break;
        }
    }

    @Override
    public TokenCredentials extract(WebContext context) {
        return this.extractor.extract(context);
    }
}
