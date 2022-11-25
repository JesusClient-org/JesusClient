package cum.jesus.jesusclient.command.commands;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.command.Command;
import net.minecraft.util.MathHelper;

public class VClipCommand extends Command {
    public VClipCommand() {
        super("vclip", "Teleports you an amount on the Y coordinate", 1, 1, new String[] { "Amount of blocks to tp" }, new String[] { "clip" });
    }

    public void onCall(String[] args) {
        if (args == null) {
            incorrectArgs();
            return;
        }
        JesusClient.mc.thePlayer.setPosition(MathHelper.floor_double(JesusClient.mc.thePlayer.posX) + 0.5D, JesusClient.mc.thePlayer.posY + Double.parseDouble(args[1]), MathHelper.floor_double(JesusClient.mc.thePlayer.posZ) + 0.5D);
    }
}
