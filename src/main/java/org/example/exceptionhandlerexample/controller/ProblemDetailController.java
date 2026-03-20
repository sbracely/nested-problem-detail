package org.example.exceptionhandlerexample.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.reuqest.problem.ProblemDetailRequest;
import org.hibernate.validator.constraints.Length;
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
    public void modelAttribute(ProblemDetailRequest problemDetailRequest) {
        log.info("problemRequest: {}", problemDetailRequest);
    }

    @GetMapping("/path-variable/{id}")
    public void pathVariable(@PathVariable @Length(min = 2, message = "id 最小长度是 2") String id) {
        log.info("id: {}", id);
    }
}
