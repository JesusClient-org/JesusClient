package cum.jesus.jesusclient.injection.plugin

import cum.jesus.jesusclient.JesusClient
import cum.jesus.jesusclient.module.modules.render.ClickGUIModule
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock
import dev.falsehonesty.asmhelper.dsl.code.CodeBlock.Companion.methodReturn
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField

fun injectGuiChat() {
    injectInitGui()
    injectSendAutocompleteRequest()
}

fun injectInitGui() = inject {
    className = "net/minecraft/client/gui/GuiChat"
    methodName = "initGui"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiTextField",
                "setMaxStringLength",
                "(I)V"
            )
        ),
        before = false
    )

    codeBlock {
        val inputField = shadowField<GuiTextField>()

        code {
            inputField.maxStringLength = 50000
        }
    }
}

fun injectSendAutocompleteRequest() = inject {
    className = "net/minecraft/client/gui/GuiChat"
    methodName = "sendAutocompleteRequest"
    methodDesc = "(Ljava/lang/String;Ljava/lang/String)V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/network/NetHandlerPlayClient",
                "addToSendQueue",
                "(Lnet/minecraft/network/Packet;)V"
            )
        )
    )

    codeBlock {
        var waitingOnAutocomplete = shadowField<Boolean>()
        val onAutocompleteResponse = shadowMethod<Unit, Array<String>>()

        val local1 = shadowLocal<String>() // message

        code {
            if (JesusClient.isLoaded() && local1.startsWith(ClickGUIModule.INSTANCE.commandPrefix.value)) {
                val ls = JesusClient.instance.commandHandler.autoComplete(local1).toTypedArray()

                if (ls.isNotEmpty() && !local1.lowercase().endsWith(ls[ls.size - 1].lowercase())) {
                    waitingOnAutocomplete = true
                    onAutocompleteResponse(ls)
                    methodReturn()
                }
            }
        }
    }
}