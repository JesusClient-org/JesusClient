package cum.jesus.jesusclient.injection

import cum.jesus.jesusclient.injection.plugin.injectGuiChat
import cum.jesus.jesusclient.injection.plugin.injectGuiInGame
import cum.jesus.jesusclient.injection.plugin.injectGuiScreen
import cum.jesus.jesusclient.injection.plugin.injectMinecraft
import cum.jesus.jesusclient.script.ScriptManager
import dev.falsehonesty.asmhelper.BaseClassTransformer
import net.minecraft.launchwrapper.LaunchClassLoader

class JesusTransformer : BaseClassTransformer() {
    private var transforming = false

    override fun setup(classLoader: LaunchClassLoader) {
        super.setup(classLoader)

        classLoader.addTransformerExclusion("cum.jesus.") // anything made by me
        classLoader.addTransformerExclusion("file__") // for rhino generated classes
        classLoader.addTransformerExclusion("com.google.gson.")
        classLoader.addTransformerExclusion("org.mozilla.javascript")
        classLoader.addTransformerExclusion("org.mozilla.classfile")
        classLoader.addTransformerExclusion("com.fasterxml.jackson.core.Version")
        classLoader.addTransformerExclusion("dev.falsehonesty.asmhelper.")
        classLoader.addTransformerExclusion("org.fife.")
    }

    override fun makeTransformers() {
        if (transforming) return
        transforming = true

        try {
            injectMinecraft()
            injectGuiChat()
            injectGuiScreen()
            injectGuiInGame()

            ScriptManager.setup()
            ScriptManager.asmPass()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}