package cum.jesus.jesusclient.module.modules.render;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.KeyInputEvent;
import cum.jesus.jesusclient.events.Render2DEvent;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.gui.clickgui.BoringRenderThingy;
import cum.jesus.jesusclient.gui.tabgui.SubTab;
import cum.jesus.jesusclient.gui.tabgui.Tab;
import cum.jesus.jesusclient.gui.tabgui.TabGui;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.BooleanSetting;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.utils.font.GlyphPageFontRenderer;
import me.superblaubeere27.clickgui.IRenderer;
import me.superblaubeere27.clickgui.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static cum.jesus.jesusclient.module.modules.self.Timer.getTimer;

public class Hud extends Module {
    private TabGui<Module> tabGui;
    @NotNull
    private List<Integer> fps = new ArrayList<>();
    private int fpsStatLength = 250;

    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    private BooleanSetting tabGuiEnabled = new BooleanSetting("TabGui", false);
    private BooleanSetting showInfo = new BooleanSetting("Show info", false);
    private BooleanSetting watermark = new BooleanSetting("Watermark", true);
    private BooleanSetting arrayList = new BooleanSetting("ArrayList", true);
    public static BooleanSetting lgbtMode = new BooleanSetting("LGBT mode", false);

    private GlyphPageFontRenderer consolas;
    private GlyphPageFontRenderer bigConsolas;
    private IRenderer renderer;
    private IRenderer bigRenderer;

    public Hud() {
        super("HUD", "Controls different HUD elements from Jesus Client", Category.RENDER);

        HashMap<Category, java.util.List<Module>> moduleCategoryMap = new HashMap<>();

        consolas = GlyphPageFontRenderer.create("Consolas", 12, false, false, false);
        bigConsolas = GlyphPageFontRenderer.create("Consolas", 24, true, false, false);
        renderer = new HudRenderer(consolas);
        bigRenderer = new HudRenderer(bigConsolas);

        tabGui = new TabGui<>();

        for (Module module : JesusClient.INSTANCE.moduleManager.getModules()) {
            if (!moduleCategoryMap.containsKey(module.getCategory())) {
                moduleCategoryMap.put(module.getCategory(), new ArrayList<>());
            }

            if (!module.isHidden()) moduleCategoryMap.get(module.getCategory()).add(module);
        }

        moduleCategoryMap.entrySet().stream().sorted(Comparator.comparingInt(cat -> cat.getKey().toString().hashCode())).forEach(cat -> {
            Tab<Module> tab = new Tab<>(cat.getKey().toString());

            for (Module module : cat.getValue()) {
                tab.addSubTab(new SubTab<>(module.getName(), subTab -> subTab.getObject().toggle(), module));
            }

            tabGui.addTab(tab);
        });
    }

    public static int lgbt(int delay) {
        double lgbtState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        lgbtState %= 360;
        return Color.getHSBColor((float) (lgbtState / 360.0f), 0.8f, 0.7f).getRGB();
    }

    @EventTarget
    private void render2D(Render2DEvent event) {
        NotificationManager.render();

        if (!isToggled()) return;

        fps.add(Minecraft.getDebugFPS());
        while (fps.size() > fpsStatLength) {
            fps.remove(0);
        }

        int color = lgbtMode.getObject() ? lgbt(0) : new Color(200, 0, 0).getRGB();

        ScaledResolution res = new ScaledResolution(mc);

        int blackBarHeight = consolas.getFontHeight() * 2 + 4;

        if (showInfo.getObject()) Gui.drawRect(0, res.getScaledHeight() - blackBarHeight, res.getScaledWidth(), res.getScaledHeight(), (new Color(0, 0, 0, 150)).getRGB());
        if (watermark.getObject()) {
            bigRenderer.drawString(2, 2, JesusClient.CLIENT_NAME, color);
            int i = bigRenderer.getStringWidth(JesusClient.CLIENT_NAME);

            renderer.drawString(i+3, bigConsolas.getFontHeight() - 2, JesusClient.CLIENT_VERSION, color);
            renderer.drawString(4, consolas.getFontHeight() * 2 + 10, "by " + JesusClient.CLIENT_AUTHOR, color);
        }

        if (showInfo.getObject()) {
            bigRenderer.drawString(1, res.getScaledHeight()*2 - bigConsolas.getFontHeight()*2, JesusClient.CLIENT_INITIALS.replace(" ", ""), color);
            int initialSize = bigRenderer.getStringWidth(JesusClient.CLIENT_INITIALS);

            double currSpeed = Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) * (double)getTimer().timerSpeed * 20.0;

            renderer.drawString(initialSize + 4, res.getScaledHeight()*2 - consolas.getFontHeight()*2 - 2, "FPS: " + Minecraft.getDebugFPS(), -1);
            int fpsWidth = renderer.getStringWidth("FPS: " + Minecraft.getDebugFPS());
            renderer.drawString(initialSize + 4, res.getScaledHeight()*2 - consolas.getFontHeight()*2 * 2 - 2, String.format("BPS: %.2f", currSpeed), -1);

            fpsWidth = Math.max(fpsWidth, renderer.getStringWidth(String.format("BPS: %.2f", currSpeed)));


            LocalDateTime now = LocalDateTime.now();
            String date = dateFormat.format(now);
            String time = timeFormat.format(now);


            renderer.drawString(res.getScaledWidth()*2 - renderer.getStringWidth(date) - 5, res.getScaledHeight()*2 - consolas.getFontHeight()*2 - 2, date, -1);
            renderer.drawString(res.getScaledWidth()*2 - renderer.getStringWidth(time) - 5, res.getScaledHeight()*2 - consolas.getFontHeight()*2 * 2 - 2, time, -1);

            int max = fps.stream().max(Integer::compareTo).orElse(1);
            double transform = blackBarHeight / 2.0 / (double) max;

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glLineWidth(1.0f);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            GL11.glBegin(GL11.GL_LINE_STRIP);

            fpsWidth += 3;

            double v = ((res.getScaledWidth() / 2.0 - 100) - fpsWidth) / (double) fps.size();

            for (int j = 0; j < fps.size(); j++) {
                int currFPS = fps.get(j);

                GL11.glVertex2d(fpsWidth + j * v, res.getScaledHeight() - transform * currFPS);
            }

            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        if (arrayList.getObject()) {
            AtomicInteger offset = new AtomicInteger(3);
            AtomicInteger index = new AtomicInteger();

            JesusClient.INSTANCE.moduleManager.getModules().stream().filter(mod -> mod.isToggled() && !mod.isHidden() && mod.getClass() != Hud.class).sorted(Comparator.comparingInt(mod -> -renderer.getStringWidth(mod.getName()))).forEach(mod -> {
                int arrayColor = lgbtMode.getObject() ? lgbt(index.get() * 100) : new Color(200, 0, 0).getRGB();
                renderer.drawString(res.getScaledWidth()*2 - consolas.getStringWidth(mod.getName())*2 - 3, offset.get(), mod.getName(), arrayColor);

                offset.addAndGet(consolas.getFontHeight() + 3);
                index.getAndIncrement();
            });
        }

        //NotificationManager.render();
        if (tabGuiEnabled.getObject()) tabGui.render(5, (2 + consolas.getFontHeight()) * 3);
    }

    @EventTarget
    public void onKey(@NotNull KeyInputEvent event) {
        tabGui.handleKey(event.getKey());
    }
}
