package com.baomidou.shaun.core.context;

import java.lang.reflect.Constructor;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.baomidou.shaun.core.profile.TokenProfile;

/**
 * 参考至 {org.springframework.security.core.context.SecurityContextHolder}
 *
 * @author miemie
 * @since 2020-05-25
 */
public class ProfileHolder {

    public static final String MODE_REQUEST = "MODE_REQUEST";
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
    public static final String SYSTEM_PROPERTY = "shaun.profile.strategy";
    private static ProfileHolderStrategy strategy;
    private static int initializeCount = 0;
    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);

    static {
        initialize();
    }

    public static void clearProfile() {
        strategy.clearProfile();
    }

    public static TokenProfile getProfile() {
        return strategy.getProfile();
    }

    public static void setProfile(TokenProfile profile) {
        strategy.setProfile(profile);
    }

    private static void initialize() {
        if (!StringUtils.hasText(strategyName)) {
            // Set default
            strategyName = MODE_REQUEST;
        }
        if (strategyName.equals(MODE_REQUEST)) {
            strategy = new RequestProfileHolderStrategy();
        } else if (strategyName.equals(MODE_THREADLOCAL)) {
            strategy = new ThreadLocalProfileHolderStrategy();
        } else if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
            strategy = new InheritableThreadLocalProfileHolderStrategy();
        } else {
            // Try to load a custom strategy
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (ProfileHolderStrategy) customStrategy.newInstance();
            } catch (Exception ex) {
                ReflectionUtils.handleReflectionException(ex);
            }
        }

        initializeCount++;
    }

    public static void setStrategyName(String strategyName) {
        ProfileHolder.strategyName = strategyName;
        initialize();
    }

    public static ProfileHolderStrategy getProfileHolderStrategy() {
        return strategy;
    }

    public static int getInitializeCount() {
        return initializeCount;
    }
}
