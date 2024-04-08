package core.validation.annotations.basic.constraint.impl;

import core.validation.annotations.Max;
import core.validation.annotations.basic.constraint.ConstraintValidator;

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
