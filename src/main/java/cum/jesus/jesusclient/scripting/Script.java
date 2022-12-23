package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.JesusClient;

import java.util.ArrayList;
import java.util.List;

public class Script {
    private String name;
    private String version;
    private List<ScriptModule> modules = new ArrayList<>();

    public Script(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public List<ScriptModule> getModules() {
        return modules;
    }

    public void register() {
        for (ScriptModule module : modules) {
            JesusClient.INSTANCE.moduleManager.addScriptModule(module);
        }
    }
}
