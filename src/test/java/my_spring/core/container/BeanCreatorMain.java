package my_spring.core.container;

import my_spring.core.annotation.*;
import org.junit.jupiter.api.Test;

class BeanCreatorMain {

    @Test
    void createBeanDefinition() {
        BeanCreator.createBeanDefinition(TestClass1.class);
        BeanDefinition beanDefinition = BeanContainer.getBeanContainer().getBeanDefinition("abc");
        System.out.println(beanDefinition);
        System.out.println(beanDefinition.getBean());
        System.out.println(BeanContainer.getBeanContainer().getBeanDefinitionsBySuper(TestClass1.class));
        System.out.println(BeanContainer.getBeanContainer().getBeanDefinitionsByAnnotation(Component.class));
    }

    @Test
    void testCreateBeanDefinition() throws Exception {
        TestClass2 configClass = new TestClass2();
        BeanCreator.createBeanDefinition(configClass, TestClass2.class.getMethod("student"));
        BeanDefinition beanDefinition = BeanContainer.getBeanContainer().getBeanDefinition("student");
        System.out.println(beanDefinition);
        System.out.println(beanDefinition.getBean());
    }

    @Test
    void createBeanByCreatorMethod() throws Exception {
        System.out.println(TestClass1.class.getDeclaredConstructor().newInstance());
    }
}

@Service(name = "abc", lazyInit = true, scope = BeanScope.PROTOTYPE, initMethod = "init")
class TestClass1 {
    public void init() {
        System.out.println("init...");
    }
}

@Configuration
class TestClass2 {
    static class Student {
        String name;
        String id;

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }
    @Bean(scope = BeanScope.PROTOTYPE)
    public Student student() {
        Student student = new Student();
        student.name = "Alice";
        student.id = "A1001";
        return student;
    }
}