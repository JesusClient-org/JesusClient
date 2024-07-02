package cum.jesus.jesusclient.injection.plugin

import cum.jesus.jesusclient.JesusClient
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock
import dev.falsehonesty.asmhelper.dsl.inject
import net.minecraft.client.Minecraft

fun injectGuiScreen() {
    injectSendChatMessage()
}

fun injectSendChatMessage() = inject {
    className = "net/minecraft/client/gui/GuiScreen"
    methodName = "sendChatMessage"
    methodDesc = "(Ljava/lang/String;Z)V"

    at = At(InjectionPoint.HEAD)

    codeBlock {
        val mc = shadowField<Minecraft>()

        val local1 = shadowLocal<String>()

        code {
            if (JesusClient.isLoaded() && local1.startsWith(JesusClient.instance.config.commandPrefix.value) && local1.length > JesusClient.instance.config.commandPrefix.value.length) {
                if (JesusClient.instance.commandHandler.execute(local1)) {
                    mc.ingameGUI.chatGUI.addToSentMessages(local1)
                }

                CodeBlock.methodReturn()
            }
        }
    }
}