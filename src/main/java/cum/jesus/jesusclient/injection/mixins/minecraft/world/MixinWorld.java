package cum.jesus.jesusclient.injection.mixins.minecraft.world;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.module.modules.render.NoSlimes;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({World.class})
public class MixinWorld {
    @Inject(method = "spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V", at = @At("HEAD"), cancellable = true)
    private void removeSlimeParticles(EnumParticleTypes type, double p_spawnParticle_2_, double p_spawnParticle_3_, double p_spawnParticle_4_, double p_spawnParticle_5_, double p_spawnParticle_6_, double p_spawnParticle_7_, int[] p_spawnParticle_8_, CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        if (type == EnumParticleTypes.SLIME && NoSlimes.INSTANCE.isToggled() && SkyblockUtils.onSkyblock) {
            if (NoSlimes.INSTANCE.homeOnly.getObject() && !SkyblockUtils.onPrivateIsland) return;
            ci.cancel();
        }
    }
}
