package cum.jesus.jesusclient.module;

public enum Category {
    COMBAT("Combat"), SKYBLOCK("Skyblock"), SELF("Self"), RENDER("Render"), FUNNY("Funny"), OTHER("Other");

    private String name;

    Category(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
