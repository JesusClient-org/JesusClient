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

    const JesusClientClass = Java.type("cum.jesus.jesusclient.JesusClient");
    global.JesusClient = JesusClientClass.instance; // need to do this cuz it's not INSTANCE

    global.ConfigManager = JesusClient.configManager;
    global.SettingManager = JesusClient.settingManager;
    global.CommandHandler = JesusClient.commandHandler;
    global.ModuleHandler = JesusClient.moduleHandler;
    global.Config = JesusClient.clientConfig;

    loadClass("cum.jesus.jesusclient.file.FileManager");

    loadClass("cum.jesus.jesusclient.script.lib.EventLib");

    loadInstance("cum.jesus.jesusclient.script.languages.js.JSRegister", "TriggerRegister");

    loadClass("cum.jesus.jesusclient.script.trigger.BasicTrigger");
    loadClass("cum.jesus.jesusclient.script.trigger.Trigger");

    global.Priority = Trigger.Priority;

    // returns true on success
    global.cancel = event => {
        try {
            EventLib.cancel(event);
            return true;
        } catch (e) {
            return false;
        }
    };

    global.register = (type, method) => TriggerRegister.register(type, method);
})(this);