package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse
{
    Long id;

    String username;

    String password;

    String firstName;

    String lastName;
}
