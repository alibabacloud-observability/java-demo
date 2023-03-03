package com.tracing.oteldemo.otelsdkusage.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Async
    public void async() {
        System.out.println("UserService.async -- " + Thread.currentThread().getId());
        System.out.println("my name is async");
        System.out.println("UserService.async -- ");
    }
}