package my_spring.util;

public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.equals("");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String getDefaultBeanName(String className) {
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
}
