package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.JesusClient;

import java.util.ArrayList;
import java.util.List;

public class Script {
    private String name;
    private String description;
    private String version;
    private String[] authors;
    private List<ScriptModule> modules = new ArrayList<>();
    private List<ScriptCommand> commands = new ArrayList<>();
    private ScriptIndex index;

    public Script(String name, String description, String version, String[] authors, ScriptIndex index) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.authors = authors;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String[] getAuthors() {
        return authors;
    }

    public List<ScriptModule> getModules() {
        return modules;
    }

    public List<ScriptCommand> getCommands() {
        return commands;
    }

    public ScriptIndex getIndex() {
        return index;
    }

    public void register() {
        for (ScriptModule module : modules) {
            JesusClient.INSTANCE.moduleManager.addScriptModule(module);
        }
        for (ScriptCommand command : commands) {
            JesusClient.INSTANCE.commandManager.addScriptCommand(command);
        }
    }
}
