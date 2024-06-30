package cum.jesus.jesusclient.command.arguments;

public final class DoubleParser extends ArgumentParser<Double> {
    @Override
    public Double parse(String arg) throws Exception {
        return Double.parseDouble(arg);
    }
}
