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
