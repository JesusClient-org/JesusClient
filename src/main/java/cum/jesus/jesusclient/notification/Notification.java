package cum.jesus.jesusclient.notification;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.font.GlyphPageFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.Sys;

import java.awt.*;

public class Notification {
    private NotificationType type;
    private String title;
    private String message;
    private long start;

    private long fadeIn;
    private long fadeOut;
    private long end;

    public Notification(NotificationType type, String title, String message, int length) {
        this.type = type;
        this.title = title;
        this.message = message;

        fadeIn = 200 * length;
        fadeOut = fadeIn + 500 * length;
        end = fadeOut + fadeIn;
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
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
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void show() {
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render() {
        GlyphPageFontRenderer consolas = GlyphPageFontRenderer.create("Consolas", 16, false, false, false);
        GlyphPageFontRenderer consolasBold = GlyphPageFontRenderer.create("Consolas", 20, true, false, false);

        ScaledResolution res = new ScaledResolution(JesusClient.mc);
        double offset;
        int width = 120;
        int height = 30;
        long time = getTime();

        if (time < fadeIn)
            offset = Math.tanh(time / (double)(fadeIn) * 3.0) * width;
        else if (time > fadeOut)
            offset = (Math.tanh(3.0 - (time-fadeOut) / (double)(end-fadeOut) * 3.0) * width);
        else
            offset = width;

        Color color = new Color(0, 0, 0, 220);
        Color color1 = new Color(200, 0, 0);

        if (type == NotificationType.ERROR) {
            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 127.5)));
            color = new Color(i, 0, 0, 220);
            title = title + " - Error";
        } else if (type == NotificationType.WARNING) {
            title = title + " - Warn";
        }

        drawRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height, res.getScaledWidth(), res.getScaledHeight() - 5, color.getRGB());
        drawRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height, res.getScaledWidth() - offset + 4, res.getScaledHeight() - 5, color1.getRGB());

        consolasBold.drawString(title, (int) (res.getScaledWidth() - offset + 8), res.getScaledHeight() - 2 - height, Color.WHITE.getRGB(), false);
        consolas.drawString(message, (int) (res.getScaledWidth() - offset + 8), res.getScaledHeight() - 15, Color.WHITE.getRGB(), false);
    }
}
