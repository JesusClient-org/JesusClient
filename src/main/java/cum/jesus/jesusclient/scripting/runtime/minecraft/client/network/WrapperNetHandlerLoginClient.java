package cum.jesus.jesusclient.scripting.runtime.minecraft.client.network;

import cum.jesus.jesusclient.scripting.runtime.minecraft.util.WrapperIChatComponent;
import net.minecraft.client.network.NetHandlerLoginClient;

public class WrapperNetHandlerLoginClient {
    private NetHandlerLoginClient real;

    public WrapperNetHandlerLoginClient(NetHandlerLoginClient var1) {
        this.real = var1;
    }

    public NetHandlerLoginClient unwrap() {
        return this.real;
    }

    public void onDisconnect(WrapperIChatComponent var1) {
        this.real.onDisconnect(var1.unwrap());
    }
}
