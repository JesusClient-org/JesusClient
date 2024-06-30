package cum.jesus.jesusclient.command.arguments;

public final class IntegerParser extends ArgumentParser<Integer> {
    @Override
    public Integer parse(String arg) throws Exception {
        return Integer.parseInt(arg);
    }
}
