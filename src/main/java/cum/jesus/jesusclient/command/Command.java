package cum.jesus.jesusclient.command;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.client.Minecraft;

public abstract class Command {
    protected final Minecraft mc = Minecraft.getMinecraft();

    private final String name;
    private final String help;
    private final int minArgs;
    private final int maxArgs;
    private final String[] alias;
    private String[] args;
    private boolean premiumOnly;

    public Command(String name, String help, int minArgs, int maxArgs, String[] alias, String[] args) {
        this.name = name;
        this.help = help;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.alias = alias;
        this.args = args;
    }

    public Command(String name, String help, int minArgs, int maxArgs, String[] args) {
        this(name, help, minArgs, maxArgs, args, new String[0]);
    }

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public int getMinArgs() {
        return minArgs;
    }

    public int getMaxArgs() {
        return maxArgs;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isPremiumOnly() {
        return premiumOnly;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public void onCall(String[] args) {}

    public void incorrectArgs() {
        JesusClient.sendPrefixMessage("Incorrect arguments.");
        JesusClient.sendPrefixMessage("Type `" + Gui.prefix.getObject() + "help " + getName() + "` for info on the command.");
    }

    public String[] getAliases() {
        return this.alias;
    }
}
