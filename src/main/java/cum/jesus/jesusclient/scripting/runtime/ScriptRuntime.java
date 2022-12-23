package cum.jesus.jesusclient.scripting.runtime;

import cum.jesus.jesusclient.scripting.runtime.minecraft.client.WrapperMinecraft;

public class ScriptRuntime {
    public static WrapperMinecraft getMinecraft() {
        return WrapperMinecraft.getMinecraft();
    }
}
