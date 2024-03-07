package cum.jesus.jesusclient.module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleRegistry {
    private List<Module> modules = new ArrayList<>();

    public List<Module> getModules() {
        return modules;
    }

    public void addDefaultModules() {

    }

    public void addPremiumModules() {

    }

    public void addDevModules() {

    }

    public void add(Module module) {
        modules.add(module);
    }
}
