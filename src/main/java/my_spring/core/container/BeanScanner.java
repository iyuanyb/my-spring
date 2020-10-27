package my_spring.core.container;


import my_spring.core.annotation.Bootstrap;
import my_spring.core.common.Constant;
import my_spring.core.exception.AppStartUpException;
import my_spring.util.ProcessorUtils;
import my_spring.util.ReflectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;

/**
 * bean 定义信息读取器。
 */
public class BeanScanner {

    /** 自定义类加载器 */
    private static final ClassLoader classLoader = BeanScanner.class.getClassLoader();
    private static final BeanContainer beanContainer = BeanContainer.getBeanContainer();
    
    /**
     * 扫描 basePackage 下的 Bean。
     */
    public static void scanBeanDefinition(String basePackage) {
        basePackage = File.separator + basePackage.replace(".", File.separator);
        Path path = Path.of(ReflectUtils.class.getResource("/").toString().substring(Constant.FILE_PREFIX_LENGTH) + basePackage);
        Set<Class<?>> classSet = new HashSet<>();
        try {
            extractClassFiles(classSet, path, basePackage);
        } catch (Exception e) {
            throw new AppStartUpException(e);
        }
        classSet.forEach(BeanCreator::createBeanDefinition);
        // 处理器
        beanContainer.executeProcessors();
        // 启动
        Object bootstrap = beanContainer.getBeanDefinitionsByAnnotation(Bootstrap.class).get(0).getBean();
        try {
            bootstrap.getClass().getMethod("start").invoke(bootstrap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从指定路径扫描类。
     */
    private static void extractClassFiles(Set<Class<?>> classSet, Path path, String basePackage) throws Exception {
        if (!Files.isDirectory(path)) {
            return;
        }
        Path[] pathArray = Files.list(path).toArray(Path[]::new);
        for (Path p : pathArray) {
            // 目录递归处理
            if (Files.isDirectory(p)) {
                extractClassFiles(classSet, p, basePackage);
            }
            // 处理 .class 文件
            else if (p.toString().endsWith(Constant.CLASS_SUFFIX)) {
                String pStr = p.toString();
                String className = pStr.substring(pStr.indexOf(basePackage) + 1)
                        .replace(File.separatorChar, '.');
                className = className.substring(0, className.length() - Constant.CLASS_SUFFIX_LENGTH);
                Class<?> clazz = classLoader.loadClass(className);
                // 如果标记了 @Component 注解
                if (ReflectUtils.isAnnotatedWithComponent(clazz)) {
                    classSet.add(clazz);
                }
            }
            // 处理 .jar 文件
            else if (p.toString().endsWith(Constant.JAR_SUFFIX)) {
                loadClassesFromJarFile(p.toString(), classSet);
            }
        }
    }

    /**
     * 从 Jar 包中加载被 @Component 注解了的类。
     * @param jarFilePath Jar文件路径
     * @param classSet 如果被 @Component 标记，则将该 Class 对象加入到 classSet
     */
    private static void loadClassesFromJarFile(String jarFilePath, Set<Class<?>> classSet) {
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            jarFile.stream()
                    .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                    .map(jarEntry -> {
                        try {
                            return classLoader.loadClass(jarEntry.getName());
                        } catch (Exception e) {
                            throw new AppStartUpException();
                        }
                    })
                    .filter(ReflectUtils::isAnnotatedWithComponent)
                    .forEach(classSet::add);
        } catch (IOException e) {
            throw new AppStartUpException(e);
        }
    }
}
