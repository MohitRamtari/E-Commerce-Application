package com.ttn.mohitramtari.bootcampproject.ecommerce.app.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@FieldDefaults(level = PRIVATE)
public class ValidationErrors {
    String field;
    String defaultMessage;
}
