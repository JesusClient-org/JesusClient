package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.modules.render.Gui;

public class JesusCommand extends Command {
    public JesusCommand() {
        super("jesus", "Brings up the config gui", 0, 0, new String[0]);
    }

    Module gui = JesusClient.INSTANCE.moduleManager.getModule(Gui.class);

    @Override
    public void onCall(String[] args) {
        gui.setToggled(true);
    }
}
