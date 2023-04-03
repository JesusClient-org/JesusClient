package cum.jesus.jesusclient.command.commands.premium;

import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;
import cum.jesus.jesusclient.utils.ClipUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HClipCommand extends Command {
    public HClipCommand() {
        super("hclip", "Clips you horizontally");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length != 1) {
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "[<distance>]");
        }

        double dist = Double.parseDouble(args[0]);

        ClipUtils.hClip(dist);
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
