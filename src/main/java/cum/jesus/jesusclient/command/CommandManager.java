package cum.jesus.jesusclient.command;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.commands.dev.DevToolsCommand;
import cum.jesus.jesusclient.command.commands.premium.ReloadScriptsCommand;
import cum.jesus.jesusclient.command.commands.premium.SpamWebhookCommand;
import cum.jesus.jesusclient.remote.Premium;
import cum.jesus.jesusclient.command.commands.*;
import cum.jesus.jesusclient.command.commands.dev.CloseMinecraftDevCommand;
import cum.jesus.jesusclient.command.commands.dev.HttpDevCommand;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.scripting.ScriptCommand;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {
    @NotNull
    public List<Command> commandList = new ArrayList<>();

    public boolean addCommands() {
        try {
            // dev cmd
            addCommand(new CloseMinecraftDevCommand());
            addCommand(new HttpDevCommand());
            addCommand(new DevToolsCommand());

            // premium cmd
            addCommand(new SpamWebhookCommand());
            addCommand(new ReloadScriptsCommand());

            addCommand(new DiscordCommand());
            addCommand(new HelpCommand());
            addCommand(new JesusCommand());
            addCommand(new VClipCommand());
        } catch (Exception e) {
            Logger.error("Error while loading command manager: " + e.getMessage() + "\n");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void addCommand(Command c) {
        if (c.isDevOnly() && !JesusClient.devMode) return;
        commandList.add(c);
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

    public boolean execute(@NotNull String string) {
        String rawSex = string.substring(Gui.prefix.getObject().length());
        String[] splittedStr = rawSex.split(" ");

        if (splittedStr.length == 0) return false; // there is no command

        String cmdName = splittedStr[0];

        Command command = commandList.stream().filter(c -> c.matchCmdName(cmdName)).findFirst().orElse(null);

        try {
            if (command == null) { // cmd does not exist
                ChatUtils.sendPrefixMessage(cmdName + " is not a command. Run " + Gui.prefix.getObject() + "help for a list of the commands");
                return false;
            } else {
                String[] args = new String[splittedStr.length - 1];

                System.arraycopy(splittedStr, 1, args, 0, splittedStr.length - 1);

                if (command.isPremiumOnly() && !Premium.isUserPremium()) { // non premium user running premiu command
                    ChatUtils.sendPrefixMessage("This command is only available to Jesus Client premium users");
                    return false;
                }

                command.run(splittedStr[0], args);

                return true; // the command is successfully run and also exists
            }
        } catch (CommandException e) {
            ChatUtils.sendPrefixMessage("§c" + e.getMessage());
        }

        return true;
    }

    public Collection<String> autoCompletion(@NotNull String currentCmd) {
        String raw = currentCmd.substring(Gui.prefix.getObject().length());
        String[] split = raw.split(" ");

        List<String> womanRights = new ArrayList<>();

        Command currCmd = split.length >= 1 ? commandList.stream().filter(c -> c.matchCmdName(split[0])).findFirst().orElse(null) : null;

        if (split.length >= 2 || currCmd != null && currentCmd.endsWith(" ")) {
            if (currCmd == null) return womanRights;

            String[] args = new String[split.length - 1];

            System.arraycopy(split, 1, args, 0, split.length - 1);

            List<String> autoCompleted = currCmd.autoComplete(args.length + (currentCmd.endsWith(" ") ? 1: 0), args);

            return autoCompleted == null ? new ArrayList<>() : autoCompleted;
        } else if (split.length == 1) {
            for (Command c : commandList) womanRights.addAll(c.getNameAndAliases());

            return womanRights.stream().map(str -> Gui.prefix.getObject() + str).filter(str -> str.toLowerCase().startsWith(currentCmd.toLowerCase())).collect(Collectors.toList());
        }

        return womanRights;
    }

    public void addScriptCommand(ScriptCommand command) {
        addCommand(command);
    }

    public void removeScriptCommands() {
        commandList.clear();
        addCommands();
    }
}
