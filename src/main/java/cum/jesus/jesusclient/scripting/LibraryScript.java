package cum.jesus.jesusclient.scripting;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryScript extends Script {
    private final Map<String, Object> publicObjects = new HashMap<>();

    public LibraryScript(String name, String description, String version, String[] authors, String[] dependencies, ScriptIndex index) {
        super(name, description, version, authors, dependencies, index);
    }

    public Map<String, Object> getPublicObjects() {
        return publicObjects;
    }

    public void addPublicObject(String name, Object obj) {
        publicObjects.put(name, obj);
    }

    public void addObjectToEngine(ScriptEngine engine) throws ScriptException {
        Bindings objBindings = engine.createBindings();
        objBindings.putAll(publicObjects);

        engine.put(this.getName().replace(' ', '_'), objBindings);
    }
}
