package org.example.validation.annotations.basic.constraint;


/**
 * Интерфейс, предназначенный для обработчиков constraint
 *
 * @param <Annotation> the type parameter
 * @param <Type>       the type parameter
 */
public interface ConstraintValidator<Annotation, Type> {
    /**
     * Инициализация обработчика параметрами из аннотации
     *
     * @param constraintAnnotation - аннотация constraint
     */

    void initialize(Annotation constraintAnnotation);

    /**
     * Проверка содержимого поля с параметром валидации
     *
     * @param value the value
     * @return boolean boolean
     */
    boolean isValid(Type value);
}