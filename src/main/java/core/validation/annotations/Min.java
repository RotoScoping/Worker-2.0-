package core.validation.annotations;

import core.validation.annotations.basic.constraint.Constraint;
import core.validation.annotations.basic.constraint.impl.MinConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация-метка для проверки поля на min допустимое значение
 */
@Constraint(validatedBy = MinConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {

    /**
     * Value long.
     *
     * @return the long
     */
    long value();

    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "Значение должно быть больше {min}";
}