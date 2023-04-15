package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.NotificationType;
import cum.jesus.jesusclient.remote.Premium;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public abstract class Module {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected String name;
    private String description;
    private Category category;
    private boolean canBeEnabled;
    private boolean hidden;
    private int keybind;
    private boolean toggled;
    private boolean premiumFeature = false;
    private boolean shouldNotify = true;

    protected Module(String name, String description, Category category) {
        this(name, description, category, true, false, Keyboard.KEY_NONE);
    }

    protected Module(String name, String description, Category category, boolean canBeEnabled, boolean hidden, int keybind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.canBeEnabled = canBeEnabled;
        this.hidden = hidden;
        this.keybind = keybind;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public boolean canBeEnabled() {
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

    public boolean shouldNotify() {
        return shouldNotify;
    }

    public boolean isPremiumFeature() {
        return premiumFeature;
    }

    public void setToggled(boolean state) {
        if (isPremiumFeature() && !Premium.isUserPremium()) {
            this.toggled = false;
            return;
        }

        if (state) {
            this.toggled = true;

            onEnable();

            if (!Gui.hideNotifs.getObject() && shouldNotify()) {
                NotificationManager.show(new Notification(NotificationType.INFO, getName(), "Enabled " + getName(), 1));
            }

        } else {
            this.toggled = false;

            onDisable();

            if (!Gui.hideNotifs.getObject() && shouldNotify()){
                NotificationManager.show(new Notification(NotificationType.INFO, getName(), "Disabled " + getName(), 1));
            }
        }
    }

    public void setToggledNoNotif(boolean state) {
        if (isPremiumFeature() && !Premium.isUserPremium()) {
            this.toggled = false;
            return;
        }

        if (state) {
            this.toggled = true;
            onEnable();
        } else {
            this.toggled = false;
            onDisable();
        }
    }

    public void toggle() {
        setToggled(!isToggled());
    }

    protected void onEnable() {}

    protected void onDisable() {}

    protected void handleKeybind() {
        if (isPremiumFeature() && !Premium.isUserPremium()) {
            return;
        }

        toggle();
    }
}