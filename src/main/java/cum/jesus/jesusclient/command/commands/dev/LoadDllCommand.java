package cum.jesus.jesusclient.command.commands.dev;

import cum.jesus.jesusclient.command.Command;
import cum.jesus.jesusclient.command.CommandException;

import java.util.ArrayList;
import java.util.List;

public class LoadDllCommand extends Command {
    public LoadDllCommand() {
        super("loaddll", "Loads a dll from the client root dir with the given name (can have spaces if needed)");
    }

    @Override
    public void run(String alias, String[] args) {
        if (args.length < 1) {
            throw new CommandException("Usage: ");
        }
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
