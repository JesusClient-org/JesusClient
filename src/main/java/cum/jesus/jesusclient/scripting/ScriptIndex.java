package cum.jesus.jesusclient.scripting;

import cum.jesus.jesusclient.events.*;
import cum.jesus.jesusclient.events.eventapi.EventTarget;
import cum.jesus.jesusclient.scripting.runtime.events.ScriptChatEvent;
import cum.jesus.jesusclient.scripting.runtime.events.ScriptGameTickEvent;
import cum.jesus.jesusclient.scripting.runtime.events.ScriptKeyInputEvent;
import cum.jesus.jesusclient.scripting.runtime.events.ScriptMotionUpdateEvent;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptIndex {
    private ScriptEngine engine;

    public void setScriptEngine(ScriptEngine scriptEngine) {
        engine = scriptEngine;
    }

    public void scriptLoad() {
        try {
            ((Invocable)engine).invokeFunction("scriptLoad");
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onChat(ChatEvent event) {
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
        try {
            ((Invocable)engine).invokeFunction("onRender2D");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException ignored) {
        }
    }

    @EventTarget
    public void onGameTick(GameTickEvent event) {
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
        ScriptKeyInputEvent ev = new ScriptKeyInputEvent(event.getKey());

        try {
            ((Invocable)engine).invokeFunction("onKeyInput", ev);
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onWorldLoad(WorldLoadEvent event) {
        try {
            ((Invocable)engine).invokeFunction("onWorldLoad");
        } catch (NoSuchMethodException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}