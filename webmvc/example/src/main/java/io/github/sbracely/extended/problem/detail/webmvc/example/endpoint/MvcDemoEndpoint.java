package io.github.sbracely.extended.problem.detail.webmvc.example.endpoint;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
@Endpoint(id = "demo")
public class MvcDemoEndpoint {

    @ReadOperation
    public String hello(@Selector String name, String param1, String param2) {
        return "hello " + name + ", param1: " + param1 + ", param2: " + param2;
    }

    @WriteOperation
    public String update(@RequestBody Map<String, Object> data) {
        return "Update: " + data;
    }
}
