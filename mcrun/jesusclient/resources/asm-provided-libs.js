let global = this;

global.Java = {
    type: klass => Packages[klass]
};

global.Logger = Java.type("cum.jesus.jesusclient.util.Logger");

global.print = (text) => {
    if (text == null) {
        text = "null";
    } else if (text == undefined) {
        text = "undefined";
    }

    Logger.info(text);
}