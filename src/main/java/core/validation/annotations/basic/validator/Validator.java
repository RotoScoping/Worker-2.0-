package core.validation.annotations.basic.validator;

import core.validation.annotations.basic.constraint.Constraint;
import core.validation.annotations.basic.constraint.ConstraintValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс валидатора, проверяющего модели на валидный ивариант
 */
public class Validator {

    private final List<Class<?>> classes;

    /**
     * Instantiates a new Validator.
     *
     * @param pack the pack
     */
    public Validator(String pack) {
        this.classes = PackageScan.getClasses(pack);
    }


    /**
     * Validate list.
     *
     * @param obj the obj
     * @return the list
     */
    public List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();
        Class<?> objClass = obj.getClass();
        for (Field field : objClass.getDeclaredFields()) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType()
                        .isAnnotationPresent(Constraint.class)) {
                    if (!validateField(annotation, obj, field))
                        errors.add(String.format("Поле %s не валидно", field.getName()));
                }
            }
            if (classes.contains(field.getType())) {
                if (field.getType().isEnum()) continue;
                field.setAccessible(true);
                Object nestedObject = null;
                try {
                    nestedObject = field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (nestedObject != null) {
                    List<String> nestedErrors = validate(nestedObject);
                    errors.addAll(nestedErrors);

                }
            }
        }
        return errors;
    }


    private boolean validateField(Annotation validation, Object obj, Field field) {

        Constraint constraint = validation.annotationType()
                .getAnnotation(Constraint.class);
        Class<? extends ConstraintValidator> validateHandler = constraint.validatedBy();

        try {
            ConstraintValidator validatorHandlerInstance = validateHandler.getDeclaredConstructor()
                    .newInstance();
            Method initializeMethod = validateHandler.getMethod("initialize", validation.annotationType());
            initializeMethod.invoke(validatorHandlerInstance, validation);

            field.setAccessible(true);
            Object fieldValue = field.get(obj);

            Method isValidMethod = validateHandler.getMethod("isValid", Object.class);
            return (Boolean) isValidMethod.invoke(validatorHandlerInstance, fieldValue);

        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }



}