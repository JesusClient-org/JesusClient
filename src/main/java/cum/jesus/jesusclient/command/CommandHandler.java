package cum.jesus.jesusclient.command;

import org.jetbrains.annotations.NotNull;

public final class CommandHandler {
    private final CommandRegistry registry;

    public CommandHandler(CommandRegistry registry) {
        this.registry = registry;
    }

    public void addCommands() {

    }

    public boolean execute(@NotNull String string) {
        String raw = string.substring(1);
        String[] split = raw.split(" ");
        return false;
    }
}
