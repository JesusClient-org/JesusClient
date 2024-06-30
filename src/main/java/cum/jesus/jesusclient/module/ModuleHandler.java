package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.event.EventTarget;
import cum.jesus.jesusclient.event.events.videogame.KeyInputEvent;

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

    public <T extends Module> T getModule(Class<T> klass) {
        return (T) registry.getModules().stream().filter(mod -> mod.getClass() == klass).findFirst().orElse(null);
    }

    public Module getModule(String name) {
        return registry.getModules().stream().filter(mod -> name.equalsIgnoreCase(mod.getName())).findFirst().orElse(null);
    }

    /**
     * For config manager
     */
    public Module getModuleNoSpace(String name) {
        return registry.getModules().stream().filter(mod -> name.equalsIgnoreCase(mod.getName().replace(" ", ""))).findFirst().orElse(null);
    }

    @EventTarget
    private void onKey(KeyInputEvent event) {
        for (Module module : registry.getModules()) {
            if (module.getKeybind() == event.getKey()) {
                module.handleKeybind();
            }
        }
    }
}
