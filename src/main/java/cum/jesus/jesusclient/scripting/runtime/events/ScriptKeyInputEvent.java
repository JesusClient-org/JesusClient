package cum.jesus.jesusclient.scripting.runtime.events;

public class ScriptKeyInputEvent {
    private int key;

    public ScriptKeyInputEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
