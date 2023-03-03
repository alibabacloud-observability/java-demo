## 通过OpenTelemetry上报Java应用数据
### 方法三：同时使用Java Agent和Java SDK埋点

1. 下载[OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases)

2. 添加Maven依赖
```xml
<dependencies>
    <!-- 略去了其他依赖，只展示OpenTelemetry相关的API -->
  <dependency>
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
        <artifactId>opentelemetry-extension-annotations</artifactId>
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
    <dependency>
        <groupId>io.opentelemetry</groupId>
        <artifactId>opentelemetry-sdk-extension-autoconfigure</artifactId>
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

3. 修改Java启动的VM参数
```
-javaagent:/path/to/opentelemetry-javaagent.jar    //请将路径修改为您文件下载的实际地址。
-Dotel.resource.attributes=service.name=<appName>     //<appName> 为应用名。
-Dotel.exporter.otlp.headers=Authentication=<token>
-Dotel.exporter.otlp.endpoint=<endpoint>
```
4. 启动应用，端口号：8083（在`resources/application.properties`中修改）
