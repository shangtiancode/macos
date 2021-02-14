package com.alishangtian.mubbo.consumer;

import com.alishangtian.mubbo.comsumer.annotation.EnableMubboConsumer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description Application
 * @Date once upon a time
 * @Author maoxiaobing
 **/
@SpringBootApplication
@EnableMubboConsumer(scanPackages = {"com.alishangtian.mubbo.consumer.service"})
@Log4j2
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("server started");
    }
}
