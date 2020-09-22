package com.alishangtian.macos.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description TODO
 * @ClassName PackResult
 * @Author alishangtian
 * @Date 2020/7/24 22:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackResult {
    private int code;
    private String msg;
    private Object data;
}
