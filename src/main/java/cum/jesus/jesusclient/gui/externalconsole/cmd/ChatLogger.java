package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.gui.externalconsole.Console;

import java.awt.*;

public class ChatLogger extends Cmd {
    public ChatLogger() {
        super("chatlogger", "Will log all Minecraft chat output to the console", "chatlogger <[enable/disable]>");
    }

    @Override
    public void run(String[] args) {
        if (args.length == 0) {
            cum.jesus.jesusclient.module.modules.render.Console.shouldChatLog.setObject(!cum.jesus.jesusclient.module.modules.render.Console.shouldChatLog.getObject());
            Console.INSTANCE.println((cum.jesus.jesusclient.module.modules.render.Console.shouldChatLog.getObject() ? "Enabled" : "Disabled") + " chat logging", false);
        } else {
            if (args[0].equalsIgnoreCase("enable")) {
                cum.jesus.jesusclient.module.modules.render.Console.shouldChatLog.setObject(true);
                Console.INSTANCE.println("Enabled chat logging", false);
            } else if (args[0].equalsIgnoreCase("disable")) {
                cum.jesus.jesusclient.module.modules.render.Console.shouldChatLog.setObject(false);
                Console.INSTANCE.println("Disabled chat logging", false);
            } else {
                Console.INSTANCE.println("Usage: " + getUsage(), false, new Color(255, 85, 85));
            }
        }
    }
}
