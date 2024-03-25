package core.validation.annotations.basic.constraint;

import java.lang.annotation.*;



/**
 * Аннотация, указывающая обработчик, реализцющий интерфейс ConstraintValidator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Constraint {
    Class<? extends ConstraintValidator<?, ?>> validatedBy();
}