package com.chance.feign.controller;

import com.chance.feign.proxy.HelloClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeignController {

    HelloClient client;

    public FeignController(HelloClient client) {this.client = client;}

    @GetMapping("/hi")
    public String hello() {
        return client.hello();
    }

}
