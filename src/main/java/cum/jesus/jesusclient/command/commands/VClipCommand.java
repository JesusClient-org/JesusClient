package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class VClipCommand extends Command {
    public VClipCommand() {
        super("vclip", "Teleports you an amount on the Y coordinate);");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length != 1) {
            throw new CommandException("Usage: " + Gui.prefix.getObject() + alias + "[<blocks to clip>]");
        }

        JesusClient.mc.thePlayer.setPosition(MathHelper.floor_double(JesusClient.mc.thePlayer.posX) + 0.5D, JesusClient.mc.thePlayer.posY + Double.parseDouble(args[0]), MathHelper.floor_double(JesusClient.mc.thePlayer.posZ) + 0.5D);
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }
}
