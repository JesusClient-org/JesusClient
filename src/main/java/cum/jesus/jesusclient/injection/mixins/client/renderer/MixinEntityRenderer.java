package cum.jesus.jesusclient.injection.mixins.client.renderer;

import cum.jesus.jesusclient.module.modules.combat.Reach;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;distanceTo(Lnet/minecraft/util/Vec3;)D", ordinal = 2))
    private double distanceTo(Vec3 instance, Vec3 vec) {
        return (Reach.INSTANCE.isToggled() && instance.distanceTo(vec) <= Reach.INSTANCE.reachAmount.getObject() ? 2.9000000953674316D : (instance.distanceTo(vec)));
    }
}