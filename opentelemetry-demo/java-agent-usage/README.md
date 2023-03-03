## 通过OpenTelemetry上报Java应用数据
### 方法一：使用OpenTelemetry Java Agent自动埋点

1. 下载[OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases)

2. 修改Java启动的VM参数
```
-javaagent:/path/to/opentelemetry-javaagent.jar    //请将路径修改为您文件下载的实际地址。
-Dotel.resource.attributes=service.name=<appName>     //<appName> 为应用名。
-Dotel.exporter.otlp.headers=Authentication=<token>
-Dotel.exporter.otlp.endpoint=<endpoint>
```
3. 启动应用，端口号：8081（在`resources/application.properties`中修改）
