package com.mexxar.payroll.commissiontype.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CommissionTypeNotFoundException extends RuntimeException {
    public CommissionTypeNotFoundException(String message) {
        super(message);
    }
}
