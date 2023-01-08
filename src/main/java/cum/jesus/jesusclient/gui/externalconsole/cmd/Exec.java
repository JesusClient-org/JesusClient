package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.gui.externalconsole.Console;

import java.awt.*;

public class Exec extends Cmd {
    public Exec() {
        super("exec", "Executes a command as if it was run in Minecraft", "exec <name/alias> <[args]>");
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            Console.INSTANCE.println("Usage: " + getUsage(), false, new Color(255, 85, 85));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        if (JesusClient.INSTANCE.commandManager.execute(sb.toString())) {
            Console.INSTANCE.println("Successfully ran the command '" + args[0] + "'. To be able to see the Minecraft chat output in console, run the 'chatlogger' command", false);
        } else {
            Console.INSTANCE.println("Failed to run '" + args[0] + "'", false, new Color(255, 85, 85));
        }
    }
}
