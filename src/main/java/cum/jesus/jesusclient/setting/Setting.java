package cum.jesus.jesusclient.setting;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.ISetting;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

public abstract class Setting<T> implements ILabeled {
    private String name;
    private String configName;
    private String description;
    private boolean hidden;

    public final List<Setting<?>> subSettings = new ArrayList<>();

    private T value;
    private T defaultValue;

    protected BooleanSupplier visibilityDependency = null;
    protected ChangeListener<T> changeListener = null;

    protected Setting(String name, String description, T defaultValue, boolean hidden) {
        this.name = name;
        this.configName = name;
        this.description = description;
        this.hidden = hidden;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getConfigName() {
        return configName;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public Stream<ISetting<?>> getSubSettings() {
        if (subSettings.size() == 0) return null;
        return subSettings.stream().filter(setting -> setting instanceof ISetting).sorted(Comparator.comparing(Setting::getDisplayName)).map(setting -> (ISetting<?>) setting);
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        T oldValue = this.value;
        this.value = value;

        if (changeListener != null) {
            changeListener.invoke(oldValue, value);
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setChangeListener(ChangeListener<T> changeListener) {
        this.changeListener = changeListener;
    }

    public boolean shouldBeVisible() {
        return (visibilityDependency == null || visibilityDependency.getAsBoolean()) && !hidden;
    }

    @Override
    public IBoolean isVisible() {
        return this::shouldBeVisible;
    }

    @SuppressWarnings("unchecked")
    public <S extends Setting<?>> S setConfigName(String name) {
        configName = name;
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    public <S extends Setting<?>> S withDependency(BooleanSupplier dependency) {
        visibilityDependency = dependency;
        return (S) this;
    }

    public void reset() {
        setValue(getDefaultValue());
    }

    public abstract void addToBuilder(ConfigBuilder builder);

    public abstract void getFromReader(ConfigReader reader);

    public interface ChangeListener<T> {
        void invoke(T oldValue, T newValue);
    }
}
