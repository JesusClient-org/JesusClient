package cum.jesus.jesusclient.module.modules.other;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;

public class SelfDestruct extends Module {
    public static BooleanSetting addLoadCommand = new BooleanSetting("Add reload command", true, true);

    public SelfDestruct() {
        super("Self Destruct", "Destroys and almost completely removes Jesus Client from your game", Category.OTHER);
    }

    @Override
    public void onEnable() {
        JesusClient.INSTANCE.unLoad();
    }
}
