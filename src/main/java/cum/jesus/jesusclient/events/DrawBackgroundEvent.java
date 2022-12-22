package cum.jesus.jesusclient.events;

import cum.jesus.jesusclient.events.eventapi.events.Event;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class DrawBackgroundEvent implements Event {
    public final GuiScreen gui;
    private final int mouseX;
    private final int mouseY;

    public DrawBackgroundEvent(GuiScreen gui) {
        this.gui = gui;

        ScaledResolution scaledresolution = new ScaledResolution(gui.mc);
        int scaledWidth = scaledresolution.getScaledWidth();
        int scaledHeight = scaledresolution.getScaledHeight();
        this.mouseX = Mouse.getX() * scaledWidth / gui.mc.displayWidth;
        this.mouseY = scaledHeight - Mouse.getY() * scaledHeight / gui.mc.displayHeight - 1;
    }

    public int getMouseX() {
        return this.mouseX;
    }

    public int getMouseY() {
        return this.mouseY;
    }
}
