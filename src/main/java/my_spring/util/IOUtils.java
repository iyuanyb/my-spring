package my_spring.util;

import my_spring.core.common.Constant;

/**
 * IO 工具类。
 */
public class IOUtils {

    private IOUtils() {
        throw new UnsupportedOperationException();
    }

    public static String getSpringClassPath() {
        return IOUtils.class.getResource("/").toString().substring(Constant.FILE_PREFIX_LENGTH);
    }
}
