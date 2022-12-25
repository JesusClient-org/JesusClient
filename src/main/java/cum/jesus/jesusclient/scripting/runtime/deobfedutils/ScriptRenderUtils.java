package cum.jesus.jesusclient.scripting.runtime.deobfedutils;

import cum.jesus.jesusclient.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.awt.*;

public class ScriptRenderUtils {
    public static void setColor(Color color) {
        RenderUtils.setColor(color);
    }

    public static void setColor(int rgba) {
        RenderUtils.setColor(rgba);
    }

    public static void drawRect(int glFlag, int left, int top, int right, int bottom, int color) {
        RenderUtils.drawRect(glFlag, left, top, right, bottom, color);
    }

    public static void drawRect(int mode, double left, double top, double right, double bottom, int color) {
        RenderUtils.drawRect(mode, left, top, right, bottom, color);
    }

    public static void drawImage(ResourceLocation location, float x, float y, float width, float height, float opacity) {
        RenderUtils.drawImage(location, x, y, width, height, opacity);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
        RenderUtils.drawTexturedRect(x, y, width, height, 0f, 1f, 0f , 1f, filter);
    }

    public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        RenderUtils.drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, filter);
    }

    public static void drawTexturedRectNoBlend(float x, float y, float width, float height, float uMin, float uMax, float vMin, float vMax, int filter) {
        RenderUtils.drawTexturedRectNoBlend(x, y, width, height, uMin, uMax, vMin, vMax, filter);
    }
}
