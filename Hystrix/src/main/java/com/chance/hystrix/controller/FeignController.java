package com.chance.hystrix.controller;

import com.chance.hystrix.proxy.HelloClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    HelloClient client;

    public FeignController(HelloClient client) {this.client = client;}

    @GetMapping("/hi")
    @HystrixCommand(fallbackMethod = "fallback")
    public String hello() {
        return client.hello();
    }

    public String fallback() {
        return "这是一个熔断机制";
    }

}
