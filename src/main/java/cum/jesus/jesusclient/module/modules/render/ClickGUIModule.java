package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.gui.clickgui.ClickGUI;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.ModuleCategory;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.renderers.BasicNotificationRenderer;
import cum.jesus.jesusclient.notification.renderers.ChatNotificationRenderer;
import cum.jesus.jesusclient.setting.settings.*;
import org.lwjgl.input.Keyboard;

public final class ClickGUIModule extends Module {
    public static final ClickGUIModule INSTANCE = new ClickGUIModule();

    public final EnumSetting<ColorModel> colorModel = new EnumSetting<>("Color model", "Whether to use RGB or HSB for colors", ColorModel.HSB, ColorModel.class);
    public final IntegerSetting rainbowSpeed = new IntegerSetting("Rainbow speed", "The speed of the rgb gamer lights", 1, 100, 32);
    public final IntegerSetting scrollSpeed = new IntegerSetting("Scroll speed", "How fast you scroll", 0, 20, 10);
    public final IntegerSetting animationSpeed = new IntegerSetting("Animation speed", "How fast GUI animations play out", 0, 1000, 200);
    public final BooleanSetting enableNotifications = new BooleanSetting("Enable notifications", "Displays a notification when a module is toggled with a keybind", true);
    public final EnumSetting<NotificationRenderer> notificationRenderer = new EnumSetting<>("Notification style", "The way notifications are displayed", NotificationRenderer.Rectangle, NotificationRenderer.class).withDependency(enableNotifications::getValue);
    public final EnumSetting<Theme> theme = new EnumSetting<>("Theme", "What theme to use", Theme.AhahaFreaky, Theme.class);
    public final EnumSetting<Layout> layout = new EnumSetting<>("Layout", "What layout to use", Layout.CSGOHorizontal, Layout.class);

    public final StringSetting commandPrefix = new StringSetting("Command prefix", "Prefix for Jesus Client commands", "-");

    private ClickGUIModule() {
        super("Click GUI", "Allows customizing the GUI", ModuleCategory.RENDER, true, false, Keyboard.KEY_RCONTROL);

        notificationRenderer.setChangeListener(((oldValue, newValue) -> {
            switch (newValue) {
                case Rectangle:
                    NotificationManager.setRenderer(BasicNotificationRenderer.INSTANCE);
                    break;
                case Chat:
                    NotificationManager.setRenderer(ChatNotificationRenderer.INSTANCE);
                    break;
            }
        }));

        addSettings(colorModel, rainbowSpeed, scrollSpeed, animationSpeed, enableNotifications, notificationRenderer, theme, layout);
    }

    @Override
    protected void onKeybind() {
        ClickGUI.INSTANCE.enterGUI();
    }

    @Override
    protected void onEnable() {
        setToggled(false);
    }

    public enum ColorModel {
        RGB, HSB
    }

    public enum NotificationRenderer {
        Rectangle,
        Chat
    }

    public enum Theme {
        AhahaFreaky
    }

    public enum Layout {
        ClassicPanel,
        PopupPanel,
        DraggablePanel,
        SinglePanel,
        PanelMenu,
        ColorPanel,
        CSGOHorizontal,
        CSGOVertical,
        CSGOCategory,
        SearchableCSGO
    }
}
