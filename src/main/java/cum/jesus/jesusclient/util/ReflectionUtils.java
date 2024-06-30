package cum.jesus.jesusclient.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public final class ReflectionUtils {
    public static Object instantiateInnerClass(Class<?> klass, Object parent) {
        try {
            if (Modifier.isStatic(klass.getModifiers())) {
                Constructor<?> constructor = klass.getDeclaredConstructor();
                if (!constructor.isAccessible()) constructor.setAccessible(true);
                return constructor.newInstance();
            } else {
                Constructor<?> constructor = klass.getDeclaredConstructor(parent.getClass());
                if (!constructor.isAccessible()) constructor.setAccessible(true);
                return constructor.newInstance();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while instantiating inner class!");
        }
    }
}
