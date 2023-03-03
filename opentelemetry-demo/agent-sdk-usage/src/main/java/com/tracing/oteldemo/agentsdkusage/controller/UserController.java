package com.tracing.oteldemo.agentsdkusage.controller;

import com.tracing.oteldemo.agentsdkusage.service.UserService;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.extension.annotations.SpanAttribute;
import io.opentelemetry.extension.annotations.WithSpan;
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
    private void biz() {
        Tracer tracer = GlobalOpenTelemetry.get().getTracer("tracer");
        Span span = tracer.spanBuilder("biz (manual)")
                .setParent(Context.current().with(Span.current())) // 可选，自动设置
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("biz-id", "111");

            es.submit(new Runnable() {
                @Override
                public void run() {
                    Span asyncSpan = tracer.spanBuilder("async")
                            .setParent(Context.current().with(span))
                            .startSpan();
                    try {
                        Thread.sleep(1000L); // some async jobs
                    } catch (Throwable e) {
                    }
                    asyncSpan.end();
                }
            });

            Thread.sleep(1000); // fake biz logic
            System.out.println("biz done");
            OpenTelemetry openTelemetry = GlobalOpenTelemetry.get();
            openTelemetry.getPropagators();
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR, "handle biz error");
        } finally {
            span.end();
        }
    }

    // 第二种：通过注解创建埋点
    @WithSpan
    private void child(@SpanAttribute("user.type") String userType) {
        System.out.println(userType);
        biz();
    }

    // 第一种：自动埋点，基于 API 手工添加信息
    @RequestMapping("/async")
    public String async() {
        System.out.println("UserController.async -- " + Thread.currentThread().getId());
        Span span = Span.current();
        span.setAttribute("user.id", "123456");
        userService.async();
        child("vip");
        return "async";
    }

}

