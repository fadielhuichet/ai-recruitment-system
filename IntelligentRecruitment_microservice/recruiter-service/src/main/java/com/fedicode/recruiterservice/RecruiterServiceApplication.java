package com.fedicode.recruiterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RecruiterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruiterServiceApplication.class, args);
    }

}
