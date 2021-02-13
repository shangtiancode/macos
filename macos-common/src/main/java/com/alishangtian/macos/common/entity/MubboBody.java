package com.alishangtian.macos.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shangtian
 * @description
 * @date 2021/2/10:16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MubboBody implements Serializable {
    private String name;
    private int age;
    private String address;
    private Integer id;
}
