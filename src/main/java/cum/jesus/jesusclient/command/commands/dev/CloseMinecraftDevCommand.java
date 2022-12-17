package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.Command;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

public class CloseMinecraftDevCommand extends Command {
    public CloseMinecraftDevCommand() {
        super("closemc", "Closes minecraft");
    }

    @Override
    public void run(String alias, String[] args) {
        FMLCommonHandler.instance().exitJava(69, false);
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean isDevOnly() {
        return true;
    }
}
