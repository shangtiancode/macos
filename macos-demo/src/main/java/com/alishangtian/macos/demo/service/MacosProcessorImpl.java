package com.alishangtian.macos.demo.service;

import com.alishangtian.macos.macos.processor.MacosProcessor;
import com.alishangtian.macos.model.core.XtimerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @ClassName XtimerProcessorImpl
 * @Author alishangtian
 * @Date 2020/7/24 22:49
 */
@Slf4j
@Service("macosProcessor")
public class MacosProcessorImpl implements MacosProcessor {

    @Override
    public boolean process(XtimerRequest xtimerRequest) {
        return true;
    }

}
