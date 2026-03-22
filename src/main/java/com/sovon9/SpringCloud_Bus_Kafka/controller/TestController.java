package com.sovon9.SpringCloud_Bus_Kafka.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class TestController {

    @Value("${balance:0}")
    private double balance;

    @Value("${username:default}")
    private String username;

    @GetMapping("/balance")
    public String getBalance()
    {
        return "balance for "+username+" is "+balance;
    }

}
