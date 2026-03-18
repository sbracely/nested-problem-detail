package org.example.exceptionhandlerexample.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.exceptionhandlerexample.model.User;
import org.example.exceptionhandlerexample.reuqest.user.UserRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/get-by-id")
    public User getById(@RequestParam Integer id) {
        log.info("getById id = {}", id);
        User user = new User();
        user.setId(id);
        return user;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserRequest put(UserRequest userRequest) {
        log.info("put userRequest = {}", userRequest);
        return userRequest;
    }

    @PutMapping(path = "/v2", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserRequest putV2(UserRequest userRequest) {
        log.info("put userRequest = {}", userRequest);
        return userRequest;
    }

    @DeleteMapping("/path-variable/{id}")
    public void delete(@PathVariable Integer iid) {
        log.info("delete iid = {}", iid);
    }

    @PutMapping("/file")
    public void file(@RequestPart("file") MultipartFile file) {
        log.info("file = {}", file);
    }
}
