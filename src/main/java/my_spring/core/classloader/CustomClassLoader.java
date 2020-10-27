package my_spring.core.classloader;

import my_spring.core.common.Constant;
import my_spring.core.exception.AppStartUpException;
import my_spring.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomClassLoader extends ClassLoader {


    private static final CustomClassLoader INSTANCE = new CustomClassLoader();

    private CustomClassLoader() {}

    public static CustomClassLoader getInstance() {
        return INSTANCE;
    }

    /**
     * 加载类
     * @param name 要加载的类的完全限定名
     * @return 加载后的 Class 对象
     */
    @Override
    public Class<?> loadClass(String name) {
        return loadClass(name, false);
    }

    /**
     * 加载类
     * @param name 要加载的类的完全限定名
     * @return 加载后的 Class 对象
     */
    @Override
    public Class<?> loadClass(String name, boolean resolve) {
        synchronized (getClassLoadingLock(name)) {
            // 检查类是否被加载过了
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                return c;
            }
            try {
                // 不加载 my_spring 中的类
                if (name.startsWith(Constant.MY_SPRING_PKG_PREFIX)) {
                    throw new ClassNotFoundException();
                }
                // 没被加载过则加载
                c = findClass(name);
            } catch (ClassNotFoundException ignore) {
                // 加载失败，交给线程上线文类加载器
                // 失败情况：my_spring 中的类、JDK内部类
                try {
                    c = Thread.currentThread().getContextClassLoader().loadClass(name);
                } catch (ClassNotFoundException e) {
                    throw new AppStartUpException(e);
                }
            }
            return c;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String classFilePath = IOUtils.getSpringClassPath()
                + name.replace('.', File.separatorChar)
                + Constant.CLASS_SUFFIX;
        try {
            byte[] bytes = Files.readAllBytes(Path.of(classFilePath));
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(String.format("Class %s not found.", name));
        }
    }

}
