package com.tracing.oteldemo.otelsdkusage.controller;

import com.tracing.oteldemo.otelsdkusage.service.UserService;
import com.tracing.oteldemo.otelsdkusage.util.OpenTelemetrySupport;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
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

    private void biz() {
        Tracer tracer = OpenTelemetrySupport.getTracer();
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

    private void child(String userType) {
        Span span = OpenTelemetrySupport.getTracer().spanBuilder("child span").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("user.type", userType);
            System.out.println(userType);
            biz();
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR, "handle child span error");
        } finally {
            span.end();
        }
    }

    @RequestMapping("/async")
    public String async() {
        System.out.println("UserController.async -- " + Thread.currentThread().getId());
        Span span = OpenTelemetrySupport.getTracer().spanBuilder("parent span").startSpan();
        span.setAttribute("user.id", "123456");
        try (Scope scope = span.makeCurrent()) {
            userService.async();
            child("vip");
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR, "handle parent span error");
        } finally {
            span.end();
        }
        return "async";
    }

}
