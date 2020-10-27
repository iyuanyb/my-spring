package my_spring.core.annotation;

import my_spring.core.container.BeanContainer;

public interface Condition {
    boolean test(BeanContainer container);
}
