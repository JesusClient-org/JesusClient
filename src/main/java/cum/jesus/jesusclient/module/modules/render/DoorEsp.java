package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.NumberSetting;

import java.awt.*;

public class DoorEsp extends Module {
    private NumberSetting<Integer> red = new NumberSetting<>("Color red", 255, 0, 255);
    private NumberSetting<Integer> green = new NumberSetting<>("Color green", 0, 0, 255);
    private NumberSetting<Integer> blue = new NumberSetting<>("Color blue", 0, 0, 255);

    Color color;

    public DoorEsp() {
        super("Door ESP", "Shows where wither doors are", Category.RENDER);
    }


}
