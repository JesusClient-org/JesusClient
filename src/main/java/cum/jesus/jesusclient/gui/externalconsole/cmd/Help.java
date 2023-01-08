package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.gui.externalconsole.Console;

import java.awt.*;
import java.util.List;

public class Help extends Cmd {
    public Help() {
        super("help", "Shows all commands and what they do", "help <[commandName]>");
    }

    @Override
    public void run(String[] args) {
        List<Cmd> cmdList = Console.INSTANCE.getCommandList();

        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();
            for (Cmd cmd : cmdList) {
                sb.append(cmd.getName()).append(" - ").append(cmd.getHelp()).append("\n");
            }

            Console.INSTANCE.println(sb.toString(), false);
        } else {
            Cmd cmd = Console.INSTANCE.getCommandByName(args[0].toLowerCase());

            if (cmd == null) {
                Console.INSTANCE.println("Command '" + args[0] + "' wasn't found", false, new Color(255, 85, 85));
                return;
            }

            Console.INSTANCE.println(
                    "Help - " + cmd.getName() + "\n" +
                    "Description: " + cmd.getHelp() + "\n" +
                    "Usage: " + cmd.getUsage(),
                    false
            );
        }
    }
}
