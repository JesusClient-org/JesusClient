package cum.jesus.jesusclient.module.settings;

import com.google.gson.JsonObject;

import java.util.function.Predicate;

public abstract class Setting<T> {
    private String name;
    private T object;
    private T defaultVal;

    private Predicate<T> validator;

    Setting(String name, T defaultVal, Predicate<T> validator) {
        this.name = name;
        this.object = defaultVal;
        this.defaultVal = defaultVal;
        this.validator = validator;
    }

    public abstract void addToJsonObject(JsonObject obj);

    public abstract void fromJsonObject(JsonObject obj);

    public String getName() {
        return name;
    }

    public T getObject() {
        return object;
    }

    public boolean setObject(T object) {
        if (validator != null && !validator.test(object)) return false;

        this.object = object;

        return true;
    }

    public void setValidator(Predicate<T> validator) {
        this.validator = validator;
    }

    public Object getDefault() {
        return defaultVal;
    }
}
