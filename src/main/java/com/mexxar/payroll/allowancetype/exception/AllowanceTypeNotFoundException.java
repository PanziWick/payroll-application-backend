package com.mexxar.payroll.allowancetype.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AllowanceTypeNotFoundException extends RuntimeException {
    public AllowanceTypeNotFoundException(String message) {
        super(message);
    }
}
