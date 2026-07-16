package com.yipei;

import com.yipei.config.AppProperties;
import com.yipei.config.AiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, AiProperties.class})
public class YipeiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YipeiApplication.class, args);
    }

}
