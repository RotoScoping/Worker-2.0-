package core.validation.annotations.basic.constraint.impl;

import core.validation.annotations.NotNull;
import core.validation.annotations.basic.constraint.ConstraintValidator;
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
