package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.event.EventTarget;
import cum.jesus.jesusclient.event.events.videogame.KeyInputEvent;
import cum.jesus.jesusclient.event.events.videogame.MouseInputEvent;
import cum.jesus.jesusclient.setting.settings.KeybindSetting;
import cum.jesus.jesusclient.setting.Setting;
import cum.jesus.jesusclient.util.Logger;

public final class ModuleHandler {
    private final ModuleRegistry registry;

    public ModuleHandler(ModuleRegistry registry) {
        this.registry = registry;
    }

    public ModuleRegistry getRegistry() {
        return registry;
    }

    public void addModules() {
        registry.addDefaultModules();
    }

    public void addDevModules() {
        registry.addDevModules();
    }

    @EventTarget
    private void onKey(KeyInputEvent event) {

        for (Module module : registry.getModules()) {
            for (Setting setting : module.getSettings2()) {
                if (setting instanceof KeybindSetting && ((KeybindSetting) setting).getValue().getKey() == event.getKey()) {
                    Runnable func = ((KeybindSetting) setting).getValue().getOnPress();
                    if (func != null) {
                        func.run();
                    }
                }
            }
        }
    }

    @EventTarget
    private void onMouse(MouseInputEvent event) {
        for (Module module : registry.getModules()) {
            for (Setting setting : module.getSettings2()) {
                if (setting instanceof KeybindSetting && ((KeybindSetting) setting).getValue().getKey() + 100 == event.getButton()) {
                    Runnable func = ((KeybindSetting) setting).getValue().getOnPress();
                    if (func != null) func.run();
                }
            }
        }
    }
}
