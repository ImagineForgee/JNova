package jnova.core.validation;

import jakarta.validation.*;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParameterValidator {
    private final Validator validator;

    public ParameterValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

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
