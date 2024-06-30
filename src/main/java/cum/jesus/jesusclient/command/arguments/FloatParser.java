package cum.jesus.jesusclient.command.arguments;

public final class FloatParser extends ArgumentParser<Float> {
    @Override
    public Float parse(String arg) throws Exception {
        return Float.parseFloat(arg);
    }
}
