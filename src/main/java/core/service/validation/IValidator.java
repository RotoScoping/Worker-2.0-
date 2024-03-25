package core.service.validation;

import java.util.List;

/**
 * The interface Validator.
 *
 * @param <T> the type parameter
 */
public interface IValidator<T> {

    /**
     * Is valid list.
     *
     * @param model the model
     * @return the list
     */
    List<String> isValid(T model);
}
