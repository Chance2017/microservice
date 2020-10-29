package com.chance.eurekaclient3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class Eurekaclient3Application {

    public static void main(String[] args) {
        SpringApplication.run(Eurekaclient3Application.class, args);
    }

}
