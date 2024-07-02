package cum.jesus.jesusclient.injection.plugin

import cum.jesus.jesusclient.JesusClient
import cum.jesus.jesusclient.event.EventManager
import cum.jesus.jesusclient.event.EventType
import cum.jesus.jesusclient.event.events.videogame.GameTickEvent
import cum.jesus.jesusclient.event.events.videogame.KeyInputEvent
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.Descriptor
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard


fun injectMinecraft() {
    injectConstructor()
    injectStartGame()
    injectShutdown()
    injectRunTickDispatchKeypresses()
    injectRunTickPre()
}

fun injectConstructor()  = inject {
    className = "net/minecraft/client/Minecraft"
    methodName = "<init>"
    methodDesc = "(Lnet/minecraft/client/main/GameConfiguration;)V"

    at = At(InjectionPoint.RETURN(0))

    codeBlock {
        code {
            JesusClient.init()
        }
    }
}

fun injectStartGame() = inject {
    className = "net/minecraft/client/Minecraft"
    methodName = "startGame"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/gui/GuiIngame",
                "<init>",
                "(Lnet/minecraft/client/Minecraft;)V"
            )
        ),
        before = false
    )

    codeBlock {
        code {
            JesusClient.instance.start();
        }
    }
}

fun injectShutdown() = inject {
    className = "net/minecraft/client/Minecraft"
    methodName = "shutdown"
    methodDesc = "()V"

    at = At(InjectionPoint.HEAD)

    codeBlock {
        code {
            JesusClient.instance.stop()
        }
    }
}

fun injectRunTickDispatchKeypresses() = inject {
    className = "net/minecraft/client/Minecraft"
    methodName = "runTick"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraft/client/Minecraft",
                "dispatchKeypresses",
                "()V"
            )
        ),
        before = false
    )

    codeBlock {
        val currentScreen = shadowField<GuiScreen>()

        code {
            if (JesusClient.isLoaded()) {
                if (Keyboard.getEventKeyState() && currentScreen != null) {
                    EventManager.call(KeyInputEvent(
                        if (Keyboard.getEventKey() == 0) Keyboard.getEventCharacter().code + 256
                        else Keyboard.getEventKey()
                    ))
                }
            }
        }
    }
}


fun injectRunTickPre() = inject {
    className = "net/minecraft/client/Minecraft"
    methodName = "runTick"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraftforge/fml/common/FMLCommonHandler",
                "onPreClientTick",
                "()V"
            )
        ),
    )

    codeBlock {
        code {
            if (JesusClient.isLoaded()) {
                EventManager.call(GameTickEvent(EventType.PRE))
            }
        }
    }
}

fun injectRunTickPost() = inject {
    className = "net/minecraft/client/Minecraft"
    methodName = "runTick"
    methodDesc = "()V"

    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                "net/minecraftforge/fml/common/FMLCommonHandler",
                "onPostClientTick",
                "()V"
            )
        ),
    )

    codeBlock {
        code {
            if (JesusClient.isLoaded()) {
                EventManager.call(GameTickEvent(EventType.POST))
            }
        }
    }
}