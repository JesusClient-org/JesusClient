package cum.jesus.jesusclient.script;

import cum.jesus.jesusclient.script.trigger.Trigger;
import cum.jesus.jesusclient.script.trigger.TriggerType;

import java.lang.invoke.MethodHandle;
import java.net.URI;
import java.net.URL;
import java.util.List;

public interface ScriptLoader {
    void setup(List<URL> jars);

    void asmSetup();

    void asmPass(Script script, URI asmURI);

    void entrySetup();

    void entryPass(Script script, URI entryURI);

    MethodHandle asmInvokeLookup(Script script, URI functionURI);

    void addTrigger(Trigger trigger);

    void removeTrigger(Trigger trigger);

    void clearTriggers();

    void trigger(Trigger trigger, Object method, Object[] args);

    void execTriggerType(TriggerType type, Object[] args);
}
