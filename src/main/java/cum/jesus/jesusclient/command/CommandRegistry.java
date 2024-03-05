package cum.jesus.jesusclient.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class CommandRegistry {
    @NotNull
    private List<Command> commands = new ArrayList<>();

    public void addDefaultCommands() {

    }

    public void addPremiumCommands() {

    }

    public void addDevCommands() {

    }

    public void add(Command cmd) {
        commands.add(cmd);
    }
}
