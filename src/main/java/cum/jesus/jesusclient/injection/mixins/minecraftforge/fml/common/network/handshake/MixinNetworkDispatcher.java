package cum.jesus.jesusclient.injection.mixins.minecraftforge.fml.common.network.handshake;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.utils.Logger;
import cum.jesus.jesusclient.utils.SkyblockUtils;
import cum.jesus.jesusclient.utils.Utils;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkDispatcher.class})
public class MixinNetworkDispatcher {
    @Shadow @Final public NetworkManager manager;

    @Shadow @Final private Side side;

    @Inject(method = "completeClientSideConnection", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", shift = At.Shift.BEFORE), remap = false)
    private void checkServerLogin(CallbackInfo ci) {
        boolean isLocal = this.manager.isLocalChannel();

        Logger.debug("server login");

        Utils.onHypixel = (!isLocal && (JesusClient.mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel") ? JesusClient.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel") : false));
    }

    @Inject(method = "disconnect", at = @At(value = "HEAD"), remap = false)
    private void checkServerDisconnect(CallbackInfo ci) {
        if (this.side == Side.CLIENT) {
            SkyblockUtils.onSkyblock = false;
            SkyblockUtils.onPrivateIsland = false;
            SkyblockUtils.inDungeon = false;
            Utils.onHypixel = false;
        }
    }

    @Inject(method = "close", at = @At(value = "HEAD"), remap = false)
    private void checkServerClose(CallbackInfo ci) {
        if (this.side == Side.CLIENT) {
            SkyblockUtils.onSkyblock = false;
            SkyblockUtils.onPrivateIsland = false;
            SkyblockUtils.inDungeon = false;
            Utils.onHypixel = false;
        }
    }
}
