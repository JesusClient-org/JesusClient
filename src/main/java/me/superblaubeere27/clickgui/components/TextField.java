package me.superblaubeere27.clickgui.components;

import me.superblaubeere27.clickgui.AbstractComponent;
import me.superblaubeere27.clickgui.IRenderer;
import me.superblaubeere27.clickgui.Window;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

public class TextField extends AbstractComponent {
    private static final int PREFERRED_WIDTH = 180;
    private static final int PREFERRED_HEIGHT = 22;

    private String value;
    private ValueChangeListener<String> listener;

    private boolean hovered;
    private boolean listening = false;

    private int mouseX;
    private int mouseY;

    public TextField(IRenderer renderer, String text) {
        super(renderer);
        setValue(text);

        setWidth(PREFERRED_WIDTH);
        setHeight(PREFERRED_HEIGHT);
    }

    @Override
    public void render() {
        renderer.drawRect(GL11.GL_QUADS, x, y, getWidth(), getHeight(), hovered ? Window.SECONDARY_FOREGROUND.getRGB() : Window.TERTIARY_FOREGROUND.getRGB());
        renderer.drawOutline(x, y, getWidth(), getHeight(), 1.0f, hovered ? Window.SECONDARY_OUTLINE : Window.SECONDARY_FOREGROUND);

        renderer.drawString(x + getWidth() / 2 - renderer.getStringWidth(value) / 2, y + renderer.getStringHeight(value) / 4, value, Window.FOREGROUND.getRGB());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        setWidth(renderer.getStringWidth(value));
        setHeight(renderer.getStringHeight(value));

        this.value = value;
    }

    public void setListener(ValueChangeListener<String> listener) {
        this.listener = listener;
    }

    @Override
    public boolean mousePressed(int button, int x, int y, boolean offscreen) {
        if (button == 0 && hovered) {
            listening = true;
            return true;
        }
        return super.mousePressed(button, x, y, offscreen);
    }

    @Override
    public boolean keyPressed(int key, char c) {
        if (listening) {
            if (key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_NUMPADENTER || key == Keyboard.KEY_RETURN) {
                listening = false;
            } else if (key == Keyboard.KEY_BACK) {
                if (value.length() > 0)
                    value = value.substring(0, value.length() - 1);
            } else if (!Arrays.asList(keyBlackList).contains(key)) {
                value = value + c;
            }

            if (listener != null)
                if (listener.onValueChange(value))
                    this.value = value;

            return true;
        }

        return super.keyPressed(key, c);
    }

    @Override
    public boolean mouseMove(int x, int y, boolean offscreen) {
        updateHovered(x, y, offscreen);

        return false;
    }

    private void updateHovered(int x, int y, boolean offscreen) {
        hovered = !offscreen && x >= this.x && y >= this.y && x <= this.x + getWidth() && y <= this.y + PREFERRED_HEIGHT;

        mouseX = x;
        mouseY = y;
    }

    private final int[] keyBlackList = {
            Keyboard.KEY_LSHIFT,
            Keyboard.KEY_RSHIFT,
            Keyboard.KEY_UP,
            Keyboard.KEY_RIGHT,
            Keyboard.KEY_LEFT,
            Keyboard.KEY_DOWN,
            Keyboard.KEY_END,
            Keyboard.KEY_NUMLOCK,
            Keyboard.KEY_DELETE,
            Keyboard.KEY_LCONTROL,
            Keyboard.KEY_RCONTROL,
            Keyboard.KEY_CAPITAL,
            Keyboard.KEY_LMENU,
            Keyboard.KEY_F1,
            Keyboard.KEY_F2,
            Keyboard.KEY_F3,
            Keyboard.KEY_F4,
            Keyboard.KEY_F5,
            Keyboard.KEY_F6,
            Keyboard.KEY_F7,
            Keyboard.KEY_F8,
            Keyboard.KEY_F9,
            Keyboard.KEY_F10,
            Keyboard.KEY_F11,
            Keyboard.KEY_F12,
            Keyboard.KEY_F13,
            Keyboard.KEY_F14,
            Keyboard.KEY_F15,
            Keyboard.KEY_F16,
            Keyboard.KEY_F17,
            Keyboard.KEY_F18,
            Keyboard.KEY_F19,
            Keyboard.KEY_SCROLL,
            Keyboard.KEY_RMENU,
            Keyboard.KEY_LMETA,
            Keyboard.KEY_RMETA,
            Keyboard.KEY_FUNCTION,
            Keyboard.KEY_PRIOR,
            Keyboard.KEY_NEXT,
            Keyboard.KEY_INSERT,
            Keyboard.KEY_HOME,
            Keyboard.KEY_PAUSE,
            Keyboard.KEY_APPS,
            Keyboard.KEY_POWER,
            Keyboard.KEY_SLEEP,
            Keyboard.KEY_SYSRQ,
            Keyboard.KEY_CLEAR,
            Keyboard.KEY_SECTION,
            Keyboard.KEY_UNLABELED,
            Keyboard.KEY_KANA,
            Keyboard.KEY_CONVERT,
            Keyboard.KEY_NOCONVERT,
            Keyboard.KEY_YEN,
            Keyboard.KEY_CIRCUMFLEX,
            Keyboard.KEY_AT,
            Keyboard.KEY_UNDERLINE,
            Keyboard.KEY_KANJI,
            Keyboard.KEY_STOP,
            Keyboard.KEY_AX,
            Keyboard.KEY_TAB,
    };
}
