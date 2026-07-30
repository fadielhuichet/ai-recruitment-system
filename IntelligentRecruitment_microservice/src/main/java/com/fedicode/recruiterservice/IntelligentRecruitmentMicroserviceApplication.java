package com.fedicode.recruiterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IntelligentRecruitmentMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelligentRecruitmentMicroserviceApplication.class, args);
    }

}
