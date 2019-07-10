package jetbooster.di;

import java.util.HashMap;
import java.util.Map;

public class Injectors {

    public static Map<Class<?>, Injector> injectorMap = new HashMap<>();

    static {
        try {
            Class.forName("jetbooster.di.InjectorMap");
        } catch (ClassNotFoundException e) {

        }
    }

    public static <T> void register(Class<T> clazz, Injector<T> injector) {
        injectorMap.put(clazz, injector);
    }

    public static void inject(Object obj) {
        if (obj == null)
            return;
        Injector injector = injectorMap.get(obj.getClass());
        if (injector == null)
            return;
        injector.inject(obj);
    }
}
