package cum.jesus.jesusclient.module;

public enum ModuleCategory {
    RENDER("Render"),

    ;

    public final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
