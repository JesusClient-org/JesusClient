package cum.jesus.jesusclient.gui.tabgui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.HudRenderer;
import cum.jesus.jesusclient.utils.RenderUtils;
import cum.jesus.jesusclient.utils.font.GlyphPageFontRenderer;
import me.superblaubeere27.clickgui.IRenderer;
import net.minecraft.client.gui.FontRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static cum.jesus.jesusclient.gui.tabgui.TabGui.*;
import static org.lwjgl.opengl.GL11.*;

public class Tab<T> {
    @NotNull
    private List<SubTab<T>> subTabs = new ArrayList<>();
    private String text;

    private GlyphPageFontRenderer consolas;

    public Tab(String text) {
        this.text = text;

        consolas = GlyphPageFontRenderer.create("Consolas", 15, false, false, false);
    }

    public void addSubTab(SubTab<T> subTab) {
        subTabs.add(subTab);
    }

    @NotNull
    public List<SubTab<T>> getSubTabs() {
        return subTabs;
    }

    public void renderSubTabs(int x, int y, int selectedSubTab) {

        glTranslated(x, y, 0);

        int height = (consolas.getFontHeight() + OFFSET) * subTabs.size();

        int width = 0;

        for (SubTab<T> tab : subTabs) {
            if (consolas.getStringWidth(tab.getText()) > width) {
                width = consolas.getStringWidth(tab.getText());
            }
        }

        width += 2 + 2;

        drawRect(GL_QUADS, 0, 0, width, height, BACKGROUND.getRGB());

        glLineWidth(1.0f);
        drawRect(GL_LINE_LOOP, 0, 0, width, height, BORDER.getRGB());

        int offset = 2;

        int i = 0;

        for (SubTab<T> tab : subTabs) {
            if (selectedSubTab == i) {
                RenderUtils.drawRect(GL_QUADS, 0, offset - 2, width, offset + consolas.getFontHeight() + OFFSET - 1, SELECTED);
            }

            consolas.drawString(tab.getText(), 2, offset, FOREGROUND.getRGB(), false);
            offset += consolas.getFontHeight() + OFFSET;
            i++;
        }

        glTranslated(-x, -y, 0);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
