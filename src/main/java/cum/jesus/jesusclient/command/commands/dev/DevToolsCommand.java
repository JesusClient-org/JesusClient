package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DevToolsCommand extends Command {
    public DevToolsCommand() {
        super("devtools", "Allows the use of developer tools");
    }

    @Override
    public void run(String alias, String[] args) {
        if (!args[0].equalsIgnoreCase("toggle") && args.length != 2)
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + " toggle [<devtool>]");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        List<String> temp;
        if (arg == 1) {
            temp = Arrays.asList("toggle");
        } else {
            temp = new ArrayList<>();
        }

        return temp;
    }
}
