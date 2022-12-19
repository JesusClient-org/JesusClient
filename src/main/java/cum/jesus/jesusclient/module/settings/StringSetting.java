package cum.jesus.jesusclient.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {
    public StringSetting(String name, String defaultVal, boolean premium) {
        this(name, defaultVal, null, premium);
    }

    public StringSetting(String name, String defaultVal) {
        this(name, defaultVal, null);
    }

    public StringSetting(String name, String defaultVal, Predicate<String> validator, boolean premium) {
        super(name, defaultVal, validator, premium);
    }

    public StringSetting(String name, String defaultVal, Predicate<String> validator) {
        super(name, defaultVal, validator, false);
    }

    @Override
    public void addToJsonObject(@NotNull JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(@NotNull JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isString()) {
                setObject(element.getAsString());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}
