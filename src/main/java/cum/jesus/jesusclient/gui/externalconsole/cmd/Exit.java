package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.modules.render.Console;

public class Exit extends Cmd {
    public Exit() {
        super("exit", "Closes the console", "exit");
    }

    @Override
    public void run(String[] args) {
        Module consoleObj = JesusClient.INSTANCE.moduleManager.getModule(Console.class);
        consoleObj.setToggled(false);
    }
}
