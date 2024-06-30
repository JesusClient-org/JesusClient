package cum.jesus.jesusclient.command.arguments;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;

public abstract class ArgumentParser<T> {
    private final TypeToken<T> type = new TypeToken<T>(getClass()) {};
    public final Class<?> typeClass = type.getRawType();

    public abstract T parse(String arg) throws Exception;

    public List<String> autoComplete(String current, Parameter parameter) {
        return Collections.emptyList();
    }
}
