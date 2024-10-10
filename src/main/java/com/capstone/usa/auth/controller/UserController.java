package com.capstone.usa.auth.controller;

import com.capstone.usa.auth.dto.CreateUserDto;
import com.capstone.usa.auth.dto.LoginUserDto;
import com.capstone.usa.auth.dto.ModifyUserDto;
import com.capstone.usa.auth.model.User;
import com.capstone.usa.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    public UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(
            @RequestBody CreateUserDto dto
    ) {
        return userService.createUser(dto);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(
            @RequestBody LoginUserDto dto
    ) {
        return userService.loginWithAuthenticationManager(dto);
    }

    @GetMapping
    public User getMyProfile(
            @AuthenticationPrincipal User user
    ) {
        return user;
    }

    @PutMapping
    public ResponseEntity<?> modifyUser(
            @RequestBody ModifyUserDto dto,
            @AuthenticationPrincipal User user
    ) {
        return userService.modifyUser(dto, user);
    }
}
