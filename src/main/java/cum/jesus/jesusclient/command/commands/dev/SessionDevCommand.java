package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.injection.interfaces.IMixinMinecraft;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.ChatUtils;
import net.minecraft.util.Session;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SessionDevCommand extends Command {
    public SessionDevCommand() {
        super("session", "Gets or sets your current session. Useful for testing server things");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "<get/getAsCliArgs/set> [<session>]");

        if (args[0].equalsIgnoreCase("get")) {
            Session session = ((IMixinMinecraft)mc).getSession();

            String text = session.getUsername() + "/" + session.getPlayerID() + "/" + session.getToken();

            try {
                StringSelection stringSelection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                ChatUtils.sendPrefixMessage("Copied your current session to clipboard!");
            } catch (Exception e) {
                throw new CommandException("Failed to copy session to clipboard: " + e);
            }
        } else if (args[0].equalsIgnoreCase("getAsCliArgs")) {
            Session session = ((IMixinMinecraft)mc).getSession();

            String text = "--username " + session.getUsername()  + " --uuid " + session.getPlayerID() + " --accessToken " + session.getToken();

            try {
                StringSelection stringSelection = new StringSelection(text);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                ChatUtils.sendPrefixMessage("Copied your current session to clipboard!");
            } catch (Exception e) {
                throw new CommandException("Failed to copy session to clipboard: " + e);
            }
        } else if (args[0].equalsIgnoreCase("set")) {
            if (args.length != 2)
                throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + " set <session>.\nSession format: username/uuid/token");

            String[] split = args[1].split("/");

            if (split.length != 3)
                throw new CommandException("Invalid session format. (username/uuid/token)");

            ((IMixinMinecraft)mc).setSession(new Session(split[0], split[1], split[2], "mojang"));
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        if (arg == 1) return Arrays.asList("get", "getAsCliArgs", "set");
        else return new ArrayList<>();
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }
}
