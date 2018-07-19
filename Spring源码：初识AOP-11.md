前言：IOC搞了这么久，终于来到了AOP。

## 一、一个简单的例子

```java
public class TestBean {

    private String testStr = "testStr";

    public String getTestStr() {
        return testStr;
    }

    public void setTestStr(String testStr) {
        this.testStr = testStr;
    }
    
    public void test() {
        System.out.println("test");
    }
}

@Aspect
public class AspectJTest {

    @Pointcut("execution(* *.test(..))")
    public void test() {
    }

    @Before("test()")
    public void beforeTest() {
        System.out.println("beforeTest");
    }

    @After("test()")
    public void afterTest() {
        System.out.println("afterTest");
    }

    @Around("test()")
    public Object aroundTest(ProceedingJoinPoint p) {
        System.out.println("before1");
        Object o = null;
        try {
            o = p.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("after1");
        return o;
    }
}

public static void main(String[] args) {
    ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext("xiaokui1/xiaokui.xml");
    TestBean testBean = (TestBean)application.getBean("testBean");
    testBean.test();
}



控制台会输出一下内容：
before1
beforeTest
test
after1
afterTest
```

配置文件如下

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       		http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
       		http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">
    <aop:aspectj-autoproxy/>

    <bean id="testBean" class="xiaokui1.TestBean"/>

    <bean class="xiaokui1.AspectJTest"/>
</beans>
```



这就是Spring AOP带来的神奇功效了。Spring实现了对所有类的test方法进行增加，是辅助功能可以独立于核心业务之外，方便与程序的扩展和解耦。

那么，Spring究竟是如何实现AOP的呢？首先我们知道，Spring是否支持主键的AOP是由一个配置文件控制的，也就是`<aop:aspectj-autoproxy/>`，当在配置文件中声明了这句配置的时候，Spring就会支持注解的AOP，那么我们的分析就从这句开始。

## 二、动态AOP自定义标签

之前讲过Spring中的自定义注解，如果声明了自定义注解，那么就一定会在程序中的某个地方注册了对应的解析器。我们搜索aop模块代码，发现了注册代码

```java
/**
 * {@code NamespaceHandler} for the {@code aop} namespace.
 *
 * <p>Provides a {@link org.springframework.beans.factory.xml.BeanDefinitionParser} for the
 * {@code &lt;aop:config&gt;} tag. A {@code config} tag can include nested
 * {@code pointcut}, {@code advisor} and {@code aspect} tags.
 *
 * <p>The {@code pointcut} tag allows for creation of named
 * {@link AspectJExpressionPointcut} beans using a simple syntax:
 * <pre class="code">
 * &lt;aop:pointcut id=&quot;getNameCalls&quot; expression=&quot;execution(* *..ITestBean.getName(..))&quot;/&gt;
 * </pre>
 *
 * <p>Using the {@code advisor} tag you can configure an {@link org.springframework.aop.Advisor}
 * and have it applied to all relevant beans in you {@link org.springframework.beans.factory.BeanFactory}
 * automatically. The {@code advisor} tag supports both in-line and referenced
 * {@link org.springframework.aop.Pointcut Pointcuts}:
 *
 * <pre class="code">
 * &lt;aop:advisor id=&quot;getAgeAdvisor&quot;
 *     pointcut=&quot;execution(* *..ITestBean.getAge(..))&quot;
 *     advice-ref=&quot;getAgeCounter&quot;/&gt;
 *
 * &lt;aop:advisor id=&quot;getNameAdvisor&quot;
 *     pointcut-ref=&quot;getNameCalls&quot;
 *     advice-ref=&quot;getNameCounter&quot;/&gt;</pre>
 */
public class AopNamespaceHandler extends NamespaceHandlerSupport {

	/**
	 * Register the {@link BeanDefinitionParser BeanDefinitionParsers} for the
	 * '{@code config}', '{@code spring-configured}', '{@code aspectj-autoproxy}'
	 * and '{@code scoped-proxy}' tags.
	 */
	public void init() {
		// In 2.0 XSD as well as in 2.1 XSD.
		registerBeanDefinitionParser("config", new ConfigBeanDefinitionParser());
		registerBeanDefinitionParser("aspectj-autoproxy", new AspectJAutoProxyBeanDefinitionParser());
		registerBeanDefinitionDecorator("scoped-proxy", new ScopedProxyBeanDefinitionDecorator());

		// Only in 2.0 XSD: moved to context namespace as of 2.1
		registerBeanDefinitionParser("spring-configured", new SpringConfiguredBeanDefinitionParser());
	}

}
```

我们可以得知，在解析配置文件的时候，一旦遇到aspectj-autoproxy配置时就会使用解析器AspectJAutoProxyBeanDefinitionParser进行解析，那么我们来看一看AspectJAutoProxyBeanDefinitionParser的内部实现。

### 1、注册AspectJAutoProxyBeanDefinitionParser

所有的解析器，因为是对BeanDefinitionParser接口的统一实现，入口都是从parse方法开始的，AspectJAutoProxyBeanDefinitionParser的parse方法如下

```java
public BeanDefinition parse(Element element, ParserContext parserContext) {
	// 注册AspectJAnnotationAutoProxyCreator   
  AopNamespaceUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(parserContext, element);
    // 对于注解中子类的处理
    extendBeanDefinition(element, parserContext);
    return null;
}
```

其中registerAspectJAnnotationAutoProxyCreatorIfNecessary方法使我们比较关心的，也是关键逻辑的实现。

```java
// 注册AspectJAnnotationAutoProxyCreator
public static void registerAspectJAnnotationAutoProxyCreatorIfNecessary(
        ParserContext parserContext, Element sourceElement) {
	// 注册或升级AutoProxyCreator
    BeanDefinition beanDefinition = AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(
            parserContext.getRegistry(), parserContext.extractSource(sourceElement));
    // 对于proxy-target-class以及expose-proxy属性的处理
    useClassProxyingIfNecessary(parserContext.getRegistry(), sourceElement);
    // 注册组件并通知，便于监听器做进一步处理
    // 其中beanDefinition的className为AnnotationAwareAspectJAutoProxyCreator
    registerComponentIfNecessary(beanDefinition, parserContext);
}
```

在registerAspectJAnnotationAutoProxyCreatorIfNecessary方法中主要完成了3件事情，基本每行代码就是一个完整的逻辑。

1. 注册或升级AspectJAnnotationAutoProxyCreator

对于AOP的实现，基本上都是靠AspectJAnnotationAutoProxyCreator去完成，它可以根据@Point注解定义的切点来自动代理相匹配的bean。但是为了配置简便，Spring使用了自定义配置来帮助我们自动注册AspectJAnnotationAutoProxyCreator，其注册过程就是在这里实现的。

```java
public static BeanDefinition registerAspectJAnnotationAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry, Object source) {
    return registerOrEscalateApcAsRequired(AnnotationAwareAspectJAutoProxyCreator.class, registry, source);
}

private static BeanDefinition registerOrEscalateApcAsRequired(Class cls, BeanDefinitionRegistry registry, Object source) {
    Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
    // 如果已经存在了自动代理创建起且存在的自动代理创建起与现在的不一致，那么需要根据优先级来判断
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        BeanDefinition apcDefinition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
            int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
            int requiredPriority = findPriorityForClass(cls);
            if (currentPriority < requiredPriority) {
                // 改变bean最重要的就是改变bean所对应的className属性
                apcDefinition.setBeanClassName(cls.getName());
            }
        }
        // 如果已经存在自动代理创建器并且与将要创建的一致，那么无需再次创建
        return null;
    }
    RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
    beanDefinition.setSource(source);
    beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
    beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
    return beanDefinition;
}
```

以上代码实现了自动注册AspectJAnnotationAutoProxyCreator类的功能，同时这里还涉及到了一个优先级的问题，如果已经存在自动代理创建起，而且存在的自动代理创建器与现在的不一致，那么需要根据优先级来判断到底需要使用哪个。

2. 处理proxy-target-class以及expose-proxy属性

useClassProxyingIfNecessary实现了proxy-target-class属性以及expose-proxy属性的处理。

```java
private static void useClassProxyingIfNecessary(BeanDefinitionRegistry registry, Element sourceElement) {
    if (sourceElement != null) {
        // 对于proxy-target-class属性的处理
        boolean proxyTargetClass = Boolean.valueOf(sourceElement.getAttribute(PROXY_TARGET_CLASS_ATTRIBUTE));
        if (proxyTargetClass) {
            AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
        }
        // 对于expose-proxy属性的处理
        boolean exposeProxy = Boolean.valueOf(sourceElement.getAttribute(EXPOSE_PROXY_ATTRIBUTE));
        if (exposeProxy) {
            AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
        }
    }
}

public static void forceAutoProxyCreatorToUseClassProxying(BeanDefinitionRegistry registry) {
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        definition.getPropertyValues().add("proxyTargetClass", Boolean.TRUE);
    }
}

// 强制使用的过程其实也是一个属性设置的过程
static void forceAutoProxyCreatorToExposeProxy(BeanDefinitionRegistry registry) {
    if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
        BeanDefinition definition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
        definition.getPropertyValues().add("exposeProxy", Boolean.TRUE);
    }
}
```

### 2、动态代理

- proxy-target-class：Spring AOP部分使用JDK动态代理或者CGLIB来为目标对象创建代理（建议尽量使用JDK的动态代理）。如果被代理目标对象实现了至少一个接口，则会使用JDK动态代理。所有该目标类型的实现的接口都将被代理。若该目标对象没有实现任何接口，则创建一个CGLIB代理。如果你希望强制使用CGLIB代理（例如希望代理目标对象的所有方法），而不只是实现来自接口的方法，那也可以，但是需要考虑一下两个问题

1. 无法通知（advise）final方法，因为它们不能被覆盖重写。
2. 你需要将CGLIB二进制发行包放在classpath下面。

与之相较，JDK本身就提供了动态代理，强制使用CFLIB代理需要将`<aop:config>`的proxy-target-class属性设为true，如下

```xml
<aop:config proxy-target-class="true"/>
```

当需要使用CGLIB代理和AspectJ自动代理支持，可以按照以下方式设置

```xml
<aop:aspectj-autoproxy proxy-target-class="true"/>
```

而在实际使用过程中才会发现细节问题的差别，如下

- JDK动态代理：其代理对象必须是某个接口的实现，它是通过在运行期间创建**一个接口的实现类**来完成对目标对象的代理（如果是实现了多个接口，那么代理类也是实现多个接口的，但代理类型仍然为com.sun.proxy.$Proxy0，可以通过强制转换到目标类型）。
- CGLIB代理：实现原理类似与JDK动态代理，只是它在运行期间生成的代理对象是**针对目标类扩展的子类**。CGLIB是高效的代码生成包，底层是依靠ASM（开源的Java字节码编辑类库）操作字节码实现的，性能比JDK强。
- expose-proxy：有时候目标对象内部的自我调用将无法实施切面中的增加。

例如下文将会出现的两个例子，这里只列出关键代码

```java
// JDK动态代理，默认将不会代理invokeByAdd方法，因为invoke(所代理对象的接口...)形式的方法只调用了一次
public class UserServiceImpl implements UserService {

    @Override
    public void add() {
        System.out.println("==== add and invokeByAdd ====");
        // 如果有这行，将会报错exposeProxy设置出错，查看源代码无非是将代理对象暴露给当前前程
        // 此时一个好的做法就是不要嵌套调用方法，当使用jdk代理时，或者类实现某个接口时
        //System.out.println(AopContext.currentProxy().getClass());
        invokeByAdd();
    }

    @Override
    public void invokeByAdd() {
        System.out.println("invoke by add");
    }
}
输出结果如下
==== before ====
==== add and invokeByAdd ====
invoke by add
==== after ====
    
```

此时有以下几种解决办法（个人观点，未经实践）：

1. 代理对象最好不要实现增强方法之间的调用。
2. 如果非要执行增强方法之间的调用，且如果你的类实现了某个接口，那么Spring将默认使用JDK动态代理，此时可以通过设置expose-proxy属性为true，此时Spring将暴露一个代理对象给ThreadLocal对象，可以通过AopContext.currentProxy()获取到这个对象。
3. 如果你的类实现了某个接口，且不想暴露这个代理对象，或者是觉得上面哪种方法不好使，此时可以强制Spring使用CGLIB代理，此时子方法都会被增强，即使是在内部方法之间的调用。

```java
// 而CGLIB将会代理invokeByTest方法，因为代理对象的子类将替换代理对象，每次都会执行invoke方法
public class CglibProxyTest {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibProxyTest.class);
        enhancer.setCallback(new MethodInterceptorImpl());

        CglibProxyTest cglibProxyTest = (CglibProxyTest) enhancer.create();
        cglibProxyTest.test();
//        System.out.println(cglibProxyTest);
    }

    public void test () {
        System.out.println("CglibProxyTest test()");
        // 如果有这行，将会报错exposeProxy设置出错，尽管cglib不需要其他步骤也是可行的
        //System.out.println(AopContext.currentProxy().getClass());
        invokeByTest();
    }

    public void invokeByTest() {
        System.out.println("invoke by test");
    }

    private static class MethodInterceptorImpl implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("before invoke " + method);
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("after invoke " + method);
            return result;
        }
    }
}

输出结果如下
before invoke public void xiaokui1.proxy.CglibProxyTest.test()
CglibProxyTest test()
before invoke public void xiaokui1.proxy.CglibProxyTest.invokeByTest()
invoke by test
after invoke public void xiaokui1.proxy.CglibProxyTest.invokeByTest()
after invoke public void xiaokui1.proxy.CglibProxyTest.test()
```



## 三、创建AOP代理

上文中讲解了通过自定义配置完成了对AspectJAnnotationAutoProxyCreator类型的自动注册，那么这个类到底做了什么工作来完成AOP的操作呢？首先我们看看注册AspectJAnnotationAutoProxyCreator类的类结构。如图

![AspectJAnnotationAutoProxyCreator类图](https://good-looking-hk-img.oss-cn-shenzhen.aliyuncs.com/spring/AspectJAnnotationAutoProxyCreator%E7%B1%BB%E7%BB%93%E6%9E%84.png)

在类的层级中，我们看到AspectJAnnotationAutoProxyCreator实现了BeanPostProcessor接口，而实现BeanPostProcessor后，当Spring加载这个bean时会在实例化前调用其postProcessAfterInitialization方法，而我们对于AOP逻辑的分析也由此开始。

```java
// 来自于父类AbstractAutoProxyCreator
/**
 * Create a proxy with the configured interceptors if the bean is
 * identified as one to proxy by the subclass.
 * @see #getAdvicesAndAdvisorsForBean
 */
public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean != null) {
        // 根据给定的bean的class和name构建出key，格式：beanClassName_beanName
        Object cacheKey = getCacheKey(bean.getClass(), beanName);
        if (!this.earlyProxyReferences.containsKey(cacheKey)) {
            // 如果它适合被代理，则需要封装指定bean
            return wrapIfNecessary(bean, beanName, cacheKey);
        }
    }
    return bean;
}

/**
 * Wrap the given bean if necessary, i.e. if it is eligible for being proxied.
 * @param bean the raw bean instance
 * @param beanName the name of the bean
 * @param cacheKey the cache key for metadata access
 * @return a proxy wrapping the bean, or the raw bean instance as-is
 */
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
    // 如果已经处理过
    if (beanName != null && this.targetSourcedBeans.containsKey(beanName)) {
        return bean;
    }
    // 无需增强
    if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
        return bean;
    }
    // 给定的bean来是否代表一个基础设施类，基础设施类不应代理，或者配置了指定bean不需要自动代理
    if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
        this.advisedBeans.put(cacheKey, Boolean.FALSE);
        return bean;
    }

    // 如果存在增强方法则创建代理
    // Create proxy if we have advice.
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
    // 如果获取到了增强则需要针对增强创建代理
    if (specificInterceptors != DO_NOT_PROXY) {
        this.advisedBeans.put(cacheKey, Boolean.TRUE);
        // 创建代理
        Object proxy = createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
        this.proxyTypes.put(cacheKey, proxy.getClass());
        return proxy;
    }

    this.advisedBeans.put(cacheKey, Boolean.FALSE);
    return bean;
}
```

方法中我们已经看到了代理创建的雏形。当然，真正开始之前还需要经过一些判断，比如是否已经处理过或者是否是需要跳过的bean，而真正创建代理的代码是从getAdvicesAndAdvisorsForBean开始的。

创建代理主要包含了两个步骤：

1. 获取增强方法或者增强器。
2. 根据获取的增强进行代理。

本方法的每一步其实都经历了大量复杂的逻辑，首先来看看获取增强器方法的实现逻辑。

```java
protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName, TargetSource targetSource) {
    List advisors = findEligibleAdvisors(beanClass, beanName);
    if (advisors.isEmpty()) {
        return DO_NOT_PROXY;
    }
    return advisors.toArray();
}

/**
 * Find all eligible Advisors for auto-proxying this class.
 * @param beanClass the clazz to find advisors for
 * @param beanName the name of the currently proxied bean
 * @return the empty List, not {@code null},
 * if there are no pointcuts or interceptors
 * @see #findCandidateAdvisors
 * @see #sortAdvisors
 * @see #extendAdvisors
 */
protected List<Advisor> findEligibleAdvisors(Class beanClass, String beanName) {
    List<Advisor> candidateAdvisors = findCandidateAdvisors();
    List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
    extendAdvisors(eligibleAdvisors);
    if (!eligibleAdvisors.isEmpty()) {
        eligibleAdvisors = sortAdvisors(eligibleAdvisors);
    }
    return eligibleAdvisors;
}
```

对于指定bean的增强方法的获取一定是包含两个步骤的，获取所有的增强以及寻找所有增强中适用与bean的增强并应用，那么findCandidateAdvisors与findAdvisorsThatCanApply便是做了这两件事情。当然，如果无法找到对应的增强器便返回DO_NOT_PROXY，其中DO_NOT_PROXY为null。

### 1、获取增强器

由于我们分析的是使用注解进行的AOP，所以对于findCandidateAdvisors的实现其实是由AnnotationAwareAspectJAutoProxyCreator类完成的，我们继续跟踪AnnotationAwareAspectJAutoProxyCreator的findCandidateAdvisors方法。

```java
protected List<Advisor> findCandidateAdvisors() {
    // Add all the Spring advisors found according to superclass rules.
    // 当使用注解方式配置AOP的时候，并不是丢弃对xml配置的支持
    // 在这里调用父类方法加载配置文件中的AOP声明
    List<Advisor> advisors = super.findCandidateAdvisors();
    // Build Advisors for all AspectJ aspects in the bean factory.
    advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
    return advisors;
}
```

AnnotationAwareAspectJAutoProxyCreator间接继承了AbstractAdvisorAutoProxyCreator，在实现获取增强的方法中除了保留父类的获取配置文件中定义的增强外，同时添加了获取bean的注解增强的功能，那么其实现正是`this.aspectJAdvisorsBuilder.buildAspectJAdvisors()`来实现的。

我们一改以往的方式，先来了解方法提供的大概功能框架。

1. 获取所有beanName，这一步骤中所有在BeanFactory中注册的bean都会被提取出来。
2. 遍历所有beanName，并找出声明AspectJ注解的类，进行进一步的处理。
3. 对标记为AspectJ注解的类进行增强器的提取。
4. 将提取结果加入缓存。

现在我们来看看方法实现，对Spring中所有的类进行分析，提取Advisor。

```java
/**
 * Look for AspectJ-annotated aspect beans in the current bean factory,
 * and return to a list of Spring AOP Advisors representing them.
 * <p>Creates a Spring Advisor for each AspectJ advice method.
 * @return the list of {@link org.springframework.aop.Advisor} beans
 * @see #isEligibleBean
 */
public List<Advisor> buildAspectJAdvisors() {
    List<String> aspectNames = null;

    synchronized (this) {
        aspectNames = this.aspectBeanNames;
        if (aspectNames == null) {
            List<Advisor> advisors = new LinkedList<Advisor>();
            aspectNames = new LinkedList<String>();
            // 获取所有beanName
            String[] beanNames =
                    BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.beanFactory, Object.class, true, false);
            // 循环所有的beanName，找出对应的增强方法
            for (String beanName : beanNames) {
                // 不合法的bean则略过，由子类定义规则，默认返回true
                if (!isEligibleBean(beanName)) {
                    continue;
                }
                // We must be careful not to instantiate beans eagerly as in this
                // case they would be cached by the Spring container but would not
                // have been weaved
                // 获取对应的bean类型
                Class beanType = this.beanFactory.getType(beanName);
                if (beanType == null) {
                    continue;
                }
                // 如果存在Aspect注解
                if (this.advisorFactory.isAspect(beanType)) {
                    aspectNames.add(beanName);
                    AspectMetadata amd = new AspectMetadata(beanType, beanName);
                    if (amd.getAjType().getPerClause().getKind() == PerClauseKind.SINGLETON) {
                        MetadataAwareAspectInstanceFactory factory =
                                new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                        // 解析标记AspectJ注解中的增强方法
                        List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);
                        if (this.beanFactory.isSingleton(beanName)) {
                            this.advisorsCache.put(beanName, classAdvisors);
                        }
                        else {
                            this.aspectFactoryCache.put(beanName, factory);
                        }
                        advisors.addAll(classAdvisors);
                    }
                    else {
                        // Per target or per this.
                        if (this.beanFactory.isSingleton(beanName)) {
                            throw new IllegalArgumentException("Bean with name '" + beanName +
                                    "' is a singleton, but aspect instantiation model is not singleton");
                        }
                        MetadataAwareAspectInstanceFactory factory =
                                new PrototypeAspectInstanceFactory(this.beanFactory, beanName);
                        this.aspectFactoryCache.put(beanName, factory);
                        advisors.addAll(this.advisorFactory.getAdvisors(factory));
                    }
                }
            }
            this.aspectBeanNames = aspectNames;
            return advisors;
        }
    }

    if (aspectNames.isEmpty()) {
        return Collections.EMPTY_LIST;
    }
    // 记录在缓存中
    List<Advisor> advisors = new LinkedList<Advisor>();
    for (String aspectName : aspectNames) {
        List<Advisor> cachedAdvisors = this.advisorsCache.get(aspectName);
        if (cachedAdvisors != null) {
            advisors.addAll(cachedAdvisors);
        }
        else {
            MetadataAwareAspectInstanceFactory factory = this.aspectFactoryCache.get(aspectName);
            advisors.addAll(this.advisorFactory.getAdvisors(factory));
        }
    }
    return advisors;
}
```

至此，我们已经完成了Advisor的提取，在上面的步骤中最为重要也是最为繁杂的就是增强器的获取，而这一功能委托给了getAdvisors方法去实现（代码：`this.advisorFactory.getAdvisors(factory)`）。

```java
public List<Advisor> getAdvisors(MetadataAwareAspectInstanceFactory maaif) {
    // 获取标记为AspectJ的类
    final Class<?> aspectClass = maaif.getAspectMetadata().getAspectClass();
    // 获取标记为AspectJ的name
    final String aspectName = maaif.getAspectMetadata().getAspectName();
    // 验证
    validate(aspectClass);

    // We need to wrap the MetadataAwareAspectInstanceFactory with a decorator
    // so that it will only instantiate once.
    final MetadataAwareAspectInstanceFactory lazySingletonAspectInstanceFactory =
            new LazySingletonAspectInstanceFactoryDecorator(maaif);

    final List<Advisor> advisors = new LinkedList<Advisor>();
    for (Method method : getAdvisorMethods(aspectClass)) {
        Advisor advisor = getAdvisor(method, lazySingletonAspectInstanceFactory, advisors.size(), aspectName);
        if (advisor != null) {
            advisors.add(advisor);
        }
    }

    // If it's a per target aspect, emit the dummy instantiating aspect.
    if (!advisors.isEmpty() && lazySingletonAspectInstanceFactory.getAspectMetadata().isLazilyInstantiated()) {
        // 如果寻找的增强器不为空而且又配置了增强延迟初始化那么需要在首位加入同步实例化增强器
        Advisor instantiationAdvisor = new SyntheticInstantiationAdvisor(lazySingletonAspectInstanceFactory);
        advisors.add(0, instantiationAdvisor);
    }

    // 获取DeclareParents注解
    // Find introduction fields.
    for (Field field : aspectClass.getDeclaredFields()) {
        Advisor advisor = getDeclareParentsAdvisor(field);
        if (advisor != null) {
            advisors.add(advisor);
        }
    }

    return advisors;
}
```

方法中首先完成了对增强器的获取，包括获取注解以及根据注解生成增强器的步骤，然后考虑在配置中可能会将增强配置成延迟初始化，那么需要在首位加入同步实例化增强器以保证增强使用之前的实例化，最后是对DeclareParents注解的获取，下面将详细介绍每一个步骤。

#### 1、普通增强器的获取

普通增强器的获取逻辑通过getAdvisor方法实现，实现步骤包括对切点的注解的获取以及根据注解信息生成增强。

```java
public Advisor getAdvisor(Method candidateAdviceMethod, MetadataAwareAspectInstanceFactory aif,
        int declarationOrderInAspect, String aspectName) {

    validate(aif.getAspectMetadata().getAspectClass());
	// 切点信息的获取
    AspectJExpressionPointcut ajexp =
            getPointcut(candidateAdviceMethod, aif.getAspectMetadata().getAspectClass());
    if (ajexp == null) {
        return null;
    }
    // 根据切点信息生成增强器
    return new InstantiationModelAwarePointcutAdvisorImpl(
            this, ajexp, aif, candidateAdviceMethod, declarationOrderInAspect, aspectName);
}
```

1. 切点信息的获取。所谓的获取切点信息就是指定注解的表达式信息的获取，如@Before(“test”)。

```java
private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
    // 获取方法上的注解
    AspectJAnnotation<?> aspectJAnnotation =
            AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
    if (aspectJAnnotation == null) {
        return null;
    }
    // 使用AspectJExpressionPointcut实例封装获取的信息
    AspectJExpressionPointcut ajexp =
            new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class[0]);
    // 提取得到的注解中的表达式，如@Pointcut中的值
    ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
    return ajexp;
}

/**
 * Find and return the first AspectJ annotation on the given method
 * (there <i>should</i> only be one anyway...)
 */
@SuppressWarnings("unchecked")
protected static AspectJAnnotation findAspectJAnnotationOnMethod(Method method) {
    // 设置敏感的注解类
    Class<? extends Annotation>[] classesToLookFor = new Class[] {
            Before.class, Around.class, After.class, AfterReturning.class, AfterThrowing.class, Pointcut.class};
    for (Class<? extends Annotation> c : classesToLookFor) {
        AspectJAnnotation foundAnnotation = findAnnotation(method, c);
        if (foundAnnotation != null) {
            return foundAnnotation;
        }
    }
    return null;
}

// 获取指定方法上的注解并使用AspectJAnnotation封装
private static <A extends Annotation> AspectJAnnotation<A> findAnnotation(Method method, Class<A> toLookFor) {
    A result = AnnotationUtils.findAnnotation(method, toLookFor);
    if (result != null) {
        return new AspectJAnnotation<A>(result);
    }
    else {
        return null;
    }
}
```

2. 根据切点信息生成增强器。所有的增强都由Advisor的实现类InstantiationModelAwarePointcutAdvisorImpl统一封装的。

```java
public InstantiationModelAwarePointcutAdvisorImpl(AspectJAdvisorFactory af, AspectJExpressionPointcut ajexp,
        MetadataAwareAspectInstanceFactory aif, Method method, int declarationOrderInAspect, String aspectName) {

    this.declaredPointcut = ajexp;
    this.method = method;
    this.atAspectJAdvisorFactory = af;
    this.aspectInstanceFactory = aif;
    this.declarationOrder = declarationOrderInAspect;
    this.aspectName = aspectName;

    if (aif.getAspectMetadata().isLazilyInstantiated()) {
        // Static part of the pointcut is a lazy type.
        Pointcut preInstantiationPointcut =
                Pointcuts.union(aif.getAspectMetadata().getPerClausePointcut(), this.declaredPointcut);

        // Make it dynamic: must mutate from pre-instantiation to post-instantiation state.
        // If it's not a dynamic pointcut, it may be optimized out
        // by the Spring AOP infrastructure after the first evaluation.
        this.pointcut = new PerTargetInstantiationModelPointcut(this.declaredPointcut, preInstantiationPointcut, aif);
        this.lazy = true;
    }
    else {
        // A singleton aspect.
        this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
        this.pointcut = declaredPointcut;
        this.lazy = false;
    }
}
```

在封装过程中只是简单地将信息封装在类的实例中，所有信息单纯地赋值，在实例初始化的过程中还完成了对于增强器的初始化。因为不同的增强所体现的逻辑是不同的，比如@Before与@After标签的不同就是增强器增强的位置的不同，所以就需要不同的增强器来完成不同的逻辑，而根据注解中的信息初始化对应的增强器就是在instantiateAdvice方法中实现的。

```java
private Advice instantiateAdvice(AspectJExpressionPointcut pcut) {
    return this.atAspectJAdvisorFactory.getAdvice(
            this.method, pcut, this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
}

public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut ajexp, MetadataAwareAspectInstanceFactory aif, int declarationOrderInAspect, String aspectName) {

    Class<?> candidateAspectClass = aif.getAspectMetadata().getAspectClass();
    validate(candidateAspectClass);

    AspectJAnnotation<?> aspectJAnnotation =
            AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
    if (aspectJAnnotation == null) {
        return null;
    }

    // If we get here, we know we have an AspectJ method.
    // Check that it's an AspectJ-annotated class
    if (!isAspect(candidateAspectClass)) {
        throw new AopConfigException("Advice must be declared inside an aspect type: " +
                "Offending method '" + candidateAdviceMethod + "' in class [" +
                candidateAspectClass.getName() + "]");
    }

    if (logger.isDebugEnabled()) {
        logger.debug("Found AspectJ method: " + candidateAdviceMethod);
    }

    AbstractAspectJAdvice springAdvice;
	// 根据不同的注解类型封装不同的增强器
    switch (aspectJAnnotation.getAnnotationType()) {
        case AtBefore:
            springAdvice = new AspectJMethodBeforeAdvice(candidateAdviceMethod, ajexp, aif);
            break;
        case AtAfter:
            springAdvice = new AspectJAfterAdvice(candidateAdviceMethod, ajexp, aif);
            break;
        case AtAfterReturning:
            springAdvice = new AspectJAfterReturningAdvice(candidateAdviceMethod, ajexp, aif);
            AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
            if (StringUtils.hasText(afterReturningAnnotation.returning())) {
                springAdvice.setReturningName(afterReturningAnnotation.returning());
            }
            break;
        case AtAfterThrowing:
            springAdvice = new AspectJAfterThrowingAdvice(candidateAdviceMethod, ajexp, aif);
            AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
            if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
                springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
            }
            break;
        case AtAround:
            springAdvice = new AspectJAroundAdvice(candidateAdviceMethod, ajexp, aif);
            break;
        case AtPointcut:
            if (logger.isDebugEnabled()) {
                logger.debug("Processing pointcut '" + candidateAdviceMethod.getName() + "'");
            }
            return null;
        default:
            throw new UnsupportedOperationException(
                    "Unsupported advice type on method " + candidateAdviceMethod);
    }

    // Now to configure the advice...
    springAdvice.setAspectName(aspectName);
    springAdvice.setDeclarationOrder(declarationOrderInAspect);
    String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
    if (argNames != null) {
        springAdvice.setArgumentNamesFromStringArray(argNames);
    }
    springAdvice.calculateArgumentBindings();
    return springAdvice;
}
```

从方法中可以看到，Spring会根据不同的注解生成不同的增强类，例如AtBefore会对应AspectJMethodBeforeAdvice，而在AspectJMethodBeforeAdvice中完成了增强方法的逻辑。我们尝试分析下几个常用的增强器的实现。

- MethodBeforeAdviceInterceptor

```java
/**
 * Interceptor to wrap am {@link org.springframework.aop.MethodBeforeAdvice}.
 * Used internally by the AOP framework; application developers should not need
 * to use this class directly.
 */
@SuppressWarnings("serial")
public class MethodBeforeAdviceInterceptor implements MethodInterceptor, Serializable {

	private MethodBeforeAdvice advice;


	/**
	 * Create a new MethodBeforeAdviceInterceptor for the given advice.
	 * @param advice the MethodBeforeAdvice to wrap
	 */
	public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
		Assert.notNull(advice, "Advice must not be null");
		this.advice = advice;
	}

	public Object invoke(MethodInvocation mi) throws Throwable {
		this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis() );
		return mi.proceed();
	}

}
```

其中的属性MethodBeforeAdvice代表着前置增强的AspectJMethodBeforeAdvice，跟踪before方法

```java
public void before(Method method, Object[] args, Object target) throws Throwable {
	invokeAdviceMethod(getJoinPointMatch(), null, null);
}

/**
 * Invoke the advice method.
 * @param jpMatch the JoinPointMatch that matched this execution join point
 * @param returnValue the return value from the method execution (may be null)
 * @param ex the exception thrown by the method execution (may be null)
 * @return the invocation result
 * @throws Throwable in case of invocation failure
 */
protected Object invokeAdviceMethod(JoinPointMatch jpMatch, Object returnValue, Throwable ex) throws Throwable {
    return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), jpMatch, returnValue, ex));
}

protected Object invokeAdviceMethodWithGivenArgs(Object[] args) throws Throwable {
    Object[] actualArgs = args;
    if (this.aspectJAdviceMethod.getParameterTypes().length == 0) {
        actualArgs = null;
    }
    try {
        ReflectionUtils.makeAccessible(this.aspectJAdviceMethod);
        // TODO AopUtils.invokeJoinpointUsingReflection
        // 激活增强方法
        return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);
    }
    catch (IllegalArgumentException ex) {
        throw new AopInvocationException("Mismatch on arguments to advice method [" +
                this.aspectJAdviceMethod + "]; pointcut expression [" +
                this.pointcut.getPointcutExpression() + "]", ex);
    }
    catch (InvocationTargetException ex) {
        throw ex.getTargetException();
    }
}	
```

invokeAdviceMethodWithGivenArgs方法中的aspectJAdviceMethod正是对于前置增强的方法，在这里实现了调用。

#### 2、增强同步实例化增强器

如果寻找的增强器不为空而且又配置了增强延迟初始化，那么就需要在首位加入同步实例化增强器。同步实例化增强器SyntheticInstantiationAdvisor如下

```java
/**
 * Synthetic advisor that instantiates the aspect.
 * Triggered by per-clause pointcut on non-singleton aspect.
 * The advice has no effect.
 */
@SuppressWarnings("serial")
protected static class SyntheticInstantiationAdvisor extends DefaultPointcutAdvisor {

    public SyntheticInstantiationAdvisor(final MetadataAwareAspectInstanceFactory aif) {
        super(aif.getAspectMetadata().getPerClausePointcut(), new MethodBeforeAdvice() {
            // 目标方法前调用，类似@Before
            public void before(Method method, Object[] args, Object target) {
                // Simply instantiate the aspect
                // 简单初始化aspect
                aif.getAspectInstance();
            }
        });
    }
}
```

#### 3、获取DeclareParents注解

DeclareParents主要用于引介增强的注解形式的实现，而其实现与普通增强很类似，只不过使用了DeclareParentsAdvisor对功能进行封装。

```java
/**
 * Build a {@link org.springframework.aop.aspectj.DeclareParentsAdvisor}
 * for the given introduction field.
 * <p>Resulting Advisors will need to be evaluated for targets.
 * @param introductionField the field to introspect
 * @return {@code null} if not an Advisor
 */
private Advisor getDeclareParentsAdvisor(Field introductionField) {
    DeclareParents declareParents = introductionField.getAnnotation(DeclareParents.class);
    if (declareParents == null) {
        // Not an introduction field
        return null;
    }

    if (DeclareParents.class.equals(declareParents.defaultImpl())) {
        // This is what comes back if it wasn't set. This seems bizarre...
        // TODO this restriction possibly should be relaxed
        throw new IllegalStateException("defaultImpl must be set on DeclareParents");
    }

    return new DeclareParentsAdvisor(
            introductionField.getType(), declareParents.value(), declareParents.defaultImpl());
}
```

### 2、寻找匹配的增强器

前面的方法中已经完成了所有增强器的解析，但是对于所有增强器来讲，并不一定都适用于当前bean，还要挑取出适合的增强器，也就是满足我们配置的通配符的增强器。具体实现在findAdvisThatCanApply中。

```java
/**
 * Search the given candidate Advisors to find all Advisors that
 * can apply to the specified bean.
 * @param candidateAdvisors the candidate Advisors
 * @param beanClass the target's bean class
 * @param beanName the target's bean name
 * @return the List of applicable Advisors
 * @see ProxyCreationContext#getCurrentProxiedBeanName()
 */
protected List<Advisor> findAdvisorsThatCanApply(
        List<Advisor> candidateAdvisors, Class beanClass, String beanName) {

    ProxyCreationContext.setCurrentProxiedBeanName(beanName);
    try {
        // 顾虑已经得到的advisors
        return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
    }
    finally {
        ProxyCreationContext.setCurrentProxiedBeanName(null);
    }
}

/**
 * Determine the sublist of the {@code candidateAdvisors} list
 * that is applicable to the given class.
 * @param candidateAdvisors the Advisors to evaluate
 * @param clazz the target class
 * @return sublist of Advisors that can apply to an object of the given class
 * (may be the incoming List as-is)
 */
public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
    if (candidateAdvisors.isEmpty()) {
        return candidateAdvisors;
    }
    List<Advisor> eligibleAdvisors = new LinkedList<Advisor>();
    // 首先处理引介增强
    for (Advisor candidate : candidateAdvisors) {
        if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
            eligibleAdvisors.add(candidate);
        }
    }
    boolean hasIntroductions = !eligibleAdvisors.isEmpty();
    for (Advisor candidate : candidateAdvisors) {
        // 引介增强已吹
        if (candidate instanceof IntroductionAdvisor) {
            // already processed
            continue;
        }
        // 对于普通bean的处理
        if (canApply(candidate, clazz, hasIntroductions)) {
            eligibleAdvisors.add(candidate);
        }
    }
    return eligibleAdvisors;
}
```

findAdvisorsThatCanApply方法的主要功能是寻找所有增强器中适用于当前class的增强器。引介增强与普通的增强的处理是不一样的，所以要分开处理。而对于真正的匹配在canApply中实现。

```java
/**
 * Can the given advisor apply at all on the given class?
 * <p>This is an important test as it can be used to optimize out a advisor for a class.
 * This version also takes into account introductions (for IntroductionAwareMethodMatchers).
 * @param advisor the advisor to check
 * @param targetClass class we're testing
 * @param hasIntroductions whether or not the advisor chain for this bean includes
 * any introductions
 * @return whether the pointcut can apply on any method
 */
public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
    if (advisor instanceof IntroductionAdvisor) {
        return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
    }
    else if (advisor instanceof PointcutAdvisor) {
        PointcutAdvisor pca = (PointcutAdvisor) advisor;
        return canApply(pca.getPointcut(), targetClass, hasIntroductions);
    }
    else {
        // It doesn't have a pointcut so we assume it applies.
        return true;
    }
}

/**
 * Can the given pointcut apply at all on the given class?
 * <p>This is an important test as it can be used to optimize
 * out a pointcut for a class.
 * @param pc the static or dynamic pointcut to check
 * @param targetClass the class to test
 * @param hasIntroductions whether or not the advisor chain
 * for this bean includes any introductions
 * @return whether the pointcut can apply on any method
 */
public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
    Assert.notNull(pc, "Pointcut must not be null");
    if (!pc.getClassFilter().matches(targetClass)) {
        return false;
    }

    MethodMatcher methodMatcher = pc.getMethodMatcher();
    IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
    if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
        introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
    }

    Set<Class> classes = new LinkedHashSet<Class>(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
    classes.add(targetClass);
    for (Class<?> clazz : classes) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if ((introductionAwareMethodMatcher != null &&
                    introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions)) ||
                    methodMatcher.matches(method, targetClass)) {
                return true;
            }
        }
    }

    return false;
}
```

### 3、创建代理

在获取了所有对应bean的增强器后，便可以进行代理的创建了。

```java
/**
 * Create an AOP proxy for the given bean.
 * @param beanClass the class of the bean
 * @param beanName the name of the bean
 * @param specificInterceptors the set of interceptors that is
 * specific to this bean (may be empty, but not null)
 * @param targetSource the TargetSource for the proxy,
 * already pre-configured to access the bean
 * @return the AOP proxy for the bean
 * @see #buildAdvisors
 */
protected Object createProxy(
        Class<?> beanClass, String beanName, Object[] specificInterceptors, TargetSource targetSource) {

    ProxyFactory proxyFactory = new ProxyFactory();
    // Copy our properties (proxyTargetClass etc) inherited from ProxyConfig.
    // 获取当前类中相关属性
    proxyFactory.copyFrom(this);

    if (!shouldProxyTargetClass(beanClass, beanName)) {
        // Must allow for introductions; can't just set interfaces to
        // the target's interfaces only.
        // 决定对于给定的bea是否应该使用targetClass而不是它的接口代理
        // 检查proxyTargetClass设置以及preserveTargetClass属性
        Class<?>[] targetInterfaces = ClassUtils.getAllInterfacesForClass(beanClass, this.proxyClassLoader);
        for (Class<?> targetInterface : targetInterfaces) {
            // 添加代理接口
            proxyFactory.addInterface(targetInterface);
        }
    }

    Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
    for (Advisor advisor : advisors) {
        // 加入增强器
        proxyFactory.addAdvisor(advisor);
    }
	// 设置要代理的类
    proxyFactory.setTargetSource(targetSource);
    // 定制代理
    customizeProxyFactory(proxyFactory);
	
    // 用来控制代理工厂被配置之后，是否还允许修改通知
    // 缺省值为false，即在代理被配置之后，不允许修改代理的配置
    proxyFactory.setFrozen(this.freezeProxy);
    if (advisorsPreFiltered()) {
        proxyFactory.setPreFiltered(true);
    }

    return proxyFactory.getProxy(this.proxyClassLoader);
}
```

对于代理类的创建及处理，Spring委托给了ProxyFactory去处理，而在此方法中主要是对ProxyFactory的初始化操作，进而对真正的创建代理做准备，这些初始化操作包括如下内容。

1）获取当前类中的属性。

2）添加代理接口。

3）封装Advisor并加入到ProxyFactory中。

4）设置要代理的类。

5）当然在Spring中还为子类提供了定制的方法customizeProxyFactory，子类可以在此方法中进行对ProxyFactory的进一步封装。

6）进行获取代理操作。

其中，封装Advisor并加入到ProxyFactory中以及创建代理是两个相对繁琐的过程，可以通过ProxyFactory提供的addAdvisor方法直接将增强器加入代理创建工厂中，但是将拦截器封装为增强器还是需要一定的逻辑的。

```java
/**
 * Determine the advisors for the given bean, including the specific interceptors
 * as well as the common interceptor, all adapted to the Advisor interface.
 * @param beanName the name of the bean
 * @param specificInterceptors the set of interceptors that is
 * specific to this bean (may be empty, but not null)
 * @return the list of Advisors for the given bean
 */
protected Advisor[] buildAdvisors(String beanName, Object[] specificInterceptors) {
    // Handle prototypes correctly...
    // 解析注册的所有intercetorName
    Advisor[] commonInterceptors = resolveInterceptorNames();

    List<Object> allInterceptors = new ArrayList<Object>();
    if (specificInterceptors != null) {
        // 加入拦截器
        allInterceptors.addAll(Arrays.asList(specificInterceptors));
        if (commonInterceptors != null) {
            if (this.applyCommonInterceptorsFirst) {
                allInterceptors.addAll(0, Arrays.asList(commonInterceptors));
            }
            else {
                allInterceptors.addAll(Arrays.asList(commonInterceptors));
            }
        }
    }
    if (logger.isDebugEnabled()) {
        int nrOfCommonInterceptors = (commonInterceptors != null ? commonInterceptors.length : 0);
        int nrOfSpecificInterceptors = (specificInterceptors != null ? specificInterceptors.length : 0);
        logger.debug("Creating implicit proxy for bean '" + beanName + "' with " + nrOfCommonInterceptors +
                " common interceptors and " + nrOfSpecificInterceptors + " specific interceptors");
    }

    Advisor[] advisors = new Advisor[allInterceptors.size()];
    for (int i = 0; i < allInterceptors.size(); i++) {
        // 拦截器进行封装转化为Advisor
        advisors[i] = this.advisorAdapterRegistry.wrap(allInterceptors.get(i));
    }
    return advisors;
}
public Advisor wrap(Object adviceObject) throws UnknownAdviceTypeException {
    // 如果要封装的对象本身就是Advisor，那么无需处理
    if (adviceObject instanceof Advisor) {
        return (Advisor) adviceObject;
    }
    // 因此此封装方法只对Advisor与Advice两种类型的数据类型有效，如果不是将不能封装
    if (!(adviceObject instanceof Advice)) {
        throw new UnknownAdviceTypeException(adviceObject);
    }
    Advice advice = (Advice) adviceObject;
    if (advice instanceof MethodInterceptor) {
        // So well-known it doesn't even need an adapter.
        // 如果是MethodInterceptor类型则使用DefaultPointcutAdvisor封装
        return new DefaultPointcutAdvisor(advice);
    }
    // 如果存在Advisor的适配器那么也同样需要进行封装
    for (AdvisorAdapter adapter : this.adapters) {
        // Check that it is supported.
        if (adapter.supportsAdvice(advice)) {
            return new DefaultPointcutAdvisor(advice);
        }
    }
    throw new UnknownAdviceTypeException(advice);
}	
```

#### 1、创建代理

由于Spring中涉及过多的拦截器、增强器、增强方法等方式来对多级进行增强，所以非常有必要统一封装成Advisor来进行代理的创建，完成了增强的封装过程，那么解析最重要的一步就是代理的创建与获取了。

```java
/**
 * Subclasses should call this to get a new AOP proxy. They should <b>not</b>
 * create an AOP proxy with {@code this} as an argument.
 */
protected final synchronized AopProxy createAopProxy() {
    if (!this.active) {
        activate();
    }
    // 创建代理
    return getAopProxyFactory().createAopProxy(this);
}

public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
    if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
        Class targetClass = config.getTargetClass();
        if (targetClass == null) {
            throw new AopConfigException("TargetSource cannot determine target class: " +
                    "Either an interface or a target is required for proxy creation.");
        }
        if (targetClass.isInterface()) {
            return new JdkDynamicAopProxy(config);
        }
        return CglibProxyFactory.createCglibProxy(config);
    }
    else {
        return new JdkDynamicAopProxy(config);
    }
}
```

到此已经完成了代理的创建，不过我们之前是否有阅读过Spring的源代码，但是或多或少地听过对于Spring的代理中JDKProxy的实现和CglibProxy的实现。Spring是如何选择的呢？我们从源代码角度分析，看看Spring到底是如何选择代理方式的。

从if的判断条件可以看到3个方面影响着Spring的判断。

- optimize：用来控制通过CGLIB创建的代理是否使用激进的优化策略。除非完全了解AOP代理是如何处理优化，否则不推荐用户使用这个设置。目前这个属性仅用于CGLIB代理，对于JDK代理无效。
- proxyTargetClass：这个属性为true时，目标类本身被代理而不是目标类的接口。如果这个属性值被设为true，CGLIB代理将被创建，设置方式：`<aop:aspectj-autoproxy proxy-target-class="true"/>`。-
- hasNoUserSuppliedProxyInterfaces：是否存在代理接口。

下面是对JDK与CGLIB方式的总结

- 如果目标对象实现了接口，默认情况下会采用JDK的动态代理实现AOP。
- 如果目标对象实现了接口，可以强制使用CGLIB实现AOP。
- 如果目标对象没有实现接口，必须采用CGLIB库，Spring会自动在JDK动态代理和CGLIB代理之间转换。

#### 2、获取代理

确定了使用哪种代理方式之后便可以进行代理的创建了，但是创建之前有必要回顾一下两种方式的使用方法。

1. JDK代理使用示例

```java
public interface UserService { 
    void add();
}

public class UserServiceImpl implements UserService {
    @Override
    public void add() {
        System.out.println("==== add ====");
    }
}

public class MyInvocationHandler implements InvocationHandler {
    private Object target;
    public MyInvocationHandler(Object target) {
        super();
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("==== before ====");
        Object result = method.invoke(target, args);
        System.out.println("==== after ====");
        return result;
    }
    public Object getProxy() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), target.getClass().getInterfaces(), this);
    }
}

public static void main(String[] args) {
    UserService userService = new UserServiceImpl();
    MyInvocationHandler invocationHandler = new MyInvocationHandler(userService);
    UserService proxy = (UserService)invocationHandler.getProxy();
    proxy.add();
}
输出结果
==== before ====
==== add ====
==== after ====
```

以上就是一个简单的基于JDK代理的代理模式的应用，也是一个基本的AOP的实现了，在目标对象的方法执行之前和执行之后进行了增强。Spring的AOP实现其实也是用了Proxy和InvocationHandler这两个东西。

我们再次来回顾一下使用JDK代理的方式，在整个创建过程中，对于InvocationHandler的创建是最为核心的，在自定义的InvocationHandler中需要重写3个方法。

- 构造方法，将代理对象传入。
- invoke方法，此方法中实现了AOP增强的所有逻辑。
- getProxy方法，此方法千篇一律，但是必不可少。

那么我们看看Spring中的JDK代理实现是不是也是这么做的，继续之前的跟踪，到达JdkDynamicAopProxy的getProxy。

```java
public Object getProxy(ClassLoader classLoader) {
    if (logger.isDebugEnabled()) {
        logger.debug("Creating JDK dynamic proxy: target source is " + this.advised.getTargetSource());
    }
    Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised);
    findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
    return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
}
```

通过之前的示例我们知道，JDKProxy的使用关键是创建自定义的InvocationHandler，而InvocationHandler中包含了需要覆盖的方法getProxy，而当前的方法正式完成这个操作的。再次去确认一下，JdkDynamicAopProxy也确实实现了InvocationHandler接口，那么我们就可以推断还有一个invoke方法，并且JdkDynamicAopProxy会把AOP的核心逻辑写在其中。

```java
/**
 * Implementation of {@code InvocationHandler.invoke}.
 * <p>Callers will see exactly the exception thrown by the target,
 * unless a hook method throws an exception.
 */
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodInvocation invocation;
    Object oldProxy = null;
    boolean setProxyContext = false;

    TargetSource targetSource = this.advised.targetSource;
    Class<?> targetClass = null;
    Object target = null;

    try {
        // equals方法的处理
        if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
            // The target does not implement the equals(Object) method itself.
            return equals(args[0]);
        }
        // hash方法的处理
        if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
            // The target does not implement the hashCode() method itself.
            return hashCode();
        }
        // 自身类.class.isAssignableFrom（自身类或子类.class），返回true
        if (!this.advised.opaque && method.getDeclaringClass().isInterface() &&
                method.getDeclaringClass().isAssignableFrom(Advised.class)) {
            // Service invocations on ProxyConfig with the proxy config...
            return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
        }

        Object retVal;
		// 有时候目标对象内部的自我调用将无法实施切面中的增强，则需要通过此属性暴露代理
        if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }

        // May be null. Get as late as possible to minimize the time we "own" the target,
        // in case it comes from a pool.
        target = targetSource.getTarget();
        if (target != null) {
            targetClass = target.getClass();
        }
		
        // Get the interception chain for this method.
        // 获取当前方法的拦截器链
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        // Check whether we have any advice. If we don't, we can fallback on direct
        // reflective invocation of the target, and avoid creating a MethodInvocation.
        if (chain.isEmpty()) {
            // We can skip creating a MethodInvocation: just invoke the target directly
            // Note that the final invoker must be an InvokerInterceptor so we know it does
            // nothing but a reflective operation on the target, and no hot swapping or fancy proxying.
            // 如果没有发现任何拦截器，那么直接调用切点方法
            retVal = AopUtils.invokeJoinpointUsingReflection(target, method, args);
        }
        else {
            // We need to create a method invocation...
            // 将拦截器封装在ReflectiveMethodInvocation，以便于使用其proceed进行链接拦截器
            invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
            // Proceed to the joinpoint through the interceptor chain.
            // 执行拦截器链
            retVal = invocation.proceed();
        }

        // Massage return value if necessary.
        Class<?> returnType = method.getReturnType();
        if (retVal != null && retVal == target && returnType.isInstance(proxy) &&
                !RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
            // Special case: it returned "this" and the return type of the method
            // is type-compatible. Note that we can't help if the target sets
            // a reference to itself in another returned object.
            retVal = proxy;
        }
        // 返回结果
        else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
            throw new AopInvocationException(
                    "Null return value from advice does not match primitive return type for: " + method);
        }
        return retVal;
    }
    finally {
        if (target != null && !targetSource.isStatic()) {
            // Must have come from TargetSource.
            targetSource.releaseTarget(target);
        }
        if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```

上面的方法中最主要的工作就是创建了一个拦截器链，并使用ReflectiveMethodInvocation类进行了链的封装，而在ReflectiveMethodInvocation类的proceed方法中实现了拦截器的逐一调用，俺么继续来探究，在proceed方法中是怎么实现前置增强在目标方法前调用，后置增强在目标方法后调用的逻辑。

```java
public Object proceed() throws Throwable {
    //	We start with an index of -1 and increment early.
    // 执行完所有增强后执行切点方法
    if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
        return invokeJoinpoint();
    }
	// 获取下一个要执行的拦截器
    Object interceptorOrInterceptionAdvice =
            this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
    if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
        // Evaluate dynamic method matcher here: static part will already have
        // been evaluated and found to match.
        // 动态匹配
        InterceptorAndDynamicMethodMatcher dm =
                (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
        if (dm.methodMatcher.matches(this.method, this.targetClass, this.arguments)) {
            return dm.interceptor.invoke(this);
        }
        else {
            // Dynamic matching failed.
            // Skip this interceptor and invoke the next in the chain.
            // 不匹配则不执行拦截器
            return proceed();
        }
    }
    else {
        // It's an interceptor, so we just invoke it: The pointcut will have
        // been evaluated statically before this object was constructed.
        // 普通拦截器，直接调用拦截器
        // 将this作为参数传递一保证当前实例中调用链的执行
        return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
    }
}
```

在proceed方法中，或许代码逻辑并没有我们想象得那么复杂，ReflectiveMethodInvocation中的主要职责是维护链接调用的计数器，记录当前调用链接的位置，以便链可以有序地进行下去，那么在这个方法中并没有我们之前设想的维护各种增强的顺序，而是将此工作委托给了各个增强器，使各个增强器在内部逻辑实现。

2. CGLIB使用示例

CGLB是一个强大的高性能的代码生成包。它广泛地被许多AOP的框架使用，例如Spring AOP和dynaop，为他们提供方法的Interception（拦截）。最流行的OM Mapping工具Hibernate也使用CGLIB来代理单端single-ended（多对一和一对一）关联（对集合的延迟抓取是采用其他机制实现的）。EasyMock和jMock是通过使用模仿（moke）对象来测试Java代码的包。他们都是通过CGLIB来为那些没有接口的类创建模仿对象。

CGLIB包的底层通过使用一个小而快的字节码处理框架ASM，来转换字节码并生成新的类。除了CGLIB包，脚本语言例如Groovy和BeanShell，也是使用ASM来生成Java的字节码。当然不鼓励直接使用ASM，因为它要求你必须对JVM内部结构包括claas文件的格式和指令集都很熟悉。

我们先快速地了解CGLIB的使用实例。

```java
public class CglibProxyTest {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CglibProxyTest.class);
        enhancer.setCallback(new MethodInterceptorImpl());

        CglibProxyTest cglibProxyTest = (CglibProxyTest) enhancer.create();
        cglibProxyTest.test();
        System.out.println(cglibProxyTest);
    }

    public void test () {
        System.out.println("CglibProxyTest test()");
    }

    private static class MethodInterceptorImpl implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            System.out.println("before invoke " + method);
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("after invoke " + method);
            return result;
        }
    }
}

运行结果如下
before invoke public void xiaokui1.proxy.CglibProxyTest.test()
CglibProxyTest test()
after invoke public void xiaokui1.proxy.CglibProxyTest.test()
before invoke public java.lang.String java.lang.Object.toString()
before invoke public native int java.lang.Object.hashCode()
after invoke public native int java.lang.Object.hashCode()
after invoke public java.lang.String java.lang.Object.toString()
xiaokui1.proxy.CglibProxyTest$$EnhancerByCGLIB$$e15d545a@6bf2d08e

```

可以看到System.out.println(cglibProxyTest)，首先 调用了toString方法，然后又调用了hashCode，生成的对象为CglibProxyTest$$EnhancerByCGLIB$$e15d545a@6bf2d08e的实例，这个类是运行时由CGLIB产生的。

完成CGLIB代理的类是委托给CglibAopProxy类去实现的，我们进入这个类一探究竟。按照前面提供的示例，我们容易判断出来，CglibAopProxy的入口应该实在getProxy，也就是说在CglibAopProxy类的getProxy方法中实现Enhancer的创建及接口封装。

```java
public Object getProxy(ClassLoader classLoader) {
    if (logger.isDebugEnabled()) {
        logger.debug("Creating CGLIB proxy: target source is " + this.advised.getTargetSource());
    }

    try {
        Class<?> rootClass = this.advised.getTargetClass();
        Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");

        Class<?> proxySuperClass = rootClass;
        if (ClassUtils.isCglibProxyClass(rootClass)) {
            proxySuperClass = rootClass.getSuperclass();
            Class<?>[] additionalInterfaces = rootClass.getInterfaces();
            for (Class<?> additionalInterface : additionalInterfaces) {
                this.advised.addInterface(additionalInterface);
            }
        }

        // Validate the class, writing log messages as necessary.
        // 验证class
        validateClassIfNecessary(proxySuperClass);

        // Configure CGLIB Enhancer...
        // 创建及配置Enhancer
        Enhancer enhancer = createEnhancer();
        if (classLoader != null) {
            enhancer.setClassLoader(classLoader);
            if (classLoader instanceof SmartClassLoader &&
                    ((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
                enhancer.setUseCache(false);
            }
        }
        enhancer.setSuperclass(proxySuperClass);
        enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
        enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
        enhancer.setStrategy(new MemorySafeUndeclaredThrowableStrategy(UndeclaredThrowableException.class));
        enhancer.setInterceptDuringConstruction(false);

        // 设置拦截器
        Callback[] callbacks = getCallbacks(rootClass);
        Class<?>[] types = new Class<?>[callbacks.length];
        for (int x = 0; x < types.length; x++) {
            types[x] = callbacks[x].getClass();
        }
        enhancer.setCallbackFilter(new ProxyCallbackFilter(
                this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
        enhancer.setCallbackTypes(types);
        enhancer.setCallbacks(callbacks);

        // Generate the proxy class and create a proxy instance.
        // 生成代理类以及创建代理
        Object proxy;
        if (this.constructorArgs != null) {
            proxy = enhancer.create(this.constructorArgTypes, this.constructorArgs);
        }
        else {
            proxy = enhancer.create();
        }

        return proxy;
    }
    catch (CodeGenerationException ex) {
        throw new AopConfigException("Could not generate CGLIB subclass of class [" +
                this.advised.getTargetClass() + "]: " +
                "Common causes of this problem include using a final class or a non-visible class",
                ex);
    }
    catch (IllegalArgumentException ex) {
        throw new AopConfigException("Could not generate CGLIB subclass of class [" +
                this.advised.getTargetClass() + "]: " +
                "Common causes of this problem include using a final class or a non-visible class",
                ex);
    }
    catch (Exception ex) {
        // TargetSource.getTarget() failed
        throw new AopConfigException("Unexpected AOP exception", ex);
    }
}
```

以上方法完整地阐述了一个创建Spring中的Enhancer的过程，读者可以参考Enhancer的文档查看每个步骤的含义，这里最重要的是通过getCallbacks方法设置拦截器链。

```java
private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
    // Parameters used for optimisation choices...
    // 对于expose-proxy属性的处理
    boolean exposeProxy = this.advised.isExposeProxy();
    boolean isFrozen = this.advised.isFrozen();
    boolean isStatic = this.advised.getTargetSource().isStatic();

    // Choose an "aop" interceptor (used for AOP calls).
    // 将拦截器封装在DynamicAdvisedInterceptor
    Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);

    // Choose a "straight to target" interceptor. (used for calls that are
    // unadvised but can return this). May be required to expose the proxy.
    Callback targetInterceptor;
    if (exposeProxy) {
        targetInterceptor = isStatic ?
                new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) :
                new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource());
    }
    else {
        targetInterceptor = isStatic ?
                new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) :
                new DynamicUnadvisedInterceptor(this.advised.getTargetSource());
    }

    // Choose a "direct to target" dispatcher (used for
    // unadvised calls to static targets that cannot return this).
    Callback targetDispatcher = isStatic ?
            new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp();
	// 将拦截器链加入Callback中
    Callback[] mainCallbacks = new Callback[] {
            aopInterceptor,  // for normal advice
            targetInterceptor,  // invoke target without considering advice, if optimized
            new SerializableNoOp(),  // no override for methods mapped to this
            targetDispatcher, this.advisedDispatcher,
            new EqualsInterceptor(this.advised),
            new HashCodeInterceptor(this.advised)
    };

    Callback[] callbacks;

    // If the target is a static one and the advice chain is frozen,
    // then we can make some optimisations by sending the AOP calls
    // direct to the target using the fixed chain for that method.
    if (isStatic && isFrozen) {
        Method[] methods = rootClass.getMethods();
        Callback[] fixedCallbacks = new Callback[methods.length];
        this.fixedInterceptorMap = new HashMap<String, Integer>(methods.length);

        // TODO: small memory optimisation here (can skip creation for methods with no advice)
        for (int x = 0; x < methods.length; x++) {
            List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(methods[x], rootClass);
            fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(
                    chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
            this.fixedInterceptorMap.put(methods[x].toString(), x);
        }

        // Now copy both the callbacks from mainCallbacks
        // and fixedCallbacks into the callbacks array.
        callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
        System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
        System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
        this.fixedInterceptorOffset = mainCallbacks.length;
    }
    else {
        callbacks = mainCallbacks;
    }
    return callbacks;
}
```

在getCallback中Spring考虑了很多情况，但是对于我们来说，只需要理解最常用的的就可以了，比如将advised属性封装在DynamicUnadvisedInterceptor并加入在callbacks中，这么做的目的是什么你呢，如何调用呢？在前面的示例中，我们了解到CGLIB中对于方法的拦截是通过将自定义的拦截器（实现MethodInterceptor接口）加入Callback中并在调用时直接激活拦截器中intercept方法来实现的，那么在getCallback方法中正是实现了这样一个目的，DynamicUnadvisedInterceptor继承自MethodInterceptor，加入Callback中后，在再次调用代理时会直接调用DynamicUnadvisedInterceptor中的intercept方法，由此推断，对于CGLIB方式实现的代理，其核心逻辑必然在DynamicUnadvisedInterceptor中的intercept方法中。

```java
public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
    Object oldProxy = null;
    boolean setProxyContext = false;
    Class<?> targetClass = null;
    Object target = null;
    try {
        if (this.advised.exposeProxy) {
            // Make invocation available if necessary.
            oldProxy = AopContext.setCurrentProxy(proxy);
            setProxyContext = true;
        }
        // May be null. Get as late as possible to minimize the time we
        // "own" the target, in case it comes from a pool...
        target = getTarget();
        if (target != null) {
            targetClass = target.getClass();
        }
        // 获取拦截器链
        List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
        Object retVal;
        // Check whether we only have one InvokerInterceptor: that is,
        // no real advice, but just reflective invocation of the target.
        if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
            // We can skip creating a MethodInvocation: just invoke the target directly.
            // Note that the final invoker must be an InvokerInterceptor, so we know
            // it does nothing but a reflective operation on the target, and no hot
            // swapping or fancy proxying.
            // 如果拦截链为空，则激活原方法
            retVal = methodProxy.invoke(target, args);
        }
        else {
            // We need to create a method invocation...
            // 进入链
            retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
        }
        retVal = processReturnType(proxy, target, method, retVal);
        return retVal;
    }
    finally {
        if (target != null) {
            releaseTarget(target);
        }
        if (setProxyContext) {
            // Restore old proxy.
            AopContext.setCurrentProxy(oldProxy);
        }
    }
}
```

上述的实现与JDK方式实现代理中的invoke方法大同小异，都是首先构造，然后封装此链进行串联调用，稍有些区别就是在JDK中直接构造ReflectiveMethodInvocation，而在CGLIB中是使用CglibMethodInvocation。CglibMethodInvocation继承自ReflectiveMethodInvocation，但是proceed方法并没有重写。

## 四、静态AOP

加入时织入（Load-Time Weaving，LTW）指的是在虚拟机载入字节码文件时动态注入AspectJ切面，即在虚拟机启动时通过改变对象字节码的方式来完成对目标对象的增强，它与动态代理相比具有更高的效率，因为在动态代理调用的过程中，还需要一个动态创建代理类并代理目标对象的步骤，而静态代理则是启动时便完成了字节码的增强，当系统再次调用目标类时与调用正常的类并无差别，所以效率上会相对高些。

动态代理与静态代理的比较：

- 实现方式：静态代理在生成class文件时，已把增强写入；而动态代理是在运行期间创建一个新的代理对象，并进行增强。
- 性能：静态代理稍好一些，因为少了代理的步骤。
- 灵活性：灵活性一般来说与性能是反比例关系，静态代理由于是直接写死class文件，当代码数量庞大且逻辑复杂时，静态代理将显得有点臃肿且乏力；而动态代理是运行期间生成代理，可以做很多事先class文件里面没有说明的事情，所以动态代理一般用的比较多。

## 五、总结

个人对于代理模式的总结，不管是动态代理还是静态代理，所谓的Spring AOP也罢，得出结论就是

- 看山是山，看水是水——低级阶段。
- 看山不是山，看水不是水——中级阶段。
- 看山是山，看水是水——高级阶段。

当你经历了中级阶段后，对于Spring AOP的理解就算是入门了。