package com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private Integer statusCode;
    private String errorMessage;
}
