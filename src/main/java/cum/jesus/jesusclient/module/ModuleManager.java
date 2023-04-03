package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.KeyInputEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.module.modules.dungeons.CancelChestOpen;
import cum.jesus.jesusclient.module.modules.self.AntiKB;
import cum.jesus.jesusclient.module.modules.skyblock.Cum;
import cum.jesus.jesusclient.module.modules.self.Reach;
import cum.jesus.jesusclient.module.modules.movement.BHop;
import cum.jesus.jesusclient.module.modules.movement.Flight;
import cum.jesus.jesusclient.module.modules.other.SelfDestruct;
import cum.jesus.jesusclient.module.modules.render.*;
import cum.jesus.jesusclient.module.modules.self.SessionProtection;
import cum.jesus.jesusclient.module.modules.self.Timer;
import cum.jesus.jesusclient.module.modules.dungeons.AutoReady;
import cum.jesus.jesusclient.module.modules.dungeons.TerminalSolver;
import cum.jesus.jesusclient.remote.Premium;
import cum.jesus.jesusclient.scripting.ScriptModule;
import cum.jesus.jesusclient.utils.Logger;
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
        try {
            // skyblock
            addModule(new Cum());

            // dungeons
            addModule(new AutoReady());
            addModule(new TerminalSolver());
            addModule(new CancelChestOpen());

            // movement
            addModule(new Flight());
            addModule(new BHop());

            // render
            addModule(NoBlind.INSTANCE);
            addModule(NoSlimes.INSTANCE);
            addModule(new RevealHiddenMobs());
            addModule(Console.INSTANCE);
            addModule(PlayerScale.INSTANCE);

            // self
            addModule(new Timer());
            addModule(new SessionProtection());
            addModule(Reach.INSTANCE);
            addModule(AntiKB.INSTANCE);

            // other
            addModule(new SelfDestruct());

            // hud shit (has to be added last for modulelist reasons)
            addModule(new Gui());
            addModule(new Hud());
        } catch (Exception e) {
            Logger.error("Error while loading module manager: " + e.getMessage() + "\n");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void addModule(@NotNull Module module) {
        modules.add(module);
        EventManager.register(module);
        JesusClient.INSTANCE.settingManager.registerObject(module.getName(), module);
    }

    @NotNull
    public List<Module> getModules() {
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
        for (Module module : modules) {
            if (module.getKeybind() == event.getKey()) {
                if (module.isPremiumFeature() && !Premium.isUserPremium()) {
                    continue;
                }

                module.toggle();
            }
        }
    }

    public void addScriptModule(ScriptModule module) {
        modules.add(module);
        EventManager.register(module);
    }

    public void removeScriptModules() {
        modules.clear();
        addModules();
    }
}
