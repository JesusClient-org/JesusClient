package cum.jesus.jesusclient.script;

public interface Language {
    String getName();

    String[] getLanguageIDs();

    ScriptLoader getLoader();
}
