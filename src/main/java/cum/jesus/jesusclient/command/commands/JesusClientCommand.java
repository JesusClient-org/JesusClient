package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.command.annotations.Command;
import cum.jesus.jesusclient.command.annotations.Entry;
import cum.jesus.jesusclient.gui.clickgui.ClickGUI;
import cum.jesus.jesusclient.util.Utils;

@Command(value = "jesusclient", description = "Opens the config gui", aliases = { "jesus", "config" })
public final class JesusClientCommand {
    @Entry
    private void entry() {
        ClickGUI.INSTANCE.enterGUI();
    }
}
