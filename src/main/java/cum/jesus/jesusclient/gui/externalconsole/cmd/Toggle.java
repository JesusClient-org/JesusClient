package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.gui.externalconsole.Console;
import cum.jesus.jesusclient.module.Module;

import java.awt.*;

public class Toggle extends Cmd {
    public Toggle() {
        super("toggle", "Toggles a specified module in Jesus Client (not case sensitive)", "toggle <moduleName>");
    }

    @Override
    public void run(String[] args) {
        if (args.length != 1) {
            Console.INSTANCE.println("Usage" + getUsage(), true, new Color(255, 85, 85));
        } else {
            Module tmp = JesusClient.INSTANCE.moduleManager.getModule(args[0], false);

            if (tmp != null) {
                tmp.toggle();

                Console.INSTANCE.println("Toggled " + tmp.getName(), false);
            }
        }
    }
}
