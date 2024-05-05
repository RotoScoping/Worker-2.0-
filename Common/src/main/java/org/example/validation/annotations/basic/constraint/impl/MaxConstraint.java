package org.example.validation.annotations.basic.constraint.impl;

import org.example.validation.annotations.Max;
import org.example.validation.annotations.basic.constraint.ConstraintValidator;

/**
 * Класс обработчик max constraint
 */
public class MaxConstraint implements ConstraintValidator<Max, Number> {
    private long maxValue;

    @Override
    public void initialize(Max constraintAnnotation) {
        this.maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value) {
        if (value == null) {
            return true;
        }

        return value.doubleValue() <= maxValue;
    }
}
