package com.example.demo.exception;

import com.example.demo.enums.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException
{
    ErrorCode errorCode;

    public AppException(ErrorCode errorCode)
    {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
