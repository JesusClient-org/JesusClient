package cum.jesus.jesusclient.injection.mixins.minecraft.client.gui;

import cum.jesus.jesusclient.remote.Updater;
import net.minecraft.client.gui.GuiMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {
    @Shadow
    private String splashText;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initMainMenu(CallbackInfo ci) {
        if(Updater.update != null && !Updater.shouldUpdate) {
            splashText = "Jesus Client is outdated!";
        } else {
            splashText = "Jesus Client on top!";
        }
    }
}
