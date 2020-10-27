package my_spring.core.processor;

import my_spring.core.container.BeanContainer;

public interface Processor {
    BeanContainer beanContainer = BeanContainer.getBeanContainer();

    Object process(Object bean);
}
