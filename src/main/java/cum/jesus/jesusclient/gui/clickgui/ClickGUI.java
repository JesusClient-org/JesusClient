package cum.jesus.jesusclient.gui.clickgui;


import com.lukflug.panelstudio.base.*;
import com.lukflug.panelstudio.component.IComponent;
import com.lukflug.panelstudio.component.IResizable;
import com.lukflug.panelstudio.component.IScrollSize;
import com.lukflug.panelstudio.hud.HUDGUI;
import com.lukflug.panelstudio.layout.*;
import com.lukflug.panelstudio.mc8forge.MinecraftHUDGUI;
import com.lukflug.panelstudio.popup.*;
import com.lukflug.panelstudio.setting.*;
import com.lukflug.panelstudio.theme.*;
import com.lukflug.panelstudio.widget.*;
import cum.jesus.jesusclient.gui.panelstudio.Client;
import cum.jesus.jesusclient.module.ModuleCategory;
import cum.jesus.jesusclient.module.modules.render.ClickGUIModule;
import cum.jesus.jesusclient.module.modules.render.ClickGUIModule.Theme;
import cum.jesus.jesusclient.setting.settings.ColorSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

public final class ClickGUI extends MinecraftHUDGUI {
    public static final ClickGUI INSTANCE = new ClickGUI();

    public static final int WIDTH = 120;
    public static final int HEIGHT = 12;
    public static final int DISTANCE = 6;
    public static final int BORDER = 2;

    private final GUIInterface inter;
    private final HUDGUI gui;

    private ClickGUI() {
        IClient client = new Client();

        inter = new GUIInterface(true) {
            @Override
            protected String getResourcePrefix() {
                return "jesusclient:";
            }
        };

        ITheme theme = new OptimizedTheme(new ThemeSelector(inter));
        IToggleable guiToggle = new SimpleToggleable(false);
        IToggleable hudToggle = new SimpleToggleable(false) {
            @Override
            public boolean isOn() {
                return false;
            }
        };

        gui = new HUDGUI(inter, theme.getDescriptionRenderer(), (IPopupPositioner) new MousePositioner(new Point(10, 10)), guiToggle, hudToggle);
        Supplier<Animation> animation = () -> new SettingsAnimation(ClickGUIModule.INSTANCE.animationSpeed::getValue, inter::getTime);

        BiFunction<Context, Integer, Integer> scrollHeight = (context, componentHeight) -> Math.min(componentHeight, Math.max(HEIGHT * 4, ClickGUI.this.height - context.getPos().y - HEIGHT));

        PopupTuple popupType = new PopupTuple(new PanelPositioner(new Point(0, 0)), false, new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return scrollHeight.apply(context, componentHeight);
            }
        });

        PopupTuple colorPopup = new PopupTuple(new CenteredPositioner(() -> new Rectangle(new Point(0, 0), inter.getWindowSize())), true, new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return scrollHeight.apply(context, componentHeight);
            }
        });

        IntFunction<IResizable> resizable = width -> new IResizable() {
            Dimension size = new Dimension(width, 320);

            @Override
            public Dimension getSize() {
                return new Dimension(size);
            }

            @Override
            public void setSize(Dimension size) {
                this.size.width = size.width;
                this.size.height = size.height;
                if (size.width < 75) this.size.width = 75;
                if (size.height < 50) this.size.height = 50;
            }
        };

        Function<IResizable, IScrollSize> resizableHeight = size -> new IScrollSize() {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return size.getSize().height;
            }
        };

        IntPredicate keybindKey = scancode -> scancode == Keyboard.KEY_DELETE;
        IntPredicate charFilter = character -> character >= ' ';

        ITextFieldKeys keys = new ITextFieldKeys() {
            @Override
            public boolean isBackspaceKey(int scancode) {
                return scancode == Keyboard.KEY_BACK;
            }

            @Override
            public boolean isDeleteKey(int scancode) {
                return scancode == Keyboard.KEY_DELETE;
            }

            @Override
            public boolean isInsertKey(int scancode) {
                return scancode == Keyboard.KEY_INSERT;
            }

            @Override
            public boolean isLeftKey(int scancode) {
                return scancode == Keyboard.KEY_LEFT;
            }

            @Override
            public boolean isRightKey(int scancode) {
                return scancode == Keyboard.KEY_RIGHT;
            }

            @Override
            public boolean isHomeKey(int scancode) {
                return scancode == Keyboard.KEY_HOME;
            }

            @Override
            public boolean isEndKey(int scancode) {
                return scancode == Keyboard.KEY_END;
            }

            @Override
            public boolean isCopyKey(int scancode) {
                return scancode == Keyboard.KEY_C;
            }

            @Override
            public boolean isPasteKey(int scancode) {
                return scancode == Keyboard.KEY_V;
            }

            @Override
            public boolean isCutKey(int scancode) {
                return scancode == Keyboard.KEY_X;
            }

            @Override
            public boolean isAllKey(int scancode) {
                return scancode == Keyboard.KEY_A;
            }
        };

        IComponentGenerator generator = new ComponentGenerator(keybindKey, charFilter, keys);
        IComponentGenerator cycleGenerator = new ComponentGenerator(keybindKey, charFilter, keys) {
            @Override
            public IComponent getEnumComponent(IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new CycleSwitch(setting, theme.getCycleSwitchRenderer(isContainer));
            }
        };

        IComponentGenerator csgoGenerator = new ComponentGenerator(keybindKey, charFilter, keys) {
            @Override
            public IComponent getBooleanComponent(IBooleanSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ToggleSwitch(setting, theme.getToggleSwitchRenderer(isContainer));
            }

            @Override
            public IComponent getEnumComponent(IEnumSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new DropDownList(setting, theme, isContainer, false, keys, new IScrollSize() {}, adder::addPopup) {
                    @Override
                    protected Animation getAnimation() {
                        return animation.get();
                    }

                    @Override
                    public boolean allowCharacter(char character) {
                        return charFilter.test(character);
                    }

                    @Override
                    protected boolean isUpKey(int key) {
                        return key == Keyboard.KEY_UP;
                    }

                    @Override
                    protected boolean isDownKey(int key) {
                        return key == Keyboard.KEY_DOWN;
                    }

                    @Override
                    protected boolean isEnterKey(int key) {
                        return key == Keyboard.KEY_RETURN;
                    }
                };
            }

            @Override
            public IComponent getNumberComponent(INumberSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new Spinner(setting, theme, isContainer, true, keys);
            }

            @Override
            public IComponent getColorComponent(IColorSetting setting, Supplier<Animation> animation, IComponentAdder adder, ThemeTuple theme, int colorLevel, boolean isContainer) {
                return new ColorPickerComponent(setting, new ThemeTuple(theme.theme, theme.logicalLevel,colorLevel));
            }
        };

        IComponentAdder classicPanelAdder = new PanelAdder(gui, false,() -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.ClassicPanel, title -> "classicPanel_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                return resizable.apply(width);
            }

            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return resizableHeight.apply(size);
            }
        };

        ILayout classicPanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + DISTANCE) / 2,HEIGHT + DISTANCE, animation, level -> ChildUtil.ChildMode.DOWN, level -> ChildUtil.ChildMode.DOWN, popupType);
        classicPanelLayout.populateGUI(classicPanelAdder, generator, client, theme);

        IComponentAdder popupPanelAdder = new PanelAdder(gui, false, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.PopupPanel, title -> "popupPanel_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                return resizable.apply(width);
            }

            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return resizableHeight.apply(size);
            }
        };

        ILayout popupPanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE,DISTANCE), (WIDTH+DISTANCE) / 2, HEIGHT + DISTANCE, animation, level -> ChildUtil.ChildMode.POPUP, level ->  ChildUtil.ChildMode.DOWN,popupType);
        popupPanelLayout.populateGUI(popupPanelAdder,generator,client, theme);

        IComponentAdder draggablePanelAdder = new PanelAdder(gui, false, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.DraggablePanel, title -> "draggablePanel_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                return resizable.apply(width);
            }

            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return resizableHeight.apply(size);
            }
        };

        ILayout draggablePanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + DISTANCE) / 2,HEIGHT + DISTANCE, animation, level -> level == 0 ? ChildUtil.ChildMode.DRAG_POPUP : ChildUtil.ChildMode.DOWN, level -> ChildUtil.ChildMode.DOWN,popupType);
        draggablePanelLayout.populateGUI(draggablePanelAdder, generator, client, theme);

        IComponentAdder singlePanelAdder = new SinglePanelAdder(gui, new Labeled("Jesus Client", null, () -> true), theme, new Point(10, 10),WIDTH * ModuleCategory.values().length, animation, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.SinglePanel, "singlePanel") {
            @Override
            protected IResizable getResizable(int width) {
                return resizable.apply(width);
            }

            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return resizableHeight.apply(size);
            }
        };

        ILayout singlePanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + DISTANCE) / 2,HEIGHT + DISTANCE, animation, level -> ChildUtil.ChildMode.DOWN, level -> ChildUtil.ChildMode.DOWN, popupType);
        singlePanelLayout.populateGUI(singlePanelAdder, generator, client, theme);

        IComponentAdder panelMenuAdder = new StackedPanelAdder(gui, new Labeled("Jesus Client", null, () -> true), theme, new Point(10, 10), WIDTH, animation, ChildUtil.ChildMode.POPUP, new PanelPositioner(new Point(0, 0)), () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.PanelMenu, "panelMenu");
        ILayout panelMenuLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + DISTANCE) / 2, HEIGHT + DISTANCE, animation, level -> ChildUtil.ChildMode.POPUP, level -> ChildUtil.ChildMode.POPUP, popupType);
        panelMenuLayout.populateGUI(panelMenuAdder, generator, client, theme);

        IComponentAdder colorPanelAdder = new PanelAdder(gui, false, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.ColorPanel, title -> "colorPanel_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                return resizable.apply(width);
            }

            @Override
            protected IScrollSize getScrollSize(IResizable size) {
                return resizableHeight.apply(size);
            }
        };

        ILayout colorPanelLayout = new PanelLayout(WIDTH, new Point(DISTANCE, DISTANCE), (WIDTH + DISTANCE) / 2, HEIGHT + DISTANCE, animation, level -> ChildUtil.ChildMode.DOWN, level -> ChildUtil.ChildMode.POPUP, colorPopup);
        colorPanelLayout.populateGUI(colorPanelAdder, cycleGenerator, client, theme);

        AtomicReference<IResizable> horizontalResizable = new AtomicReference<>(null);
        IComponentAdder horizontalCSGOAdder = new PanelAdder(gui, true, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.CSGOHorizontal, title -> "horizontalCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                horizontalResizable.set(resizable.apply(width));
                return horizontalResizable.get();
            }
        };

        ILayout horizontalCSGOLayout = new CSGOLayout(new Labeled("Jesus Client", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", true, true, 2, ChildUtil.ChildMode.POPUP, colorPopup) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(horizontalResizable.get()).getScrollHeight(null, height);
            }
        };
        horizontalCSGOLayout.populateGUI(horizontalCSGOAdder, csgoGenerator, client, theme);

        AtomicReference<IResizable> verticalResizable = new AtomicReference<>(null);
        IComponentAdder verticalCSGOAdder = new PanelAdder(gui, true, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.CSGOVertical, title -> "verticalCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                verticalResizable.set(resizable.apply(width));
                return verticalResizable.get();
            }
        };

        ILayout verticalCSGOLayout = new CSGOLayout(new Labeled("Jesus Client", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", false, true, 2, ChildUtil.ChildMode.POPUP, colorPopup) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(verticalResizable.get()).getScrollHeight(null, height);
            }
        };
        verticalCSGOLayout.populateGUI(verticalCSGOAdder, csgoGenerator, client, theme);

        AtomicReference<IResizable> categoryResizable = new AtomicReference<>(null);
        IComponentAdder categoryCSGOAdder = new PanelAdder(gui, true, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.CSGOCategory, title -> "categoryCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                categoryResizable.set(resizable.apply(width));
                return categoryResizable.get();
            }
        };

        ILayout categoryCSGOLayout = new CSGOLayout(new Labeled("Jesus Client", null, () -> true), new Point(100, 100), 480, WIDTH, animation, "Enabled", false, false, 2, ChildUtil.ChildMode.POPUP, colorPopup) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(categoryResizable.get()).getScrollHeight(null, height);
            }
        };
        categoryCSGOLayout.populateGUI(categoryCSGOAdder, csgoGenerator, client, theme);

        AtomicReference<IResizable> searchableResizable = new AtomicReference<>(null);
        IComponentAdder searchableCSGOAdder = new PanelAdder(gui, true, () -> ClickGUIModule.INSTANCE.layout.getValue() == ClickGUIModule.Layout.SearchableCSGO, title -> "searchableCSGO_" + title) {
            @Override
            protected IResizable getResizable(int width) {
                searchableResizable.set(resizable.apply(width));
                return searchableResizable.get();
            }
        };

        ILayout searchableCSGOLayout = new SearchableLayout(new Labeled("Jesus Client",null, () -> true), new Labeled("Search", null, () -> true), new Point(100,100), 480, WIDTH, animation, "Enabled", 2, ChildUtil.ChildMode.POPUP,colorPopup, Comparator.comparing(ILabeled::getDisplayName), charFilter, keys) {
            @Override
            public int getScrollHeight(Context context, int componentHeight) {
                return resizableHeight.apply(searchableResizable.get()).getScrollHeight(null, height);
            }
        };
        searchableCSGOLayout.populateGUI(searchableCSGOAdder, csgoGenerator, client, theme);
    }

    @Override
    protected HUDGUI getGUI() {
        return gui;
    }

    @Override
    protected GUIInterface getInterface() {
        return inter;
    }

    @Override
    protected int getScrollSpeed() {
        return ClickGUIModule.INSTANCE.scrollSpeed.getValue();
    }

    private class ThemeSelector implements IThemeMultiplexer {
        protected Map<Theme, ITheme> themes = new EnumMap<>(Theme.class);

        public ThemeSelector(IInterface inter) {
            addTheme(Theme.AhahaFreaky, new ImpactTheme(new ThemeScheme(Theme.AhahaFreaky), 9, 4));
        }

        @Override
        public ITheme getTheme() {
            return themes.getOrDefault(ClickGUIModule.INSTANCE.theme.getValue(), themes.get(Theme.AhahaFreaky));
        }

        private void addTheme(Theme key, ITheme value) {
            themes.put(key, value);
            value.loadAssets(inter);
        }

        private class ThemeScheme implements IColorScheme {
            private final Theme themeValue;
            private final String themeName;

            public ThemeScheme (Theme themeValue) {
                this.themeValue=themeValue;
                this.themeName=themeValue.toString().toLowerCase();
            }

            @Override
            public void createSetting(ITheme theme, String name, String description, boolean hasAlpha, boolean allowsRainbow, Color color, boolean rainbow) {
                ClickGUIModule.INSTANCE.theme.subSettings.add(new ColorSetting(name, description, color, hasAlpha, allowsRainbow, rainbow, false).withDependency(() -> ClickGUIModule.INSTANCE.theme.getValue() == themeValue).setConfigName(themeName + "-" + name));
            }

            @Override
            public Color getColor(String name) {
                return ((ColorSetting) ClickGUIModule.INSTANCE.theme.subSettings.stream().filter(setting -> setting.getConfigName().equals(themeName + "-" + name)).findFirst().orElse(null)).getValue();
            }
        }
    }
}
