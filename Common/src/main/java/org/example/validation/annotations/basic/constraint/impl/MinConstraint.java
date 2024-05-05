package org.example.validation.annotations.basic.constraint.impl;

import org.example.validation.annotations.Min;
import org.example.validation.annotations.basic.constraint.ConstraintValidator;

/**
 * Класс обработчик min constraint
 */
public class MinConstraint implements ConstraintValidator<Min, Number> {
    private long minValue;

    @Override
    public void initialize(Min constraintAnnotation) {
        this.minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value) {
        if (value == null) {
            return true;
        }

        return value.doubleValue() >= minValue;
    }
}
