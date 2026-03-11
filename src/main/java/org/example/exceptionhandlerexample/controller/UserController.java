package org.example.exceptionhandlerexample.controller;

import jakarta.validation.Valid;
import org.example.exceptionhandlerexample.model.User;
import org.example.exceptionhandlerexample.reuqest.user.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/queryById/{id}")
    public User queryById(@PathVariable Integer id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    @PostMapping("/update")
    public UserRequest update(@Valid UserRequest userRequest) {
        return userRequest;
    }
}
