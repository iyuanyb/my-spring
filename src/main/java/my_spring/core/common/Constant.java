package my_spring.core.common;

public interface Constant {
    ///////////// 加载类相关 ////////////
    String JAR_SUFFIX = ".jar";
    String CLASS_SUFFIX = ".class";
    int CLASS_SUFFIX_LENGTH = CLASS_SUFFIX.length();
    int FILE_PREFIX_LENGTH = "file:/".length();
    String JAVA_ANNOTATION_PKG_PREFIX = "@java";
    String MY_SPRING_PKG_PREFIX = "my_spring";

    ///////////// 配置文件相关 ////////////
    String APPLICATION_CONFIG_NAME = "application";
    String PROPERTIES_SUFFIX = ".properties";
    String DYNAMIC_VALUES_NAME = "DynamicValues";
    String CONNECTOR = "-";

}
