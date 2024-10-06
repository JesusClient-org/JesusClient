package cum.jesus.jesusclient.module;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.event.EventManager;
import cum.jesus.jesusclient.module.modules.render.ClickGUIModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleRegistry {
    private List<Module> modules = new ArrayList<>();
    private Map<ModuleCategory, List<Module>> categories = new HashMap<>();

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModules(ModuleCategory category) {
        return categories.computeIfAbsent(category, k -> new ArrayList<>());
    }

    public void addDefaultModules() {
        add(ClickGUIModule.INSTANCE);
    }

    public void addDevModules() {
    }

    public void add(Module module) {
        modules.add(module);
        categories.computeIfAbsent(module.getCategory(), k -> new ArrayList<>()).add(module);

        JesusClient.instance.configManager.register(module);
    }

    @SuppressWarnings("unchecked")
    public <T extends Module> T getModule(Class<T> klass) {
        return (T) modules.stream().filter(mod -> mod.getClass() == klass).findFirst().orElse(null);
    }

    public Module getModule(String name) {
        return modules.stream().filter(mod -> name.equalsIgnoreCase(mod.getName())).findFirst().orElse(null);
    }
}
