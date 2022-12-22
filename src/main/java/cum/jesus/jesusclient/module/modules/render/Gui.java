package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.clickgui.ClickGui;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.module.settings.NumberSetting;
import cum.jesus.jesusclient.module.settings.StringSetting;
import cum.jesus.jesusclient.utils.Logger;
import org.lwjgl.input.Keyboard;

public class Gui extends Module {
    public static BooleanSetting hideNotifs = new BooleanSetting("Hide notifications", false);

    public static StringSetting prefix = new StringSetting("Command prefix", "-");

    public Gui() {
        super("ClickGui", "Config Gui", Category.RENDER, true, false,  Keyboard.KEY_F4);
    }

    @Override
    protected void onEnable() {
        JesusClient.display = ClickGui.INSTANCE;
        setToggled(false);
    }

    @Override
    public boolean shouldNotify() {
        return false;
    }
}
