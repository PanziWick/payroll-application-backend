package com.mexxar.payroll.payslip.exception;

public class PaySlipNotFoundException extends RuntimeException {
    public PaySlipNotFoundException(String message) {
        super(message);
    }
}
