package com.mexxar.payroll.designation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DesignationNotFoundException extends RuntimeException {
    public DesignationNotFoundException(String message) {
        super(message);
    }
}
