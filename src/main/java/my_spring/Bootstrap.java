package my_spring;

import my_spring.core.classloader.CustomClassLoader;

public class Bootstrap {

    private static final CustomClassLoader customClassLoader = CustomClassLoader.getInstance();

    public static void start(String basePackage) {
        try {
            customClassLoader.loadClass("my_spring.core.container.BeanScanner")
                    .getMethod("scanBeanDefinition", String.class)
                    .invoke(null, basePackage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
