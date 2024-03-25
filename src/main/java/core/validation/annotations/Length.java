package core.validation.annotations;

import core.validation.annotations.basic.constraint.Constraint;
import core.validation.annotations.basic.constraint.impl.LengthConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация-метка для проверки поля на max допустимое значение
 */
@Constraint(validatedBy = LengthConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Length {
    long value();
    String message() default "Поле {field} не должно быть более {length} символов";
}
