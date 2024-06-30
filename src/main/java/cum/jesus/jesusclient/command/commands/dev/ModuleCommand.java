package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.annotations.Argument;
import cum.jesus.jesusclient.command.annotations.Command;
import cum.jesus.jesusclient.command.annotations.SubCommand;

@Command(value = "module", description = "Allows simple interaction with modules", aliases = { "mod" })
public final class ModuleCommand {
    @SubCommand(description = "Toggles a given module")
    private void toggle(@Argument(value = "Module name", description = "Name of the module to toggle") String moduleName) {

    }
}
