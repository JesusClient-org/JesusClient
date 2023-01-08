package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.gui.externalconsole.Console;
import cum.jesus.jesusclient.gui.externalconsole.NiggerException;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.remote.Premium;
import cum.jesus.jesusclient.utils.ChatUtils;
import cum.jesus.jesusclient.utils.PacketShit;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;

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
                if (tmp.isPremiumFeature() && !Premium.isUserPremium()) {
                    for (int i = 0; i < 100; i++) {
                        CrashReport dummy = CrashReport.makeCrashReport(new NiggerException("Use of premium feature without premium"), "Use of premium feature without premium");
                        JesusClient.mc.displayCrashReport(dummy);
                    }

                    ChatUtils.sendPrefixMessage(null);
                    return;
                }

                tmp.toggle();
                Console.INSTANCE.println("Toggled " + tmp.getName(), false);
            } else {
                Console.INSTANCE.println("Module '" + args[0] + "' couldn't be found", false, new Color(255, 85, 85));
            }
        }
    }
}
