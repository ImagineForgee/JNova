package jnova.annotations.tcp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerDeprecated {
    String since();
    String message() default "";
}
