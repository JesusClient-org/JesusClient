package cum.jesus.jesusclient.module;

public enum Category {
    DUNGEONS("Dungeons"), SKYBLOCK("Skyblock"), SELF("Self"), RENDER("Render"), MOVEMENT("Movement"), OTHER("Other");

    private String name;

    Category(String name) { this.name = name; }

    @Override
    public String toString() { return name; }
}
