package com.mexxar.payroll.user;

import com.mexxar.payroll.common.enums.StatusEnum;
import jakarta.validation.constraints.*;

public record UserRequestDTO(
        @NotBlank(message = "Username must not be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String userName,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z\\d\\W_]+$", /* \\W matches any non-word character */
                message = "Password must include at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String password,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Contact number must not be blank")
        @Pattern(
                regexp = "^(\\+\\d{1,3})?\\d{10,15}$",
                message = "Contact number must be valid and include the country code if applicable"
        )
        String contactNumber,

        StatusEnum status,

        Long roleId
)
{}
