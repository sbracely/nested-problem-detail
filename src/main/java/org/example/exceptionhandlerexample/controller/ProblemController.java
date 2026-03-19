package org.example.exceptionhandlerexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.reuqest.problem.ProblemRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/problem")
public class ProblemController {

    @GetMapping("/get-by-id")
    public void getById(@RequestParam Integer id) {
        log.info("id = {}", id);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void put(ProblemRequest problemRequest) {
        log.info("problemRequest = {}", problemRequest);
    }

    @PutMapping(path = "/v2", produces = MediaType.APPLICATION_JSON_VALUE)
    public void putV2(ProblemRequest problemRequest) {
        log.info("problemRequest = {}", problemRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer iid) {
        log.info("iid = {}", iid);
    }

    @PutMapping("/file")
    public void file(@RequestPart MultipartFile file) {
        log.info("file = {}", file);
    }

    @GetMapping("/matrix/{id}")
    public void matrix(@PathVariable String id, @MatrixVariable List<String> list) {
        log.info("id = {}, list = {}", id, list);
    }

    @GetMapping("/cookie")
    public void cookie(@CookieValue String cookieValue) {
        log.info("cookieValue = {}", cookieValue);
    }
}
