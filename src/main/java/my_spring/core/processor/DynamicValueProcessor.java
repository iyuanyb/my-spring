package my_spring.core.processor;

import my_spring.core.annotation.DynamicValue;
import my_spring.core.common.Constant;
import my_spring.core.container.BeanContainer;
import my_spring.core.container.Environment;
import my_spring.util.IOUtils;
import my_spring.util.ReflectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class DynamicValueProcessor implements Processor {

    private static class Node {
        private Object bean;
        private Field field;
        private String defaultValue;

        private Node() {}

        static Node create(Object bean, Field field, String defaultValue) {
            Node node = new Node();
            node.bean = bean;
            node.field = field;
            node.defaultValue = defaultValue;
            return node;
        }
    }

    private static final Processor PROCESSOR = new DynamicValueProcessor();

    private static final Map<String, Node> keyToField = new HashMap<>();

    private static final ScheduledExecutorService dynamicValueUpdater =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            });

    static {
        dynamicValueUpdater.scheduleWithFixedDelay(DynamicValueProcessor::update, 0, 1, TimeUnit.SECONDS);
    }

    private DynamicValueProcessor() {
    }

    public static Processor getProcessor() {
        return PROCESSOR;
    }

    @Override
    public Object process(Object bean) {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(DynamicValue.class)) {
                continue;
            }
            String value = field.getAnnotation(DynamicValue.class).value();
            if (value.indexOf(':') != -1) {
                String[] arr = value.split(":");
                keyToField.put(arr[0], Node.create(bean, field, arr[1]));
            } else {
                keyToField.put(value, Node.create(bean, field, null));
            }
        }
        return bean;
    }

    private static final Environment environment = BeanContainer.getBeanContainer().getEnvironment();
    private static void update() {
        Properties properties = new Properties();
        Path path = Paths.get(
                IOUtils.getSpringClassPath() +
                Constant.DYNAMIC_VALUES_NAME +
                Constant.CONNECTOR +
                environment.getProfile() +
                Constant.PROPERTIES_SUFFIX
        );
        try {
            properties.load(Files.newBufferedReader(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        keyToField.forEach((key, node) -> {
            Object val = ReflectUtils.cast(properties.getProperty(key), node.field.getType());
            if (val != null) {
                ReflectUtils.setField(node.bean, node.field, val);
            }
        });
    }
}
