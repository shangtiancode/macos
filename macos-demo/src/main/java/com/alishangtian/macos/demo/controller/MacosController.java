package com.alishangtian.macos.demo.controller;

import com.alishangtian.macos.DefaultMacosClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @ClassName XtimerController
 * @Author alishangtian
 * @Date 2020/7/24 22:05
 */
@RestController
@Slf4j
public class MacosController {
    @Autowired(required = false)
    @Qualifier("macosClient")
    private DefaultMacosClient defaultMacosClient;
}
