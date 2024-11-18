package com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationExceptionList {
    private Integer statusCode;
    private String errorMessage;
    private List<ValidationErrors> errors;

    public ValidationExceptionList(Integer statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
