package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;

public class NoBlind extends Module {
    public static NoBlind INSTANCE = new NoBlind();

    public NoBlind() {
        super("NoBlind", "Removes blindness potion effect", Category.RENDER);
    }
}