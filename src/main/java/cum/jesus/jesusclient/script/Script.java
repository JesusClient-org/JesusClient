package cum.jesus.jesusclient.script;

import java.io.File;

public final class Script {
    private String name;
    private ScriptMetadata metadata;
    private File root;

    public Script(String name, ScriptMetadata metadata, File root) {
        this.name = name;
        this.metadata = metadata;
        this.root = root;
    }

    public String getName() {
        return name;
    }

    public ScriptMetadata getMetadata() {
        return metadata;
    }

    public File getRoot() {
        return root;
    }
}
