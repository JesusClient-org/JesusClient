package cum.jesus.jesusclient.script.languages.js;

import cum.jesus.jesusclient.JesusClient;
import net.minecraft.launchwrapper.Launch;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mozilla.javascript.Context.EMIT_DEBUG_OUTPUT;
import static org.mozilla.javascript.Context.FEATURE_LOCATION_INFORMATION_IN_ERROR;

public final class JSContextFactory extends ContextFactory {
    public static final JSContextFactory INSTANCE = new JSContextFactory();

    private ModifiedURLClassLoader classLoader = new ModifiedURLClassLoader();
    private boolean optimize = true;

    private JSContextFactory() {

    }

    public boolean getOptimize() {
        return optimize;
    }

    public void setOptimize(boolean optimize) {
        this.optimize = optimize;
    }

    public void addAllURLs(List<URL> urls) {
        classLoader.addAllURLs(urls);
    }

    @Override
    protected void onContextCreated(Context cx) {
        super.onContextCreated(cx);

        cx.setDebugOutputPath(new File(".", "DEBUG"));
        cx.setApplicationClassLoader(classLoader);
        cx.setOptimizationLevel(optimize ? 9 : 0);
        cx.setLanguageVersion(Context.VERSION_ES6);
        cx.setErrorReporter(new JSErrorReporter());

        WrapFactory wrapFactory = new WrapFactory() {
            @Override
            public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
                if (obj instanceof Collection) {
                    return super.wrap(cx, scope, ((Collection<?>) obj).toArray(), staticType);
                }

                return super.wrap(cx, scope, obj, staticType);
            }
        };
        wrapFactory.setJavaPrimitiveWrap(false);

        cx.setWrapFactory(wrapFactory);
    }

    @Override
    protected boolean hasFeature(Context cx, int featureIndex) {
        if (featureIndex == FEATURE_LOCATION_INFORMATION_IN_ERROR) return true;
        else if (featureIndex == EMIT_DEBUG_OUTPUT) return JesusClient.isLoaded() && JesusClient.instance.devMode;

        return super.hasFeature(cx, featureIndex);
    }

    private class ModifiedURLClassLoader extends URLClassLoader {
        private List<URL> sources = new ArrayList<>();

        public ModifiedURLClassLoader() {
            super(new URL[0], ModifiedURLClassLoader.class.getClassLoader());
        }

        private void addAllURLs(List<URL> urls) {
            urls.stream()
                    .filter(url -> !sources.contains(url))
                    .forEach(this::addURL);
        }

        @Override
        protected void addURL(URL url) {
            super.addURL(url);
            sources.add(url);
        }
    }
}
