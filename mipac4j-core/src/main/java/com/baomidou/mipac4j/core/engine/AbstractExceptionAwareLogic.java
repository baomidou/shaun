package com.baomidou.mipac4j.core.engine;

import com.baomidou.mipac4j.core.profile.ProfileManagerFactoryAware;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.exception.HttpAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author miemie
 * @since 2019-07-25
 */
public class AbstractExceptionAwareLogic extends ProfileManagerFactoryAware {

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Handle exceptions.
     *
     * @param e       the thrown exception
     * @param context the web context
     * @return the final HTTP result
     */
    protected Boolean handleException(final Exception e, final J2EContext context) {
        if (context == null) {
            throw runtimeException(e);
        } else if (e instanceof HttpAction) {
            final HttpAction action = (HttpAction) e;
            log.debug("extra HTTP action required in security: {}", action.getCode());
            return false;
        } else {
            throw runtimeException(e);
        }
    }

    /**
     * Wrap an Exception into a RuntimeException.
     *
     * @param exception the original exception
     * @return the RuntimeException
     */
    protected RuntimeException runtimeException(final Exception exception) {
        if (exception instanceof RuntimeException) {
            throw (RuntimeException) exception;
        } else {
            throw new RuntimeException(exception);
        }
    }
}
