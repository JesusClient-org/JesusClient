package cum.jesus.jesusclient.command;

import java.util.List;

public abstract class Command {
    private String name;
    private String description;
    /**
     * Used as usage.replace("{name}", nameOrAlias).replace("{prefix}", commandPrefix)
     */
    private String usage;
    private String[] aliases;
    private int minArgs;
    /**
     * A maxArgs value of -1 means unlimited
     */
    private int maxArgs;

    protected Command(final String name, final String description, final String usage, final String[] aliases, final int minArgs, final int maxArgs) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    /**
     * Initializes a Command with no arguments
     */
    protected Command(final String name, final String description, final String usage, final String... aliases) {
        this(name, description, usage, aliases, 0, 0);
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getUsage(String nameOrAlias, String commandPrefix) {
        return usage.replace("{name}", nameOrAlias).replace("{prefix}", commandPrefix);
    }

    public final String[] getAliases() {
        return aliases;
    }

    public final int getMinArgs() {
        return minArgs;
    }

    public final int getMaxArgs() {
        return maxArgs;
    }

    /**
     * Runs the command
     * @param correctUsage The correct usage string where the variables are replaced
     * @param args Command arguments
     * @throws CommandException If something goes wrong this is handled gracefully
     */
    public abstract void run(final String correctUsage, final String[] args) throws CommandException;

    public abstract List<String> autoComplete(int argIndex, String[] args);

    public boolean match(final String name) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(name))
                return true;
        }
        return this.name.equalsIgnoreCase(name);
    }
}
