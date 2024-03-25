package core.validation.annotations.basic.constraint.impl;

import core.validation.annotations.Length;
import core.validation.annotations.basic.constraint.ConstraintValidator;

/**
 * Класс обработчик length constraint
 */
public class LengthConstraint implements ConstraintValidator<Length, String> {

    private long length;
    @Override
    public void initialize(Length constraintAnnotation) {
        this.length = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        return value.length() <= length;
    }
}
