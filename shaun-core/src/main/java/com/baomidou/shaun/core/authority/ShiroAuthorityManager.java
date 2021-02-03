/*
 * Copyright 2019-2021 baomidou (wonderming@vip.qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.shaun.core.authority;

import com.baomidou.shaun.core.annotation.Logical;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.util.PermissionUtils;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * shiro 的 permission 验证支持
 * 只需要 shiro-core 包
 *
 * </P>
 * maven 引用:
 * <blockquote><pre>
 *  <dependency>
 *     <groupId>org.apache.shiro</groupId>
 *     <artifactId>shiro-core</artifactId>
 *     <version>1.7.1</version>
 *     <exclusions>
 *         <exclusion>
 *             <artifactId>*</artifactId>
 *             <groupId>org.apache.shiro</groupId>
 *         </exclusion>
 *     </exclusions>
 *  </dependency>
 * </pre></blockquote>
 *
 * </p>
 * gradle 引用:
 * <blockquote><pre>
 *  api("org.apache.shiro:shiro-core:1.7.1") {
 *         exclude group: 'org.apache.shiro'
 *  }
 * </pre></blockquote>
 * <p>
 * 文档: <a herf="https://doc.yonyoucloud.com/doc/apache-shiro-1.2.x-reference/II.%20Core%20%E6%A0%B8%E5%BF%83/6.1.%20Permissions%20%E6%9D%83%E9%99%90.html"></a>
 *
 * @author miemie
 * @since 2019-08-01
 */

public class ShiroAuthorityManager extends DefaultAuthorityManager {

    @Setter
    @Getter
    private PermissionResolver permissionResolver = new WildcardPermissionResolver();

    public ShiroAuthorityManager(String skipAuthenticationRolePermission) {
        super(skipAuthenticationRolePermission);
    }

    @Override
    public boolean checkPermissions(Logical logical, Set<String> elements, Set<String> permissions) {
        if (CollectionUtils.isEmpty(elements) || CollectionUtils.isEmpty(permissions)) {
            return false;
        }
        Set<Permission> elementsPerms = PermissionUtils.resolvePermissions(elements, getPermissionResolver());
        Set<Permission> permissionsPerms = PermissionUtils.resolvePermissions(permissions, getPermissionResolver());
        return checkShiroPermissions(logical, elementsPerms, permissionsPerms);
    }

    protected boolean checkShiroPermissions(Logical logical, Set<Permission> elements, Set<Permission> permissions) {
        if (logical == Logical.BOTH) {
            for (Permission permission : permissions) {
                for (Permission element : elements) {
                    if (!permission.implies(element)) {
                        return false;
                    }
                }
            }
            return true;
        }
        for (Permission permission : permissions) {
            for (Permission element : elements) {
                if (permission.implies(element)) {
                    return true;
                }
            }
        }
        return false;
    }
}
