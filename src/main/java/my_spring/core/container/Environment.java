package my_spring.core.container;

import java.util.Properties;

/**
 * 包含环境信息。
 */
public class Environment {

    /** 激活的环境 */
    private final String profile;

    /**
     * 配置的属性值。
     * 启动时会加载 classpath 下的 application.properties 和 application-${profile}.properties 文件。
     */
    private final Properties properties;

    public Environment(String profile, Properties properties) {
        this.profile = profile;
        this.properties = properties;
    }


    public String getProfile() {
        return profile;
    }

    public String getProperty(String key) {
        String property = (String)properties.get(key);
        return property != null ? property : System.getProperty(key);
    }
}
