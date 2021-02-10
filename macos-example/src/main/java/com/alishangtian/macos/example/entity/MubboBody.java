package com.alishangtian.macos.example.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author shangtian
 * @description
 * @date 2021/2/10:16
 */
@Builder
@Data
public class MubboBody implements Serializable {
    private String name;
    private int age;
    private String address;
    private Integer id;
}
