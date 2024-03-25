package core.validation.annotations.basic.constraint.impl;

import core.validation.annotations.Positive;
import core.validation.annotations.basic.constraint.ConstraintValidator;

/**
 * Класс обработчик positive constraint
 */
public class PositiveConstraint implements ConstraintValidator<Positive, Number> {


    @Override
    public void initialize(Positive constraintAnnotation) {

    }

    @Override
    public boolean isValid(Number value) {
        if (value == null) {
            return true;
        }
        return value.doubleValue() >= 0;
    }
}

