package cum.jesus.jesusclient.gui.externalconsole.cmd;

import cum.jesus.jesusclient.gui.externalconsole.Cmd;
import cum.jesus.jesusclient.gui.externalconsole.Console;

public class Echo extends Cmd {
    public Echo() {
        super("echo", "Prints a message to the output", "echo <message>");
    }

    @Override
    public void run(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        Console.INSTANCE.println(sb.toString(), false);
    }
}
