package cum.jesus.jesusclient.script.languages.js;

import cum.jesus.jesusclient.file.FileManager;
import cum.jesus.jesusclient.script.Script;
import cum.jesus.jesusclient.script.ScriptLoader;
import cum.jesus.jesusclient.script.trigger.Trigger;
import cum.jesus.jesusclient.script.trigger.TriggerType;
import cum.jesus.jesusclient.util.ChatUtils;
import cum.jesus.jesusclient.util.Logger;
import org.jetbrains.annotations.Contract;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.provider.StrongCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public final class JSLoader implements ScriptLoader {
    public static final JSLoader INSTANCE;

    static {
        try {
            INSTANCE = new JSLoader();
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<TriggerType, ConcurrentSkipListSet<Trigger>> triggers = new ConcurrentHashMap<>();
    private final MethodHandle INVOKE_JS_CALL = MethodHandles.lookup().findStatic(
            JSLoader.class,
            "asmInvoke",
            MethodType.methodType(Object.class, Callable.class, Object[].class)
    );

    private Context scriptContext;
    private Scriptable scope;
    private JesusRequire require;
    private Object ASMLib;

    private Stack<Boolean> missingContexts = new Stack<>();

    private JSLoader() throws NoSuchMethodException, IllegalAccessException {
    }

    public Context getScriptContext() {
        return scriptContext;
    }

    public Scriptable getScope() {
        return scope;
    }

    @Override
    public void setup(List<URL> jars) {
        instanceContext(jars);

        pushContext();

        String asmProvidedLibs = FileManager.saveResource("/js/asmProvidedLibs.js", "asm-provided-libs.js", true);

        try {
            scriptContext.evaluateString(scope, asmProvidedLibs, "asmProvidedLibs", 1, null);
        } catch (Throwable e) {
            e.printStackTrace();
            ChatUtils.sendPrefixMessage(e.getMessage());
        } finally {
            popContext();
        }
    }

    @Override
    public void asmSetup() {
        pushContext();

        File asmLibFile = new File(FileManager.resourcesDir, "asm-lib.js");
        FileManager.saveResource("/js/asmLib.js", "asm-lib.js", true);

        try {
            Scriptable returned = require.loadScript("ASMLib", asmLibFile.toURI());

            ASMLib = ScriptableObject.getProperty(returned, "default");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            popContext();
        }
    }

    @Override
    public void asmPass(Script script, URI asmURI) {
        pushContext();

        try {
            Scriptable returned = require.loadScript(script.getName() + "-asm$$", asmURI);

            Object asmFunction = ScriptableObject.getProperty(returned, "default");

            if (!(asmFunction instanceof Function)) {
                Logger.warn("Asm entry for " + script.getName() + " has an invalid export.");
                return;
            }

            ScriptableObject.putProperty(ASMLib, "currentScript", script.getName());
            ((Function) asmFunction).call(scriptContext, scope, scope, new Object[] { ASMLib });
        } catch (Throwable e) {
            Logger.error("Error loading asm entry for " + script.getName());
            e.printStackTrace();
        } finally {
            popContext();
        }
    }

    @Override
    public void entrySetup() {
        pushContext();

        String stdLib = FileManager.saveResource("/js/stdLib.js", "std-lib.js", true);

        try {
            scriptContext.evaluateString(scope, stdLib, "stdLib", 1, null);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            popContext();
        }
    }

    @Override
    public void entryPass(Script script, URI entryURI) {
        pushContext();

        try {
            require.loadScript(script.getName(), entryURI);
        } catch (Throwable e) {
            Logger.error("Error loading " + script.getName());
            e.printStackTrace();
        } finally {
            popContext();
        }
    }

    @Override
    public MethodHandle asmInvokeLookup(Script script, URI functionURI) {
        pushContext();

        try {
            Scriptable returned = require.loadScript(script.getName(), functionURI);
            Callable func = (Callable) ScriptableObject.getProperty(returned, "default");
            return INVOKE_JS_CALL.bindTo(func);
        } catch (Throwable e) {
            Logger.error("Error loading asm function " + functionURI.toString() + " in script" + script.getName());
            e.printStackTrace();

            return MethodHandles.dropArguments(
                    MethodHandles.constant(Object.class, null),
                    0,
                    Object[].class
            );
        } finally {
            popContext();
        }
    }

    @Override
    public void addTrigger(Trigger trigger) {
        triggers.computeIfAbsent(trigger.getTriggerType(), key -> new ConcurrentSkipListSet<>()).add(trigger);
    }

    @Override
    public void removeTrigger(Trigger trigger) {
        ConcurrentSkipListSet<Trigger> ret = triggers.get(trigger.getTriggerType());
        if (ret == null) return;
        ret.add(trigger);
    }

    @Override
    public void clearTriggers() {
        triggers.clear();
    }

    @Override
    public void trigger(Trigger trigger, Object method, Object[] args) {
        pushContext();

        try {
            if (!(method instanceof Function)) throw new IllegalArgumentException("Need to pass a function to register()");

            ((Function) method).call(Context.getCurrentContext(), scope, scope, args);
        } catch (Throwable e) {
            e.printStackTrace();
            removeTrigger(trigger);
            ChatUtils.sendPrefixMessage(e.getMessage());
        } finally {
            popContext();
        }
    }

    @Override
    public void execTriggerType(TriggerType type, Object[] args) {
        ConcurrentSkipListSet<Trigger> ret = triggers.get(type);
        if (ret != null) {
            for (Trigger trigger : ret) {
                trigger.trigger(args);
            }
        }
    }

    public void pushContext(Context context) {
        boolean missingContext = Context.getCurrentContext() == null;

        if (missingContext) {
            try {
                JSContextFactory.INSTANCE.enterContext(context);
            } catch (Throwable e) {
                JSContextFactory.INSTANCE.enterContext();
            }
        }

        missingContexts.push(missingContext);
    }

    public void pushContext() {
        pushContext(scriptContext);
    }

    public void popContext() {
        boolean missingContext = missingContexts.pop();
        if (missingContext) Context.exit();
    }

    private static Object asmInvoke(Callable func, Object[] args) {
        INSTANCE.pushContext();

        Object ret = func.call(INSTANCE.scriptContext, INSTANCE.scope, INSTANCE.scope, args);

        INSTANCE.popContext();
        return ret;
    }

    private void instanceContext(List<URL> files) {
        JSContextFactory.INSTANCE.addAllURLs(files);

        scriptContext = JSContextFactory.INSTANCE.enterContext();
        scope = new ImporterTopLevel(scriptContext);

        UrlModuleSourceProvider sourceProvider = new UrlModuleSourceProvider(Collections.singletonList(FileManager.scriptDir.toURI()), new ArrayList<>());
        StrongCachingModuleScriptProvider scriptProvider = new StrongCachingModuleScriptProvider(sourceProvider);
        require = new JesusRequire(scriptProvider);
        require.install(scope);

        Context.exit();
    }

    public class JesusRequire extends Require {
        public JesusRequire(ModuleScriptProvider scriptProvider) {
            super(scriptContext, scope, scriptProvider, null, null, false);
        }

        public Scriptable loadScript(String cachedName, URI uri) {
            return getExportedModuleInterface(scriptContext, cachedName, uri, null, false);
        }
    }
}
