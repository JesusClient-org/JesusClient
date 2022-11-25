package cum.jesus.jesusclient.command;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.Premium;
import cum.jesus.jesusclient.command.commands.DiscordCommand;
import cum.jesus.jesusclient.command.commands.HelpCommand;
import cum.jesus.jesusclient.command.commands.JesusCommand;
import cum.jesus.jesusclient.command.commands.VClipCommand;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.module.modules.render.Gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CommandManager {
    public List<Command> commandList = new ArrayList<>();
    public List<Command> sortedCommandList = new ArrayList<>();

    public boolean addCommands() {
        addCommand(new HelpCommand());
        addCommand(new JesusCommand());
        addCommand(new DiscordCommand());
        addCommand(new VClipCommand());

        return true;
    }

    public void addCommand(Command c) {
        this.commandList.add(c);
        EventManager.register(c);
    }

    public List<Command> getCommandList() {
        return this.commandList;
    }

    public Command getCommandByName(String name) {
        for (Command command : this.commandList) {
            if (command.getName().equalsIgnoreCase(name))
                return command;
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(name))
                    return command;
            }
        }
        return null;
    }

    public void noSuchCommand(String name) {
        JesusClient.sendPrefixMessage("Couldn't find the command you were using. Type `" + Gui.prefix.getObject() + "help` for a list of commands.");
    }

    public void executeCommand(String commandName, String[] args) {
        Command command = JesusClient.INSTANCE.commandManager.getCommandByName(commandName);
        if (command == null) {
            noSuchCommand(commandName);
            return;
        }

        if (command.isPremiumOnly() && !Premium.isUserPremium()) {
            JesusClient.sendPrefixMessage("This command is only avaliable to Jesus Client premium users");
            return;
        }

        command.onCall(args);
    }

    public void sort() {
        this.sortedCommandList.sort(Comparator.comparing(Command::getName));
    }
}
