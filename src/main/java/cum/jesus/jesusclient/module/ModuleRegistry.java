package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.event.EventManager;

import java.util.ArrayList;
import java.util.List;

public final class ModuleRegistry {
    private List<Module> modules = new ArrayList<>();

    public List<Module> getModules() {
        return modules;
    }

    public void addDefaultModules() {
    }

    public void addDevModules() {
    }

    public void add(Module module) {
        modules.add(module);
        EventManager.register(module);
        JesusClient.instance.configManager.register(module);
    }
}
