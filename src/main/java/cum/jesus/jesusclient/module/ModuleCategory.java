package cum.jesus.jesusclient.module;

public enum ModuleCategory {
    SELF("Self"),

    ;

    private String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
