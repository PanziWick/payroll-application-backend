package com.mexxar.payroll.leave.exception;

public class LeavePolicyNotFoundException extends RuntimeException {
    public LeavePolicyNotFoundException(String message) {
        super(message);
    }
}
