package core.validation.annotations.basic.constraint;


/**
 * Интерфейс, предназначенный для обработчиков constraint
 */
public interface ConstraintValidator<Annotation, Type> {
    /**
     * Инициализация обработчика параметрами из аннотации
     * @param constraintAnnotation - аннотация constraint
     */

    void initialize(Annotation constraintAnnotation);

    /**
     * Проверка содержимого поля с параметром валидации
     * @return boolean
     */
    boolean isValid(Type value);
}