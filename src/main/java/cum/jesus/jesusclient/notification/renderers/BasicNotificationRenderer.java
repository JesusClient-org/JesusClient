package cum.jesus.jesusclient.notification.renderers;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.notification.Notification;
import cum.jesus.jesusclient.notification.NotificationManager;
import cum.jesus.jesusclient.notification.NotificationRenderer;
import cum.jesus.jesusclient.notification.NotificationType;
import cum.jesus.jesusclient.util.font.GlyphPageFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public final class BasicNotificationRenderer implements NotificationRenderer {
    public static final BasicNotificationRenderer INSTANCE = new BasicNotificationRenderer();

    private LinkedBlockingQueue<Notification> pending = new LinkedBlockingQueue<>();
    private Notification current = null;

    private boolean finished = false;
    private NotificationRenderer newRenderer = null;

    private GlyphPageFontRenderer font = GlyphPageFontRenderer.create("Calibri", 16, false, false, false);
    private GlyphPageFontRenderer titleFont = GlyphPageFontRenderer.create("Calibri", 20, true, false, false);

    private Color mainColor = new Color(40, 40, 40, 220);
    private Color titleBarColor = new Color(200, 0, 0);

    private BasicNotificationRenderer() {

    }

    @Override
    public void show(Notification notification) {
        if (!finished) pending.add(notification);
    }

    @Override
    public void finish(NotificationRenderer newRenderer) {
        if (newRenderer == this) return;

        finished = true;
        this.newRenderer = newRenderer;
    }

    @Override
    public void clear() {
        pending.clear();
        finished = false;
    }

    @Override
    public void update() {
        if (current != null && !current.isShown()) {
            current = null;
        }

        if (current == null) {
            if (!pending.isEmpty()) {
                current = pending.poll();
                current.show();
            } else if (finished) {
                NotificationManager.setRendererReal(newRenderer);
            }
        }
    }

    @Override
    public void render() {
        update();

        if (current != null) {
            render(current);
        }
    }

    private void render(Notification notification) {
        ScaledResolution res = new ScaledResolution(JesusClient.mc);
        double offset;
        int width = 130;
        int height = 30;

        long time = notification.getTime();

        String[] lines = notification.getMessage().split("\n");
        for (String line : lines) {
            if (font.getStringWidth(line) + 8 > width) {
                width = font.getStringWidth(line) + 12;
            }

            height += font.getFontHeight() + 2;
        }

        if (time < notification.getFadeIn()) {
            offset = Math.tanh(time / (double) notification.getFadeIn() * 3.0) * width;
        } else if (time > notification.getFadeOut()) {
            offset = Math.tanh(3.0 - (time - notification.getFadeOut()) / (double) (notification.getEnd() - notification.getFadeOut()) * 3.0) * width;
        } else {
            offset = width;
        }

        Color color = mainColor;

        if (notification.getType() == NotificationType.ERROR) {
            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 127.5)));
            color = new Color(i, 0, 0, 220);
        }

        drawRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height, res.getScaledWidth(), res.getScaledHeight() - 5, color.getRGB());
        drawRect(res.getScaledWidth() - offset, res.getScaledHeight() - 5 - height, res.getScaledWidth() - offset + 4, res.getScaledHeight() - 5, titleBarColor.getRGB());

        titleFont.drawString(notification.getTitle(), (int) (res.getScaledWidth() - offset + 8), res.getScaledHeight() - 2 - height, Color.WHITE.getRGB(), false);
        ArrayUtils.reverse(lines);

        int i = 20;
        for (String line : lines) {
            font.drawString(line, (int) (res.getScaledWidth() - offset + 8), res.getScaledHeight() - i, Color.WHITE.getRGB(), false);
            i += font.getFontHeight() + 2;
        }
    }

    static void drawRect(double left, double top, double right, double bottom, int color) {
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
}
