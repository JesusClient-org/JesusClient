package cum.jesus.jesusclient.injection;

import dev.falsehonesty.asmhelper.ClassTransformationService;

import java.util.Arrays;
import java.util.List;

public final class JesusTransformationService implements ClassTransformationService {
    @Override
    public List<String> transformerClasses() {
        return Arrays.asList("cum.jesus.jesusclient.injection.JesusTransformer");
    }
}
