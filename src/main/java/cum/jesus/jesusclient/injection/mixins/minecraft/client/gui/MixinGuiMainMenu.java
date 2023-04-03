package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.remote.Updater;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
@SideOnly(Side.CLIENT)
public class MixinGuiMainMenu {
    private static final ResourceLocation minecraftTitleTextures = Utils.getPseudoResourceLocation("replace/childporn.png");

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
}
