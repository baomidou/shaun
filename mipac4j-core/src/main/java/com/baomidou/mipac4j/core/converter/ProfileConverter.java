package com.baomidou.mipac4j.core.converter;

import org.pac4j.core.profile.CommonProfile;

/**
 * Profile 转换器
 *
 * @author miemie
 * @since 2019-07-24
 */
@FunctionalInterface
public interface ProfileConverter<T extends CommonProfile, R extends CommonProfile> {

    ProfileConverter<CommonProfile, CommonProfile> NO_THING = t -> t;

    /**
     * 进行转换
     *
     * @param t 从
     * @return 到
     */
    R converter(T t);
}
