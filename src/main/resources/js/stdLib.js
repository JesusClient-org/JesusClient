(function(global) {
    const Class = Java.type("java.lang.Class");

    const getClassName = path => path.substring(path.lastIndexOf('.') + 1)

    function getClass(path) {
        const clazz = Java.type(path);
        if (clazz.class instanceof Class)
            return clazz;
        throw new Error(`moduleProvidedLibs: Could not load class "${path}"`);
    }

    function loadClass(path, className = getClassName(path)) {
        global[className] = getClass(path);
    }

    function loadInstance(path, className = getClassName(path)) {
        global[className] = getClass(path).INSTANCE;
    }
})(this);