package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.ChatUtils;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScriptDevCommand extends Command {
    public ScriptDevCommand() {
        super("scripting", "Scripting stuff");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1)
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "<eval/new> [<codeToEval>]");

        if (args.length >= 2 && args[0].equalsIgnoreCase("eval")) {
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]);

                    if (i != args.length - 1) {
                        sb.append(' ');
                    }
                }

                ChatUtils.sendPrefixMessage(JesusClient.INSTANCE.scriptManager.eval(sb.toString()));
            } catch (ScriptException e) {
                throw new CommandException(e.getMessage(), e);
            }
        }
        if (args[0].equalsIgnoreCase("new")) {
            JesusClient.INSTANCE.scriptManager.newScript();
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        if (arg == 1) return Arrays.asList("eval", "new");
        else return new ArrayList<>();
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }

    @Override
    public boolean isPremiumOnly() {
        return true;
    }
}
