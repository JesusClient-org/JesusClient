package cum.jesus.jesusclient.gui.externalconsole;

public abstract class Cmd {
    private String name;
    private String help;
    private String usage;

    public Cmd(String name, String help, String usage) {
        this.name = name;
        this.help = help;
        this.usage = usage;
    }

    public String getName() {
        return name;
    }

    public String getHelp() {
        return help;
    }

    public String getUsage() {
        return usage;
    }

    public abstract void run(String[] args);
}
