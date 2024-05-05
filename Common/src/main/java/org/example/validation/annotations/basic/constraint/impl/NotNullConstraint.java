package org.example.validation.annotations.basic.constraint.impl;

import org.example.validation.annotations.NotNull;
import org.example.validation.annotations.basic.constraint.ConstraintValidator;

/**
 * Класс обработчик not null constraint
 */
public class NotNullConstraint implements ConstraintValidator<NotNull, Object> {

    @Override
    public void initialize(NotNull constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value) {
        return value != null ;
    }
}
