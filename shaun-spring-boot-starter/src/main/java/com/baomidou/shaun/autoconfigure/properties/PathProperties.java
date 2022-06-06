/*
 * Copyright 2019-2022 baomidou (wonderming@vip.qq.com)
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
package com.baomidou.shaun.autoconfigure.properties;

import lombok.Data;

import java.util.List;

/**
 * @author miemie
 * @since 2022-06-06
 */
@Data
public class PathProperties {

    /**
     * 具体地址
     */
    private List<String> path;
    /**
     * 地址前缀
     */
    private List<String> branch;
    /**
     * 地址正则
     */
    private List<String> regex;
}
