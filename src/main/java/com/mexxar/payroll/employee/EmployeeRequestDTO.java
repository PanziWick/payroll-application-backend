package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.employee.enums.GenderEnum;
import com.mexxar.payroll.employee.enums.MaritalEnum;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeRequestDTO(
        @NotBlank(message = "First name cannot be empty")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @Size(max = 50, message = "Middle name must not exceed 50 characters")
        String middleName,

        @NotBlank(message = "Last name cannot be empty")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,

        @NotNull(message = "Date of birth cannot be null")
        @Past(message = "Date of birth must be a past date")
        LocalDate dob,

        @NotBlank(message = "Contact number cannot be empty")
        @Pattern(
                regexp = "^\\+?[0-9]{7,15}$",
                message = "Invalid contact number format")
        String contactNumber,

        @NotNull(message = "Hire date cannot be null")
        LocalDate hireDate,

        @Size(max = 20, message = "EPF number must not exceed 20 characters")
        String epfNumber,

        @NotBlank(message = "NIC number cannot be empty")
        @Size(max = 20, message = "NIC number must not exceed 20 characters")
        String nationalIdNumber,

        @NotNull(message = "Gender cannot be null")
        GenderEnum gender,

        @NotNull(message = "Marital cannot be null")
        MaritalEnum marital,

        @NotNull(message = "Department ID cannot be null")
        Long departmentId,

        @NotNull(message = "Designation ID cannot be null")
        Long designationId,

        @NotNull(message = "Status cannot be null")
        StatusEnum status
)
{}
