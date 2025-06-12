package com.example.demo.controller;

import com.example.demo.dto.request.UserCreateRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController
{
    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request)
    {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Successfully create user")
                .result(userService.createUser(request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers()
    {
        return ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Successfully retrieve all users")
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable Long id)
    {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Successfully retrieve all users")
                .result(userService.getUserById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUserById(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request
    )
    {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Successfully update user")
                .result(userService.updateUser(id, request))
                .build();
    }
}
