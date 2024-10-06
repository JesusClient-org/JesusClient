package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.command.annotations.Argument;
import cum.jesus.jesusclient.command.annotations.Command;
import cum.jesus.jesusclient.command.annotations.SubCommand;
import cum.jesus.jesusclient.command.annotations.Variadic;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.util.ChatUtils;

@Command(value = "module", description = "Allows simple interaction with modules", aliases = { "mod" })
public final class ModuleCommand {
    @SubCommand(description = "Toggles a given module")
    private void toggle(@Argument(value = "Module name", description = "Name of the module to toggle") @Variadic String moduleName) {
        Module module = JesusClient.instance.moduleHandler.getRegistry().getModule(moduleName);

        if (module == null) {
            throw new CommandException("Module named '" + moduleName + "' wasn't found");
        }

        module.toggle();
        ChatUtils.sendPrefixMessage("Toggle");
    }
}
