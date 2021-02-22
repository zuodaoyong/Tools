package com.tools.controller;


import com.tools.annotation.NettyRpcService;

@NettyRpcService(value = HelloService.class, version = "1.0")
public class HelloServiceImpl implements HelloService {

    public HelloServiceImpl() {

    }

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }

    @Override
    public String hello(String name, Integer age) {
        return name + " is " + age;
    }
}
