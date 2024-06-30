package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.config.IConfigurable;
import cum.jesus.jesusclient.setting.BooleanSetting;
import cum.jesus.jesusclient.setting.NumberSetting;
import cum.jesus.jesusclient.setting.Setting;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Module implements IConfigurable {
    protected static final Minecraft mc = JesusClient.mc;
    protected String name;
    private String description;
    private ModuleCategory category;
    private List<Setting> settings;
    private boolean canBeEnabled;
    private boolean hidden;
    private int keybind;
    private boolean toggled;

    protected Module(String name, String description, ModuleCategory category) {
        this(name, description, category, true, false, Keyboard.KEY_NONE);
    }

    protected Module(String name, String description, ModuleCategory category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.settings = new ArrayList<>();
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;

        this.settings.add(new BooleanSetting("toggled", false));
        this.settings.add(new NumberSetting<Integer>("keybind", keybind, Integer.MIN_VALUE, Integer.MAX_VALUE));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    @Override
    public List<Setting> getSettings() {
        return settings;
    }

    public boolean isCanBeEnabled() {
        return canBeEnabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public int getKeybind() {
        return keybind;
    }

    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean state) {
        if (state) {
            toggled = true;
            onEnable();
        } else {
            toggled = false;
            onDisable();
        }
    }

    public void toggle() {
        setToggled(!toggled);
    }

    protected abstract void onEnable();

    protected abstract void onDisable();

    protected void handleKeybind() {
        toggle();
    }

    protected void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }
}
