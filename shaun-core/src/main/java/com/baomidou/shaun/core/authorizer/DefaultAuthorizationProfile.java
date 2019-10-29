package com.baomidou.shaun.core.authorizer;

import java.util.Set;

import org.pac4j.core.util.CommonHelper;

import com.baomidou.shaun.core.enums.Logical;

/**
 * @author miemie
 * @since 2019-08-01
 */
public class DefaultAuthorizationProfile implements AuthorizationProfile {

    @Override
    public boolean checkRoles(Logical logical, Set<String> elements, Set<String> roles) {
        return match(logical, elements, roles);
    }

    @Override
    public boolean checkPermissions(Logical logical, Set<String> elements, Set<String> permissions) {
        return match(logical, elements, permissions);
    }

    /**
     * @param elements    注解里的值
     * @param checkValues 根据用户取出来的值
     * @return
     */
    protected boolean match(Logical logical, Set<String> elements, Set<String> checkValues) {
        if (CommonHelper.isEmpty(elements) || CommonHelper.isEmpty(checkValues)) {
            return false;
        }
        if (logical == Logical.OR) {
            for (String element : elements) {
                if (checkValues.contains(element)) {
                    return true;
                }
            }
            return false;
        }
        for (String element : elements) {
            if (!checkValues.contains(element)) {
                return false;
            }
        }
        return true;
    }
}
