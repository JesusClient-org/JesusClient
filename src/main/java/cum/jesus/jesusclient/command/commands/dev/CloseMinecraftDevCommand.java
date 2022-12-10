package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.Command;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CloseMinecraftDevCommand extends Command {
    public CloseMinecraftDevCommand() {
        super("closemc", "Closes minecraft", 0, 0, new String[0]);
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }

    @Override
    public void onCall(String[] args) {
        FMLCommonHandler.instance().exitJava(69, false);
    }
}
