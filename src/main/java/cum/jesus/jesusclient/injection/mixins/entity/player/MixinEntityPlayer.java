package cum.jesus.jesusclient.injection.mixins.entity.player;

import cum.jesus.jesusclient.injection.mixins.entity.MixinEntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {
}
