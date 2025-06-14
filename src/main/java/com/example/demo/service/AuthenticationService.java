package com.example.demo.service;

import com.example.demo.configuration.JwtConfig;
import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.request.RefreshRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.entity.InvalidatedTokenEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.ErrorCode;
import com.example.demo.exception.AppException;
import com.example.demo.repository.InvalidatedTokenRepository;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService
{
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;
    JwtConfig jwtConfig;

    public AuthenticationResponse authenticate(AuthenticationRequest request)
    {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return AuthenticationResponse.builder()
                .token(jwtConfig.generateToken(user))
                .build();
    }

    public void logout(LogoutRequest request) throws JOSEException, ParseException
    {
        SignedJWT signedJWT = jwtConfig.verifyToken(request.getToken());

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedTokenEntity invalidatedTokenEntity = InvalidatedTokenEntity.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedTokenEntity);
    }
    
    public AuthenticationResponse refreshToken(RefreshRequest request) throws JOSEException, ParseException
    {
        SignedJWT signedJWT = jwtConfig.verifyToken(request.getToken());

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        Long userId = Long.parseLong(signedJWT.getJWTClaimsSet().getSubject());

        InvalidatedTokenEntity invalidatedTokenEntity = InvalidatedTokenEntity.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedTokenEntity);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        return AuthenticationResponse.builder()
                .token(jwtConfig.generateToken(user))
                .build();
    }
}
