package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.remote.Updater;
import cum.jesus.jesusclient.utils.RenderUtils;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import javax.imageio.ImageIO;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {
    @Shadow
    private String splashText;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initMainMenu(CallbackInfo ci) {
        if (!JesusClient.clientLoaded) return;

        if(Updater.update != null && !Updater.shouldUpdate) {
            splashText = "Jesus Client is outdated!";
        } else if (JesusClient.INSTANCE.blacklisted) {
            splashText = "L";
        } else {
            splashText = "Jesus Client on top!";
        }
    }

    private static File childPorn;

    @Inject(method = "drawScreen", at = @At(value = "RETURN"))
    public void drawScreen(CallbackInfo ci) {
        float width = 374.4f;
        float height = 113.88f;

        try {
            childPorn = new File(MixinGuiMainMenu.class.getClassLoader().getResource("assets/jesusclient/childporn.jpg").toURI());
        } catch (URISyntaxException e) {
            childPorn = null;
            throw new RuntimeException(e);
        }

        if (childPorn == null) return;

        ScaledResolution res = new ScaledResolution(JesusClient.mc);
        float x = (res.getScaledWidth() / 2f) - (width / 2);
        float y = 5;

        try {
            RenderUtils.drawImage(JesusClient.mc.getTextureManager().getDynamicTextureLocation("jesusclient", new DynamicTexture(javax.imageio.ImageIO.read(childPorn))), x, y, width, height, 100);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
