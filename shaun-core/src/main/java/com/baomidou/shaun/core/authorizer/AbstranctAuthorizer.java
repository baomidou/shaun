package com.baomidou.shaun.core.authorizer;

import lombok.Data;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author miemie
 * @since 2019-08-02
 */
@Data
public abstract class AbstranctAuthorizer<U extends UserProfile> implements Authorizer<U> {

    protected final AuthorizationProfile<U> authorizationProfile;
    /**
     * 用户需要有的
     */
    private Set<String> elements;

    protected AbstranctAuthorizer(AuthorizationProfile<U> authorizationProfile, String... elements) {
        this.authorizationProfile = authorizationProfile;
        this.setElements(elements);
    }

    /**
     * 满足任意值
     *
     * @param checkValue 获取到用户拥有的
     * @return 是否满足
     */
    protected boolean requireAny(Set<String> checkValue) {
        if (CommonHelper.isEmpty(elements)) {
            return true;
        }
        if (CommonHelper.isEmpty(checkValue)) {
            return false;
        }
        for (String element : elements) {
            if (checkValue.contains(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 满足所有值
     *
     * @param checkValue 获取到用户拥有的
     * @return 是否满足
     */
    protected boolean requireAll(Set<String> checkValue) {
        if (CommonHelper.isEmpty(elements)) {
            return true;
        }
        if (CommonHelper.isEmpty(checkValue)) {
            return false;
        }
        for (String element : elements) {
            if (!checkValue.contains(element)) {
                return false;
            }
        }
        return true;
    }

    public Set<String> getElements() {
        return elements;
    }

    public void setElements(final Set<String> elements) {
        this.elements = elements;
    }

    public void setElements(final List<String> elements) {
        if (elements != null) {
            this.elements = new HashSet<>(elements);
        }
    }

    public void setElements(final String... elements) {
        if (elements != null) {
            setElements(Arrays.asList(elements));
        }
    }
}
