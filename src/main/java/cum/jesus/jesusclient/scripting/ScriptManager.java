package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.scripting.runtime.ScriptRuntime;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;

public class ScriptManager {
    private static final String moduleScriptHeader = "var rt = Java.type('" + ScriptRuntime.class.getCanonicalName() + "');\n";
    private ScriptEngine engine;

    public ScriptManager() {
        newScript();
    }

    public void newScript() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");

        if (engine == null) return;

        try {
            engine.eval(moduleScriptHeader);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public Object eval(String script) throws ScriptException {
        if (engine == null) return "Failed to initialize engine";

        return engine.eval(script);
    }

    public Script load(File scriptFile) {

    }
}
