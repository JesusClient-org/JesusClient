package cum.jesus.jesusclient.module;

import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IModule;
import cum.jesus.jesusclient.JesusClient;

import java.util.stream.Stream;

public enum ModuleCategory implements ICategory {
    RENDER("Render"),
    OTHER("Other")

    ;

    public final String name;

    ModuleCategory(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public Stream<IModule> getModules() {
        return JesusClient.instance.moduleHandler.getRegistry().getModules(this).stream().map(module -> module);
    }

    @Override
    public String toString() {
        return name;
    }
}
