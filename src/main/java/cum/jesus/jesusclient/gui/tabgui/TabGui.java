package cum.jesus.jesusclient.gui.tabgui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.Hud;
import cum.jesus.jesusclient.module.modules.render.HudRenderer;
import cum.jesus.jesusclient.utils.font.GlyphPageFontRenderer;
import me.superblaubeere27.clickgui.IRenderer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class TabGui<T> { //TODO make it look better
    static final int OFFSET = 3;
    @NotNull
    static Color BACKGROUND = new Color(0, 0, 0, 175);
    @NotNull
    static Color BORDER = new Color(0, 0, 0, 255);
    @NotNull
    static int SELECTED = Hud.lgbtMode.getObject() ? Hud.lgbt(0) : new Color(200, 0, 0, 200).getRGB();
    static Color FOREGROUND = Color.white;
    @NotNull
    private List<Tab<T>> tabs = new ArrayList<>();
    private int selectedTab = 0;
    private int selectedSubTab = -1;

    private GlyphPageFontRenderer consolas;

    public static void drawRect(int glFlag, int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            int j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(glFlag, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double) left, (double) bottom, 0.0D).endVertex();
        worldrenderer.pos((double) right, (double) bottom, 0.0D).endVertex();
        worldrenderer.pos((double) right, (double) top, 0.0D).endVertex();
        worldrenderer.pos((double) left, (double) top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public TabGui() {
        consolas = GlyphPageFontRenderer.create("Consolas", 15, false, false, false);
    }

    public void addTab(Tab<T> tab) {
        tabs.add(tab);
    }

    public void render(int x, int y) {
        glTranslated(x, y, 0);

        FontRenderer font = JesusClient.mc.fontRendererObj;

        int height = (consolas.getFontHeight() + OFFSET) * tabs.size();

        int width = 0;

        for (Tab<T> tab : tabs) {
            if (consolas.getStringWidth(tab.getText()) > width) {
                width = consolas.getStringWidth(tab.getText());
            }
        }

        width += 6;

        drawRect(GL_QUADS, 0, 0, width, height, BACKGROUND.getRGB());


        int offset = 2;

        int i = 0;

        for (Tab<T> tab : tabs) {
            if (selectedTab == i) {
                drawRect(GL_QUADS, 0, offset - 2, width, offset + consolas.getFontHeight() + OFFSET - 2, SELECTED);

                if (selectedSubTab != -1) {
                    tab.renderSubTabs(width, offset - 2, selectedSubTab);
                }
            }

            consolas.drawString(tab.getText(), 2, offset, FOREGROUND.getRGB(), false);
            offset += consolas.getFontHeight() + OFFSET;
            i++;
        }
        glLineWidth(1.0f);
        drawRect(GL_LINE_LOOP, 0, 0, width, height, BORDER.getRGB());

        glTranslated(-x, -y, 0);
    }

    public void handleKey(int keycode) {
        if (keycode == Keyboard.KEY_DOWN) {
            if (selectedSubTab == -1) {
                selectedTab++;

                if (selectedTab >= tabs.size()) {
                    selectedTab = 0;
                }
            } else {
                selectedSubTab++;

                if (selectedSubTab >= tabs.get(selectedTab).getSubTabs().size()) {
                    selectedSubTab = 0;
                }
            }
        } else if (keycode == Keyboard.KEY_UP) {
            if (selectedSubTab == -1) {
                selectedTab--;

                if (selectedTab < 0) {
                    selectedTab = tabs.size() - 1;
                }
            } else {
                selectedSubTab--;

                if (selectedSubTab < 0) {
                    selectedSubTab = tabs.get(selectedTab).getSubTabs().size() - 1;
                }
            }
        } else if (keycode == Keyboard.KEY_LEFT) {
            selectedSubTab = -1;
        } else if (selectedSubTab == -1 && (keycode == Keyboard.KEY_RETURN || keycode == Keyboard.KEY_RIGHT)) {
            selectedSubTab = 0;
        } else if (keycode == Keyboard.KEY_RETURN || keycode == Keyboard.KEY_RIGHT) {
            tabs.get(selectedTab).getSubTabs().get(selectedSubTab).press();
        }
    }
}
