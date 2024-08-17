package cum.jesus.jesusclient.util;

import cum.jesus.jesusclient.script.languages.js.JSContextFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public final class ModifiedURLClassLoader extends URLClassLoader {
    private List<URL> sources = new ArrayList<>();

    public ModifiedURLClassLoader() {
        super(new URL[0], ModifiedURLClassLoader.class.getClassLoader());
    }

    public void addAllURLs(List<URL> urls) {
        urls.stream()
                .filter(url -> !sources.contains(url))
                .forEach(this::addURL);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
        sources.add(url);
    }
}