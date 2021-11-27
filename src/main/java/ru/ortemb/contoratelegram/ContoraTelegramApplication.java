package ru.ortemb.contoratelegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ContoraTelegramApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContoraTelegramApplication.class, args);
    }

}
