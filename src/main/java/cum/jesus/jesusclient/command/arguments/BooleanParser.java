package cum.jesus.jesusclient.command.arguments;

import java.lang.reflect.Parameter;
import java.util.*;

public final class BooleanParser extends ArgumentParser<Boolean> {
    private static final Map<String, Boolean> VALUES = new HashMap<>();

    static {
        VALUES.put("true", true);
        VALUES.put("on", true);
        VALUES.put("yes", true);
        VALUES.put("y", true);
        VALUES.put("enabled", true);
        VALUES.put("enable", true);
        VALUES.put("1", true);

        VALUES.put("false", false);
        VALUES.put("off", false);
        VALUES.put("no", false);
        VALUES.put("n", false);
        VALUES.put("disabled", false);
        VALUES.put("disable", false);
        VALUES.put("0", false);
    }

    @Override
    public Boolean parse(String arg) throws Exception {
        return Optional.ofNullable(VALUES.get(arg)).orElseThrow(() -> new IllegalArgumentException(arg + " is not valid boolean"));
    }

    @Override
    public List<String> autoComplete(String current, Parameter parameter) {
        if (current != null && !current.trim().isEmpty()) {
            for (String value : VALUES.keySet()) {
                if (value.startsWith(current.toLowerCase(Locale.ENGLISH))) {
                    return Collections.singletonList(value);
                }
            }
        }

        return new ArrayList<>(VALUES.keySet());
    }
}
