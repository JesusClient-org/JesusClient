package cum.jesus.jesusclient.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface SubCommandGroup {
    // Name. Java is autistic.
    String value();

    String[] aliases() default {};
}