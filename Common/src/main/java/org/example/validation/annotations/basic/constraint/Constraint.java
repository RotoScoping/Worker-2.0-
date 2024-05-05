package org.example.validation.annotations.basic.constraint;

import java.lang.annotation.*;


/**
 * Аннотация, указывающая обработчик, реализцющий интерфейс ConstraintValidator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {
    /**
     * Validated by class.
     *
     * @return the class
     */
    Class<? extends ConstraintValidator<?, ?>> validatedBy();
}