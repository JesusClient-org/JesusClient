package cum.jesus.jesusclient.injection.plugin

import cum.jesus.jesusclient.JesusClient
import cum.jesus.jesusclient.event.EventManager
import cum.jesus.jesusclient.event.events.videogame.Render2DEvent
import cum.jesus.jesusclient.notification.NotificationManager
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import net.minecraft.client.gui.GuiIngame

fun injectGuiInGame() {
    injectRenderTooltip()
}

fun injectRenderTooltip() = inject {
    className = "net/minecraft/client/gui/GuiIngame"
    methodName = "renderTooltip"
    methodDesc = "(Lnet/minecraft/client/gui/ScaledResolution;F)V"

    at = At(InjectionPoint.RETURN())

    codeBlock {
        code {
            if (JesusClient.isLoaded()) {
                EventManager.call(Render2DEvent())
                NotificationManager.render()
            }
        }
    }
}