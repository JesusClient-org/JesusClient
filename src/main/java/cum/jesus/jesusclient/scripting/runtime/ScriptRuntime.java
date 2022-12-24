package cum.jesus.jesusclient.scripting.runtime;

import cum.jesus.jesusclient.scripting.runtime.jesusclient.WrapperJesusClient;
import cum.jesus.jesusclient.scripting.runtime.minecraft.client.WrapperMinecraft;

public class ScriptRuntime {
    public static WrapperMinecraft getMinecraft() {
        return WrapperMinecraft.getMinecraft();
    }

    public static WrapperJesusClient getJesusClient() {
        return WrapperJesusClient.getJesusClient();
    }
}
