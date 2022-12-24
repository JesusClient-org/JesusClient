package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.command.Command;
import jdk.nashorn.api.scripting.JSObject;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.List;

public class ScriptCommand extends Command {
    private ScriptEngine engine;

    ScriptCommand(String name, String description, String... alias) {
        super(name, description, alias);
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        engine = scriptEngine;
    }

    @Override
    public void run(String alias, String[] args) {
        try {
            ((Invocable)engine).invokeFunction("onCall", alias, args);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        List<String> temp = new ArrayList<>();
        try {
            JSObject returned = (JSObject) ((Invocable)engine).invokeFunction("autoComplete", arg, args);
            for (Object obj : returned.values()) {
                if (obj instanceof String) {
                    temp.add(obj.toString());
                }
            }
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

    @Override
    public boolean isPremiumOnly() {
        return true;
    }
}
