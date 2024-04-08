package core.validation.annotations;

import core.validation.annotations.basic.constraint.Constraint;
import core.validation.annotations.basic.constraint.impl.NotNullConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация-метка для проверки поля на null.
 */
@Constraint(validatedBy = NotNullConstraint.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "Значение поля {name} не может быть null";
}