package cum.jesus.jesusclient.injection.mixins.client.renderer;

import cum.jesus.jesusclient.module.modules.render.NoBlind;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void disableFire(float p_renderFireInFirstPerson_1_, CallbackInfo ci) {
        if (NoBlind.INSTANCE.isToggled() && NoBlind.INSTANCE.noFire.getObject()) ci.cancel();
    }
}
