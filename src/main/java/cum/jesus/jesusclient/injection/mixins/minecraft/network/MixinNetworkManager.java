package cum.jesus.jesusclient.injection.mixins.minecraft.network;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.PacketEvent;
import cum.jesus.jesusclient.events.eventapi.EventManager;
import cum.jesus.jesusclient.events.eventapi.types.EventType;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetworkManager.class})
public class MixinNetworkManager {
    @Inject(method = "channelRead0", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void packetReceived(ChannelHandlerContext p_channelRead0_1_, Packet packet, CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        PacketEvent event = new PacketEvent(EventType.RECIEVE, packet);
        EventManager.call(event);

        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(Packet packetIn, CallbackInfo ci) {
        if (!JesusClient.clientLoaded || JesusClient.INSTANCE.blacklisted) return;

        PacketEvent event = new PacketEvent(EventType.SEND, packetIn);
        EventManager.call(event);

        if (event.isCancelled()) ci.cancel();
    }
}
