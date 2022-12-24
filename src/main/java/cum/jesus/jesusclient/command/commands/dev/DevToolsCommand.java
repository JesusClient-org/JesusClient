package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.Utils;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class DevToolsCommand extends Command {
    public DevToolsCommand() {
        super("devtools", "Allows the use of developer tools");
    }

    public static boolean forceDungeon = false;
    public static boolean forceSkyblock = false;

    @Override
    public void run(String alias, String[] args) {
        if (!args[0].equalsIgnoreCase("force") || args.length != 3)
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "force [<devtool>] <true/false>");

        switch (args[1].toLowerCase()) {
            case "dungeon":
                forceDungeon = args[2].equalsIgnoreCase("true");
                ChatUtils.sendPrefixMessage("Forced DUNGEON to be " + forceDungeon);
                break;
            case "skyblock":
                forceSkyblock = args[2].equalsIgnoreCase("true");
                ChatUtils.sendPrefixMessage("Forced SKYBLOCK to be " + forceSkyblock);
                break;
            case "hypixel":
                Utils.onHypixel = args[2].equalsIgnoreCase("true");
                ChatUtils.sendPrefixMessage("Forced HYPIXEL to be " + Utils.onHypixel);
                break;
            case "all":
                Utils.onHypixel = args[2].equalsIgnoreCase("true");
                forceSkyblock = args[2].equalsIgnoreCase("true");
                forceDungeon = args[2].equalsIgnoreCase("true");
                ChatUtils.sendPrefixMessage("Forced all checks to be " + args[2]);
                break;
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        List<String> temp;
        if (arg == 1) {
            temp = Arrays.asList("force");
        } else {
            temp = new ArrayList<>();
        }

        return temp;
    }
}
