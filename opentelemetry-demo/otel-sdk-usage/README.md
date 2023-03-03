## 通过OpenTelemetry上报Java应用数据
### 方法二：使用OpenTelemetry Java SDK手动埋点

1. 添加Maven依赖
```xml
<dependencies>
    <!-- 略去了其他依赖，只展示OpenTelemetry相关的API -->
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
  </dependency>
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk-trace</artifactId>
  </dependency>
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
  </dependency>
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
  </dependency>
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-semconv</artifactId>
    <version>1.23.0-alpha</version>
  </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-bom</artifactId>
            <version>1.23.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2. OpenTelemetry初始化配置
- <logical-service-name>为服务名，<host-name>为主机名，请根据您的实际场景配置
```java
Resource resource = Resource.getDefault()
        .merge(Resource.create(Attributes.of(
                ResourceAttributes.SERVICE_NAME, "<logical-service-name>",
                ResourceAttributes.HOST_NAME, "<host-name>" 
        )));

SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
                .setEndpoint("<endpoint>")
                .addHeader("Authentication", "<token>")
                .build()).build())
        .setResource(resource)
        .build();

OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
        .buildAndRegisterGlobal();
```
3. 启动应用，端口号：8082（在`resources/application.properties`中修改）
- 访问地址：`localhost:8082/user/async`