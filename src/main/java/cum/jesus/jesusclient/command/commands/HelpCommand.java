package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.module.modules.render.Gui;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "This message");
    }

    @Override
    public void run(String alias, String[] args) {

    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();

    }

    /*@Override
    public void onCall(String[] args) {
        if (args == null) {
            JesusClient.INSTANCE.commandManager.sort();
            JesusClient.sendPrefixMessage("Available commands:");
            for (Command command : JesusClient.INSTANCE.commandManager.getCommandList()) {
                if (command.getName().equalsIgnoreCase("help"))
                    continue;
                JesusClient.sendMessage("\u00A7" + "d - " + command.getName());
            }
            JesusClient.sendMessage("\u00A7" + "aRun `" + Gui.prefix.getObject() + "help commandname` for more info about a command.");
        } else if (args.length == 2) {
            Command command = JesusClient.INSTANCE.commandManager.getCommandByName(args[1]);
            if (command == null) {
                JesusClient.sendPrefixMessage("Unable to find the command you were looking for.");
                return;
            }

            JesusClient.sendPrefixMessage(command.getName() + " info:");
            if (command.getAliases() != null || (command.getAliases()).length != 0) {
                JesusClient.sendMessage("\u00A7" + "aCommand Aliases:");
                for (String alias : command.getAliases())
                    JesusClient.sendMessage("\u00A7" + "a- " + alias.substring(0, 1).toUpperCase() + alias.substring(1));
            }
            if (!command.getHelp().isEmpty()) {
                JesusClient.sendMessage("\n" + "\u00A7" + "aCommand description:");
                for (String helpText : command.getHelp().split("<br>"))
                    JesusClient.sendMessage("\u00A7" + "a" + helpText);
            }
            if (command.getArgs() != null) {
                JesusClient.sendMessage("\n" + "\u00A7" + "aCommand argument description:");
                JesusClient.sendMessage("\u00A7" + "aMinimum expected arguments: " + command.getMinArgs() + "\n" + "\u00A7" + "aMaximum expected arguments " + command.getMaxArgs());
                int argIndex = 1;
                for (String argText : command.getArgs()) {
                    for (String line : argText.split("<br>")) {
                        JesusClient.sendMessage("\u00A7" + "a" + argIndex + ": " + line);
                    }
                    argIndex++;
                }
            }
        }
    }*/
}
