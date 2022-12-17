package cum.jesus.jesusclient.module.modules.combat;

import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.NumberSetting;

public class Reach extends Module {
    public static Reach INSTANCE = new Reach();
    public static NumberSetting<Float> reachAmount = new NumberSetting<>("Reach amount", 3.0f, 2.0f, 4.5f);

    public Reach() {
        super("Reach", "Hits entities from further away", Category.COMBAT);
    }
}
