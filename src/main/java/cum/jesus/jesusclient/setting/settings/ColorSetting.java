package cum.jesus.jesusclient.setting.settings;

import com.lukflug.panelstudio.setting.IColorSetting;
import com.lukflug.panelstudio.theme.ITheme;
import cum.jesus.jesusclient.config.builder.ConfigBuilder;
import cum.jesus.jesusclient.config.reader.ConfigReader;
import cum.jesus.jesusclient.gui.clickgui.ClickGUI;
import cum.jesus.jesusclient.module.modules.render.ClickGUIModule;
import cum.jesus.jesusclient.setting.Setting;

import java.awt.*;

/**
 * Represents a color
 */
public final class ColorSetting extends Setting<Color> implements IColorSetting {
    private boolean allowRainbow;
    private boolean allowAlpha;
    private boolean rainbow;

    public ColorSetting(String name, String description, Color defaultValue, boolean allowAlpha, boolean allowRainbow, boolean rainbow, boolean hidden) {
        super(name, description, defaultValue, hidden);

        this.allowRainbow = allowRainbow;
        this.allowAlpha = allowAlpha;
        this.rainbow = rainbow;
    }

    public ColorSetting(String name, String description, Color defaultValue, boolean allowAlpha, boolean allowRainbow) {
        this(name, description, defaultValue, allowAlpha, allowRainbow, false, false);
    }

    public ColorSetting(String name, String description, Color defaultValue) {
        this(name, description, defaultValue, false, false, false, false);
    }

    @Override
    public Color getValue() {
        if (rainbow) {
            int speed = ClickGUIModule.INSTANCE.rainbowSpeed.getValue();
            return ITheme.combineColors(Color.getHSBColor((System.currentTimeMillis() % (360 * speed)) / (float) (360 * speed), 1, 1), super.getValue());
        }

        return super.getValue();
    }

    @Override
    public Color getColor() {
        return super.getValue();
    }

    @Override
    public boolean getRainbow() {
        return rainbow;
    }

    @Override
    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public boolean allowsAlpha() {
        return allowAlpha;
    }

    @Override
    public boolean hasAlpha() {
        return allowAlpha;
    }

    @Override
    public boolean allowsRainbow() {
        return allowRainbow;
    }

    @Override
    public boolean hasHSBModel() {
        return ClickGUIModule.INSTANCE.colorModel.getValue() == ClickGUIModule.ColorModel.HSB;
    }

    @Override
    public void addToBuilder(ConfigBuilder builder) {
        builder.addInt(getConfigName(), getValue().getRGB());
    }

    @Override
    public void getFromReader(ConfigReader reader) {
        setValue(new Color(reader.getInt(getConfigName(), getDefaultValue().getRGB())));
    }
}
