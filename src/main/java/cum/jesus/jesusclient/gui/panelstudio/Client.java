package cum.jesus.jesusclient.gui.panelstudio;

import com.lukflug.panelstudio.setting.ICategory;
import com.lukflug.panelstudio.setting.IClient;
import cum.jesus.jesusclient.module.ModuleCategory;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Client implements IClient {
    @Override
    public Stream<ICategory> getCategories() {
        return Arrays.stream(ModuleCategory.values());
    }
}
