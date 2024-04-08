package core.validation.annotations;

import core.validation.annotations.basic.constraint.Constraint;
import core.validation.annotations.basic.constraint.impl.PositiveConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация-метка для инварианта позитивных чисел
 */
@Constraint(validatedBy = PositiveConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Positive {
    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "Значение поля {field} должно быть положительным";
}
