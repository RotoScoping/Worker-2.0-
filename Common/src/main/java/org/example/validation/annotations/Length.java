package org.example.validation.annotations;


import org.example.validation.annotations.basic.constraint.Constraint;
import org.example.validation.annotations.basic.constraint.impl.LengthConstraint;

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
    String message() default "Поле {field} не должно быть более {length} символов";
}
