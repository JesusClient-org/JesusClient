package cum.jesus.jesusclient.module;

public final class ModuleHandler {
    private final ModuleRegistry registry;

    public ModuleHandler(ModuleRegistry registry) {
        this.registry = registry;
    }

    public Module getModule(String name) {
        return registry.getModules().stream().filter(mod -> name.equalsIgnoreCase(mod.getName())).findFirst().orElse(null);
    }
}
