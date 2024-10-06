package cum.jesus.jesusclient.module;

import com.lukflug.panelstudio.base.IBoolean;
import com.lukflug.panelstudio.base.IToggleable;
import com.lukflug.panelstudio.setting.ILabeled;
import com.lukflug.panelstudio.setting.IModule;
import com.lukflug.panelstudio.setting.ISetting;
import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.config.IConfigurable;
import cum.jesus.jesusclient.event.EventManager;
import cum.jesus.jesusclient.file.builder.FileBuilder;
import cum.jesus.jesusclient.file.reader.FileReader;
import cum.jesus.jesusclient.module.modules.render.ClickGUIModule;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.setting.settings.KeybindSetting;
import cum.jesus.jesusclient.setting.Setting;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class Module implements IConfigurable, IModule {
    protected static final Minecraft mc = JesusClient.mc;
    protected String name;
    private String description;
    private ModuleCategory category;
    private List<Setting> settings;
    private boolean alwaysActive;
    private boolean hidden;
    private Keybind keybind = null;
    private boolean toggled;

    protected Module(String name, String description, ModuleCategory category) {
        this(name, description, category, true, false, Keyboard.KEY_NONE);
    }

    protected Module(String name, String description, ModuleCategory category, boolean alwaysActive, boolean hidden, @Nullable Integer keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.settings = new ArrayList<>();
        this.alwaysActive = alwaysActive;
        this.hidden = hidden;

        if (keybind != null) {
            this.keybind = new Keybind(keybind);
            this.keybind.setOnPress(this::onKeybind);

            addSetting(new KeybindSetting("Keybind", "Toggles the module", this.keybind));
        }

        if (alwaysActive) {
            EventManager.register(this);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public IBoolean isVisible() {
        return () -> !hidden;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    @Override
    public String getFileName() {
        return "modules" + File.separatorChar + category.name + "." + name.replace(" ", "");
    }

    @Override
    public Stream<ISetting<?>> getSettings() {
        return settings.stream().filter(setting -> setting instanceof ISetting).sorted(Comparator.comparing(ILabeled::getDisplayName)).map(setting -> (ISetting<?>) setting);
    }

    @Override
    public List<Setting> getSettings2() {
        return settings;
    }

    @Override
    public void writeSpecial(FileBuilder builder) {
        builder.addBoolean(toggled);
    }

    @Override
    public void readSpecial(FileReader reader) {
        toggled = reader.getBoolean();
    }

    public boolean isAlwaysActive() {
        return alwaysActive;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Keybind getKeybind() {
        return keybind;
    }

    public boolean isToggled() {
        return toggled;
    }

    @Override
    public IToggleable isEnabled() {
        return new IToggleable() {
            @Override
            public void toggle() {
                Module.this.toggle();
            }

            @Override
            public boolean isOn() {
                return toggled;
            }
        };
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

    protected void onEnable() {
        if (!alwaysActive) {
            EventManager.register(this);
        }
    }

    protected void onDisable() {
        if (!alwaysActive) {
            EventManager.unregister(this);
        }
    }

    protected void onKeybind() {
        toggle();

        if (ClickGUIModule.INSTANCE.enableNotifications.getValue()) {
            NotificationManager.notify(new Notification((toggled ? "Enabled " : "Disabled ") + name));
        }
    }

    protected void addSettings(Setting... settings) {
        this.settings.addAll(Arrays.asList(settings));
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        settings.add(setting);
        return setting;
    }
}
