package cum.jesus.jesusclient.module;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public final class Keybind {
    private int key;
    private Runnable onPress = null;

    public Keybind(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getKeyName() {
        return Keyboard.getKeyName(key);
    }

    public Runnable getOnPress() {
        return onPress;
    }

    public void setOnPress(Runnable onPress) {
        this.onPress = onPress;
    }

    public boolean isDown() {
        if (key == 0) return false;
        if (key < 0) return Mouse.isButtonDown(key + 100);
        return Keyboard.isKeyDown(key);
    }
}
