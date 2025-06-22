package jnova.core.validation;

import jakarta.validation.*;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validates method parameters using Bean Validation annotations.
 *
 * This class uses a {@link Validator} to check if method parameters satisfy
 * the constraints defined by Bean Validation annotations (e.g., {@code @NotNull},
 * {@code @Size}, etc.). It identifies parameters annotated with {@code @Valid}
 * and validates them accordingly, returning a list of violation messages.
 */
public class ParameterValidator {
    private final Validator validator;

        /**
     * Constructs a ParameterValidator instance.
     *
     * Initializes the validator using the default validation factory.
     */
    public ParameterValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

        /**
     * Validates the given arguments against the constraints defined by the `@Valid` annotation on the corresponding parameters.
     *
     * @param args       An array of arguments to validate.
     * @param parameters An array of {@link Parameter} objects representing the method parameters.
     * @return A list of validation error messages. Returns an empty list if no violations are found.
     */
    public List<String> validateParameters(Object[] args, Parameter[] parameters) {
        List<String> violations = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Parameter param = parameters[i];

            if (arg == null || !param.isAnnotationPresent(Valid.class)) continue;

            Set<ConstraintViolation<Object>> result = validator.validate(arg);
            for (ConstraintViolation<?> v : result) {
                violations.add(v.getPropertyPath() + ": " + v.getMessage());
            }
        }

        return violations;
    }

}
