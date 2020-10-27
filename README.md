# my-spring
一个简单的IoC、AOP框架，通过自定义类加载器支持简单的类隔离，AOP 通过JDK自动代理实现，还有一点关于 Environment 的细节没完成。

- 支持类扫描、自动依赖注入、JavaConfig 形式的 Bean 定义； 
- 使用JDK动态代理实现了AOP的支持，支持指定切点、创建环绕通知； 
- 通过自定义类加载器实现了用户类和框架之间的类隔离。

支持的注解：
- AOP:
  - `@Around` 环绕通知，注释有用法
  - `@Point` 指定切点，注释有用法
  - `@Aspect` 标记一个且切面类，该注解又被 `@Component` 标记过了，所以可以直接使用
- IoC:
  - `@Autowired` 自动注入，仅可以标注类字段，优先byType，有冲突则byName(fieldName)，再不行就失败
  - `@Bean` JavaConfig 形式配置
  - `@Bootstrap` 标记启动类，启动类必须有一个名为 `start` 的方法作为入口
  - `@Component/@Service/@Controller/@Respository` 支持组合注解
  - `@Value` 配置值注入，功能已实现，但 Environment 还没弄，所以还不能从配置文件中获取值
  - `@DynamicValue` 可以动态更新的 @Value，功能已实现，但 Environment 还没弄，所以也是个半成品
  - `@Configuration` 标记配置类
  - `@Conditional` 条件
  
使用：
    主类调用 `Bootstrap.start("package_name");`，扫描包 package_name 下的所有类，并启动（`@Bootstrap`标记的类的 `start` 方法）。
