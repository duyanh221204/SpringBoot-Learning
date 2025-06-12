package com.example.demo.configuration;

import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.ErrorCode;
import com.example.demo.exception.AppException;
import com.example.demo.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Component
@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtConfig
{
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${issuer}")
    String issuer;

    @NonFinal
    @Value("${signer_key}")
    String signerKey;

    public String generateToken(UserEntity user)
    {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(Long.toString(user.getId()))
                .issuer(issuer)
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .expirationTime(
                        new Date(
                                Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                        )
                )
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try
        {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        }
        catch (JOSEException e)
        {
            log.error("Cannot generate token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(UserEntity user)
    {
        StringBuilder stringBuilder = new StringBuilder();
        if (!user.getRoles().isEmpty())
        {
            Set<RoleEntity> roles = user.getRoles();
            for (RoleEntity role : roles)
                stringBuilder.append(role.getName()).append(" ");
        }
        return stringBuilder.toString().trim();
    }

    public SignedJWT verifyToken(String token) throws JOSEException, ParseException
    {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedJWT.verify(verifier) || expiryTime.after(new Date()) || invalidatedTokenRepository.existsById(jti))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
