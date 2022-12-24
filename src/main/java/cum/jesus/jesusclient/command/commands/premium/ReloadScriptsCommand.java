package cum.jesus.jesusclient.command.commands.premium;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.scripting.ScriptCommand;
import cum.jesus.jesusclient.scripting.ScriptModule;

import java.util.ArrayList;
import java.util.List;

public class ReloadScriptsCommand extends Command {
    public ReloadScriptsCommand() {
        super("reloadscripts", "Reloads scripts");
    }

    @Override
    public void run(String alias, String[] args) {
        JesusClient.INSTANCE.commandManager.removeScriptCommands();
        JesusClient.INSTANCE.moduleManager.removeScriptModules();

        JesusClient.INSTANCE.fileManager.loadScripts();
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
