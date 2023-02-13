package cum.jesus.jesusclient;

import cum.jesus.jesusclient.utils.Logger;

public class JesusClientNatives {
    public void loadNatives() {
        System.load(JesusClient.INSTANCE.fileManager.cLibrary.getAbsolutePath());
    }

    public native void test();
}
