package cum.jesus.jesusclient.module.modules;

import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.ModuleCategory;
import cum.jesus.jesusclient.setting.NumberSetting;
import cum.jesus.jesusclient.util.Logger;

public final class TestModule extends Module {
    private NumberSetting<Integer> intSetting = new NumberSetting<>("TestInt", 0, 0, 255);

    public TestModule() {
        super("Test", "Testing module", ModuleCategory.SELF);

        addSettings(
                intSetting
        );
    }

    @Override
    protected void onEnable() {
        Logger.debug("enabled");
    }

    @Override
    protected void onDisable() {
        Logger.debug("disabled");
    }
}
