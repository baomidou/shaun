package com.baomidou.shaun.core.authority;

import com.baomidou.shaun.core.annotation.Logical;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2021-02-03
 */
class ShiroAuthorityManagerTest {

    private static ShiroAuthorityManager authorityManager;

    @BeforeAll
    static void before() {
        authorityManager = new ShiroAuthorityManager("jjj");
    }

    @Test
    void checkPermissions() {
        Set<String> elements = Sets.newHashSet("printer:print,query");
        Set<String> permissions = Sets.newHashSet("printer:*");
        assertThat(authorityManager.checkPermissions(Logical.ANY, elements, permissions)).isTrue();
    }
}