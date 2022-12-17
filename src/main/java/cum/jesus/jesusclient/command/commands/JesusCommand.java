package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.gui.clickgui.ClickGui;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.modules.render.Gui;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JesusCommand extends Command {
    public JesusCommand() {
        super("jesus", "Brings up the config gui", "jesusclient");
    }

    @Override
    public void run(String alias, @NotNull String[] args) {
        Module mod = JesusClient.INSTANCE.moduleManager.getModule(Gui.class);
        mod.toggle();
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
