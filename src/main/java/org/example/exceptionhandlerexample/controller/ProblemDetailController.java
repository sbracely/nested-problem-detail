package org.example.exceptionhandlerexample.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.reuqest.problem.ProblemDetailRequest;
import org.example.exceptionhandlerexample.reuqest.valid.annocation.CheckMultipartFile;
import org.example.exceptionhandlerexample.reuqest.valid.annocation.CheckPassword;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/problem-detail")
public class ProblemDetailController {

    @GetMapping("/param")
    public void get(@RequestParam Integer id) {
        log.info("id: {}", id);
    }

    @PutMapping(path = "/consume-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putConsumeJson(ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @PutMapping(path = "/produce-json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void putProduceJson(ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer iid) {
        log.info("iid: {}", iid);
    }

    @PutMapping("/file")
    public void file(@RequestPart MultipartFile file) {
        log.info("file: {}", file);
    }

    @GetMapping("/matrix/{id}")
    public void matrix(@PathVariable String id, @MatrixVariable List<String> list) {
        log.info("id: {}, list: {}", id, list);
    }

    @GetMapping("/cookie")
    public void cookie(@CookieValue String cookieValue) {
        log.info("cookieValue: {}", cookieValue);
    }

    @GetMapping("/header")
    public void header(@RequestHeader String header) {
        log.info("header: {}", header);
    }

    @GetMapping(path = "/unsatisfied", params = {"type=1", "exist", "!debug"})
    public void unsatisfied() {
        log.info("unsatisfied");
    }

    @PostMapping("/create")
    public void create(@RequestBody @Validated ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @GetMapping(value = "/cookie-value")
    public String cookieValue(@CookieValue @Length(min = 2, message = "姓名长度最小是 2") String name) {
        log.info("name: {}", name);
        return name;
    }

    @GetMapping("/matrix-variable/{id}")
    public void matrixVariable(@PathVariable String id,
                               @MatrixVariable @Size(max = 2, message = "最大长度是 2") List<String> list) {
        log.info("id: {}, list: {}", id, list);
    }

    @GetMapping("/model-attribute")
    public void modelAttribute(@CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @GetMapping("/path-variable/{id}")
    public void pathVariable(@PathVariable @Length(min = 2, message = "id 最小长度是 2") String id) {
        log.info("id: {}", id);
    }

    @PostMapping("/request-body")
    public void requestBody(@CheckPassword(message = "密码不能是空") ProblemDetailRequest problemDetailRequest) {
        log.info("problemDetailRequest: {}", problemDetailRequest);
    }

    @GetMapping("/request-header")
    public void requestHeader(@RequestHeader @Length(min = 2, message = "最小长度是 2") String headerValue) {
        log.info("headerValue: {}", headerValue);
    }

    @GetMapping("/request-param")
    public void requestParam(@NotBlank(message = "参数不能为空") String param,
                             @NotNull(message = "参数2不能为空") @NotNull(message = "参数2不能为null") String param2) {
        log.info("param: {}, param2: {}", param, param2);
    }

    @GetMapping("/request-part")
    public void requestPart(@RequestPart(required = false) @CheckMultipartFile(extensionIncludeMessage = "文件类型不支持",
            extensionInclude = "txt", requiredMessage = "文件不能为空") MultipartFile file) {
        log.info("file: {}", file);
    }

    @GetMapping("/request-other")
    public void requestOther(@SessionAttribute(required = false) @NotBlank(message = "sessionAttribute 不能为空")
                             String sessionAttribute,
                             @RequestAttribute(required = false) @NotBlank(message = "requestAttribute 不能为空")
                             String requestAttribute,
                             @Value("") @NotBlank(message = "value 不能为空") String value) {
        log.info("sessionAttribute: {}, requestAttribute: {}, value: {}", sessionAttribute, requestAttribute, value);
    }

    @PostMapping("/request-body-validation-result")
    public void requestBodyValidationResult(@RequestBody List<@NotBlank(message = "元素不能包含空") String> list) {
        log.info("list.size = {}, list: {}", list.size(), list);
    }
}
