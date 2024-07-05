package cum.jesus.jesusclient.script.languages.js;

import cum.jesus.jesusclient.script.Register;
import cum.jesus.jesusclient.script.ScriptLoader;

public final class JSRegister extends Register {
    @Override
    public ScriptLoader getLoader() {
        return JSLoader.INSTANCE;
    }
}
