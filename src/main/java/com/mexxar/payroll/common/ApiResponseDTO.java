package com.mexxar.payroll.common;

import lombok.Data;

@Data
public class ApiResponseDTO<T> {
    private String message;
    private T data;

    public ApiResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
