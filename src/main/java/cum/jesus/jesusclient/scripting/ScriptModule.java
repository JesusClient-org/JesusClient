package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.JesusClient;
import cum.jesus.jesusclient.events.*;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.module.Category;
import cum.jesus.jesusclient.module.Module;
import cum.jesus.jesusclient.module.settings.Setting;
import cum.jesus.jesusclient.scripting.runtime.events.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
import java.util.Objects;

public class ScriptModule extends Module {
    private ScriptEngine engine;

    ScriptModule(String name, String desc, Category category) {
        super(name, desc, category);
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        engine = scriptEngine;

        for (Setting setting : Objects.requireNonNull(JesusClient.INSTANCE.settingManager.getAllSettingsFrom(this.getName()))) {
            engine.put("setting" + setting.getName().replace(" ", ""), setting);
        }
    }

    @Override
    public void onEnable() {
        try {
            ((Invocable)engine).invokeFunction("onEnable");
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            ((Invocable)engine).invokeFunction("onDisable");
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPremiumFeature() {
        return true;
    }

    @EventTarget
    public void onChat(ChatEvent event) {
        if (!isToggled()) return;

        ScriptChatEvent ev = new ScriptChatEvent(event.getEventType(), event.getMessage().getFormattedText(), event.getMessage().getUnformattedText());

        try {
            ((Invocable)engine).invokeFunction("onChat", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!isToggled()) return;

        try {
            ((Invocable)engine).invokeFunction("onRender2D");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
        }
    }

    @EventTarget
    public void onGameTick(GameTickEvent event) {
        if (!isToggled()) return;

        ScriptGameTickEvent ev = new ScriptGameTickEvent(event.getEventType());

        try {
            ((Invocable)engine).invokeFunction("onGameTick", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (!isToggled()) return;

        ScriptMotionUpdateEvent ev = new ScriptMotionUpdateEvent(event.getEventType(), event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch(), event.isOnGround());

        try {
            ((Invocable)engine).invokeFunction("onMotionUpdate", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        ev.apply(event);
    }

    @EventTarget
    public void onKeyInput(KeyInputEvent event) {
        if (!isToggled()) return;

        ScriptKeyInputEvent ev = new ScriptKeyInputEvent(event.getKey());

        try {
            ((Invocable)engine).invokeFunction("onKeyInput", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        if (!isToggled()) return;

        ScriptPacketEvent ev = new ScriptPacketEvent(event.getEventType(), event.getPacket());

        try {
            ((Invocable)engine).invokeFunction("onPacket", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onWorldLoad(WorldLoadEvent event) {
        if (!isToggled()) return;

        try {
            ((Invocable)engine).invokeFunction("onWorldLoad");
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
