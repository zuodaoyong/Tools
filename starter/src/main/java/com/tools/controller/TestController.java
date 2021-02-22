package com.tools.controller;

import com.tools.rpc.RpcClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/test")
@RestController
public class TestController {

    @Resource
    private RpcClient rpcClient;

    @RequestMapping("/hello")
    public String helloTest1() {
        HelloService helloService = rpcClient.createService(HelloService.class, "1.0");
        String result = helloService.hello("World");
        return result;
    }


}
