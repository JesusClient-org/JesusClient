package cum.jesus.jesusclient.injection.mixins.minecraft.entity;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.NoBlind;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityLivingBase.class})
public abstract class MixinEntityLivingBase extends MixinEntity {
    @Inject(method = "isPotionActive(Lnet/minecraft/potion/Potion;)Z", at = @At(value = "HEAD"), cancellable = true)
    private void hookAntiNausea(Potion potion, CallbackInfoReturnable<Boolean> cir) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        if (potion == Potion.confusion && (NoBlind.INSTANCE.isToggled()) && NoBlind.INSTANCE.nausea.getObject()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
