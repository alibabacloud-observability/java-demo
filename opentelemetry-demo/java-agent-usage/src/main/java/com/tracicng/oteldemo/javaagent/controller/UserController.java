package com.tracicng.oteldemo.javaagent.controller;

import com.tracicng.oteldemo.javaagent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 参考文档：
 * 1. https://opentelemetry.io/docs/java/manual_instrumentation/
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private ExecutorService es = Executors.newFixedThreadPool(5);

    // 第三种：获得 Tracer 纯手工埋点
    private void biz() throws InterruptedException {
        es.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000L); // some async jobs
                } catch (Throwable e) {
                }
            }
        });

        Thread.sleep(1000); // fake biz logic
        System.out.println("biz done");
    }

    // 第二种：通过注解创建埋点
    private void child(String userType) throws InterruptedException {
        System.out.println(userType);
        biz();
    }

    // 第一种：自动埋点，基于 API 手工添加信息
    @RequestMapping("/async")
    public String async() throws InterruptedException {
        System.out.println("UserController.async -- " + Thread.currentThread().getId());
        userService.async();
        child("vip");
        return "async";
    }

}

