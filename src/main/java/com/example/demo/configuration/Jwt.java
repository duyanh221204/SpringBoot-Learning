package com.example.demo.configuration;

import com.example.demo.entity.UserEntity;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Configuration
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Jwt
{
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
                .expirationTime(
                        new Date(
                                Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                        )
                )
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
}
