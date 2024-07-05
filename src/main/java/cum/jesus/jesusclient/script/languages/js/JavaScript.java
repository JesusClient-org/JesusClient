package cum.jesus.jesusclient.script.languages.js;

import cum.jesus.jesusclient.script.Language;
import cum.jesus.jesusclient.script.ScriptLoader;

public final class JavaScript implements Language {
    @Override
    public String getName() {
        return "JavaScript";
    }

    @Override
    public String[] getLanguageIDs() {
        return new String[] {
                "javascript",
                "js"
        };
    }

    @Override
    public ScriptLoader getLoader() {
        return JSLoader.INSTANCE;
    }
}
