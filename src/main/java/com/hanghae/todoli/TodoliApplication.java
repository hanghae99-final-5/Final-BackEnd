package com.hanghae.todoli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class TodoliApplication {
    //테스트101010111111111111111
    public static void main(String[] args) {
        SpringApplication.run(TodoliApplication.class, args);
    }

}
