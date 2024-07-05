package cum.jesus.jesusclient.script.languages.js;

import cum.jesus.jesusclient.script.Register;
import cum.jesus.jesusclient.script.ScriptLoader;

public final class JSRegister extends Register {
    public static final JSRegister INSTANCE = new JSRegister();

    private JSRegister() {
    }

    @Override
    public ScriptLoader getLoader() {
        return JSLoader.INSTANCE;
    }
}
