package com.example.demo.controller;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController
{
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request)
    {
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .message("Successfully sign in")
                .result(authenticationService.authenticate(request))
                .build();
    }
}
