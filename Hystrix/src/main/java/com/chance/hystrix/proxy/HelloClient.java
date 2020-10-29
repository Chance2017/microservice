package com.chance.hystrix.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("EUREKACLIENT")
public interface HelloClient {
    @GetMapping("/hello")
    String hello();
}
