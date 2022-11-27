package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.KeyInputEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.module.modules.combat.Cum;
import cum.jesus.jesusclient.module.modules.combat.KillAura;
import cum.jesus.jesusclient.module.modules.combat.Reach;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.module.modules.render.NoBlind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    @NotNull
    private static List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        EventManager.register(this);
    }

    public boolean addModules() {
        addModule(new Gui());
        addModule(new Cum());
        addModule(KillAura.INSTANCE);
        addModule(Reach.INSTANCE);
        addModule(NoBlind.INSTANCE);

        return true;
    }

    private void addModule(@NotNull Module module) {
        modules.add(module);
        EventManager.register(module);
        JesusClient.INSTANCE.settingManager.registerObject(module.getName(), module);
    }

    @NotNull
    public static List<Module> getModules() {
        return modules;
    }

    @NotNull
    public <T extends Module> T getModule(Class<T> klass) {
        return (T) modules.stream().filter(mod -> mod.getClass() == klass).findFirst().orElse(null);
    }

    public Module getModule(@NotNull String name, boolean caseSensitive) {
        return modules.stream().filter(mod -> !caseSensitive && name.equalsIgnoreCase(mod.getName()) || name.equals(mod.getName())).findFirst().orElse(null);
    }

    @EventTarget
    private void onKey(@NotNull KeyInputEvent event) {
        for (Module module : modules) if (module.getKeybind() == event.getKey()) module.setToggled(!module.isToggled());
    }
}
