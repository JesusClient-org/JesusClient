package cum.jesus.jesusclient.injection.mixins.client.renderer;

import cum.jesus.jesusclient.module.modules.combat.Reach;
import cum.jesus.jesusclient.module.modules.render.NoBlind;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
    @Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
    public boolean removeBlindness(EntityLivingBase instance, Potion potionIn) {
        return !NoBlind.INSTANCE.isToggled();
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D", ordinal = 2))
    private double distanceTo(Vec3 instance, Vec3 vec) {
        return (Reach.INSTANCE.isToggled() && instance.distanceTo(vec) <= Reach.INSTANCE.reachAmount.getObject() ? 2.9000000953674316D : (instance.distanceTo(vec)));
    }
}