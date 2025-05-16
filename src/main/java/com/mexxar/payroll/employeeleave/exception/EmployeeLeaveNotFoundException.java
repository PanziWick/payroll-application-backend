package com.mexxar.payroll.employeeleave.exception;

public class EmployeeLeaveNotFoundException extends RuntimeException {
    public EmployeeLeaveNotFoundException(String message) {
        super(message);
    }
}
