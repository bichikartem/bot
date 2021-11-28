package ru.ortemb.contoratelegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableEurekaClient
@EnableScheduling
@SpringBootApplication
public class ContoraTelegramApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContoraTelegramApplication.class, args);
    }

}
