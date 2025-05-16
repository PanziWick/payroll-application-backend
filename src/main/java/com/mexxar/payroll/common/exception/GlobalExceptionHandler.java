package com.mexxar.payroll.common.exception;

import com.mexxar.payroll.address.exception.AddressNotFoundException;
import com.mexxar.payroll.allowancetype.exception.AllowanceTypeNotFoundException;
import com.mexxar.payroll.authentication.exception.TokenRefreshException;
import com.mexxar.payroll.bankaccount.exception.BankAccountNotFoundException;
import com.mexxar.payroll.commissiontype.exception.CommissionTypeException;
import com.mexxar.payroll.commissiontype.exception.CommissionTypeNotFoundException;
import com.mexxar.payroll.department.exception.DepartmentNotFoundException;
import com.mexxar.payroll.designation.exception.DesignationNotFoundException;
import com.mexxar.payroll.employee.exception.EmployeeNotFoundException;
import com.mexxar.payroll.loan.exception.LoanNotFoundException;
import com.mexxar.payroll.payslip.exception.PaySlipNotFoundException;
import com.mexxar.payroll.payslipdetails.exception.PaySlipDetailsNotFoundException;
import com.mexxar.payroll.permission.exception.PermissionNotFoundException;
import com.mexxar.payroll.role.exception.RoleNotFoundException;
import com.mexxar.payroll.salary.exception.SalaryNotFoundException;
import com.mexxar.payroll.salaryadvance.exception.SalaryAdvanceNotFoundException;
import com.mexxar.payroll.user.exception.UserException;
import com.mexxar.payroll.user.exception.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String RESOURCE_NOT_FOUND_MSG = "Resource Not Found";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Internal Server Error";
    private static final String UNAUTHORIZED_MSG = "Unauthorized";
    private static final String BAD_REQUEST_MSG = "Bad Request";

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex) {
        logger.error("GlobalException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                500 ,
                ex.getMessage(),
                INTERNAL_SERVER_ERROR_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        logger.error("ValidationException: {}", ex.getMessage(), ex);

        String errorMessage = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Validation error occurred";

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                400,
                errorMessage,
                BAD_REQUEST_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleAddressNotFoundException(AddressNotFoundException ex) {
        logger.error("AddressNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleDepartmentNotFoundException(DepartmentNotFoundException ex) {
        logger.error("DepartmentNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DesignationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleDesignationNotFoundException(DesignationNotFoundException ex) {
        logger.error("DesignationNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        logger.error("EmployeeNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("UserNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponseDTO> handleUserException(UserException ex) {
        logger.error("UserException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                401,
                ex.getMessage(),
                UNAUTHORIZED_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponseDTO> handleTokenRefreshException(TokenRefreshException ex) {
        logger.error("TokenRefreshException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                400,
                ex.getMessage(),
                BAD_REQUEST_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CommissionTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleCommissionTypeException(CommissionTypeException ex) {
        logger.error("CommissionException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AllowanceTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleAllowanceTypeException(AllowanceTypeNotFoundException ex) {
        logger.error("AllowanceException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SalaryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleSalaryException(SalaryNotFoundException ex) {
        logger.error("SalaryException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LoanNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleLoanException(LoanNotFoundException ex) {
        logger.error("LoanException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SalaryAdvanceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleSalaryAdvanceException(SalaryAdvanceNotFoundException ex) {
        logger.error("SalaryAdvanceException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handlePermissionNotFoundException(PermissionNotFoundException ex) {
        logger.error("PermissionNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleRoleNotFoundException(RoleNotFoundException ex) {
        logger.error("RoleNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaySlipNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handlePaySlipNotFoundException(PaySlipNotFoundException ex) {
        logger.error("PaySlipNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaySlipDetailsNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handlePaySlipDetailsNotFoundException(PaySlipDetailsNotFoundException ex) {
        logger.error("PaySlipDetailsNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BankAccountNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleBankAccountNotFoundException(BankAccountNotFoundException ex) {
        logger.error("BankAccountNotFoundException: {}", ex.getMessage(), ex);

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                404,
                ex.getMessage(),
                RESOURCE_NOT_FOUND_MSG,
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
