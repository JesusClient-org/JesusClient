package cum.jesus.jesusclient.command;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Gui;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    protected final Minecraft mc = Minecraft.getMinecraft();

    private String name;
    private String description;
    private String[] aliases;
    private boolean premiumOnly = false;
    private boolean devOnly;

    protected Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public abstract void run(String alias, String[] args);

    public abstract List<String> autoComplete(int arg, String[] args);

    boolean matchCmdName(String name) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(name)) return true;
        }
        return this.name.equalsIgnoreCase(name);
    }

    @NotNull List<String> getNameAndAliases() {
        List<String> tempList = new ArrayList<>();
        tempList.add(name);
        tempList.addAll(Arrays.asList(aliases));
        return tempList;
    }

    public boolean isPremiumOnly() {
        return premiumOnly;
    }

    public void setPremiumOnly(boolean premiumOnly) {
        this.premiumOnly = premiumOnly;
    }

    public boolean isDevOnly() {
        return false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
    * to change description of one file script commands
    */
    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return name + " command";
    }
}
