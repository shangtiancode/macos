package com.alishangtian.mubbo;

import com.alishangtian.mubbo.comsumer.annotation.EnableMubboProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description Application
 * @Date once upon a time
 * @Author maoxiaobing
 **/
@Log4j2
@EnableMubboProvider
@SpringBootApplication(scanBasePackages = "com.alishangtian.mubbo")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("server started");
    }
}
