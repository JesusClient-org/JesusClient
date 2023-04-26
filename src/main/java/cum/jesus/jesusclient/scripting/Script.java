package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.module.Module;

import java.util.ArrayList;
import java.util.List;

public class Script {
    private String name;
    private String description;
    private String version;
    private String[] authors;
    private String[] dependencies;
    private List<Module> modules = new ArrayList<>();
    private List<Command> commands = new ArrayList<>();
    private ScriptIndex index;

    public Script(String name, String description, String version, String[] authors, String[] dependencies, ScriptIndex index) {
        this.name = name;
        this.description = description;
        this.version = version;
        this.authors = authors;
        this.index = index;
        this.dependencies = dependencies;
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

    public List<Module> getModules() {
        return modules;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public ScriptIndex getIndex() {
        return index;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void register() {
        for (Module module : modules) {
            if (module instanceof ScriptModule2) {
                ((ScriptModule2) module).setScriptName(name);
                JesusClient.INSTANCE.moduleManager.addScriptModule((ScriptModule2) module);
                ((ScriptModule2) module).doSettings();
            } else if (module instanceof ScriptModule) {
                JesusClient.INSTANCE.moduleManager.addScriptModule((ScriptModule) module);
            }
        }
        for (Command command : commands) {
            command.setDescription("(From script: " + name + ") " + command.getDescription());
            command.setPremiumOnly(true);
            if (command instanceof ScriptCommand) {
                JesusClient.INSTANCE.commandManager.addScriptCommand((ScriptCommand) command);
            } else {
                JesusClient.INSTANCE.commandManager.addCommand(command);
            }
        }
    }

    public void purge() {
        EventManager.unregister(index);
        index = null;

        for (Module module : modules) {
            if (module instanceof ScriptModule2) {
                JesusClient.INSTANCE.moduleManager.removeScriptModule((ScriptModule2) module);
            } else if (module instanceof ScriptModule) {
                JesusClient.INSTANCE.moduleManager.removeScriptModule((ScriptModule) module);
            }
        }
        for (Command command : commands) {
            if (command instanceof ScriptCommand) {
                JesusClient.INSTANCE.commandManager.removeScriptCommand((ScriptCommand) command);
            } else {
                JesusClient.INSTANCE.commandManager.removeCommand(command);
            }
        }

        modules.clear();
        commands.clear();
    }
}
