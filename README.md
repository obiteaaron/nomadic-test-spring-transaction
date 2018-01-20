## 说明

此工程用于验证spring事务`tx:annotation-driven`在使用中可能存在的问题。起因是在某实际工程中，配置了多个`tx:annotation-driven`以及多个`org.springframework.jdbc.datasource.DataSourceTransactionManager`导致最终事务失效的问题。

## 重点

1. `<tx:annotation-driven />` 只需要配置一个即可，如果配置了多个，事务以第一个为准，其它属性项，以级别最高的为准
2. 如果使用默认值`transactionManager`则`<tx:annotation-driven />`中可以不指定属性`transaction-manager`
3. 第一个`<tx:annotation-driven />`加载时，指定的事务会成为默认事务，无论指定的事务管理器的名称是否是`transactionManager`
4. 使用注解`@Transactional`时，如果不指定具体的事务管理器，会使用上面这个默认事务处理器
5. 如果在多个配置文件中配置了`<tx:annotation-driven />`，无法保证启动时的加载顺序，此时默认事务管理器就有可能会变
6. 为保证使用的事务永远是期望的，应该做到在使用注解`@Transactional`时，指明使用的事务处理器的id、name或者qualifier

## 2018-01-21补充

**最上面说，遇到事务不生效的问题，那么这个问题的根源在下面说明一下**

### 简单原因：

使用了下面的配置

```xml
<beans>
    <!--省略其它配置-->
    <tx:advice id="1" transaction-manager="transactionManagerTest">
        <tx:attributes>
            <tx:method name="*" rollback-for="Exception"/>
        </tx:attributes>
    </tx:advice>
    
    <aop:config>
        <aop:pointcut id="interceptorPointCuts"
                      expression="execution(* org.nomadic.test.service.WordTestService.*(..))"/>
        <aop:advisor advice-ref="2"
                     pointcut-ref="interceptorPointCuts"/>
    </aop:config>
</beans>
```

### 详细原因：

在 `tx:advice` 配置中，无法指定 `tx:attribute` 的 `qualifier` ，导致创建 `TransactionInterceptor` 时，对其 `Properties` 没有设置 `qualifier` ，而是直接将引用的类写入了其父类 `TransactionAspectSupport` 的属性 `transactionManagerCache` 中，而 `transactionManagerCache` 中的值默认都是 `SoftReference` ( 只有 `SOFT` 和 `WEAK` 可选，默认 `SOFT` )，导致当内存不足时，此缓存中的事务配置会被GC回收掉。因此在执行事务的时候，会重新获取，由于没有 `qualifier` ，又没有类名（配置的时候只配置了具体的实例对象），所以 Spring 会通过类型获取，即获取 `PlatformTransactionManager` 的实现，而此时，多事务的情况下，就会有多个 `PlatformTransactionManager` 的 `Bean` 即会出现异常`org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type [org.springframework.transaction.PlatformTransactionManager] is defined: expected single matching bean but found 2: transactionManager,transactionManagerTest`。

### 解决方案：

#### 方案一：

使用配置bean的方式配置一个 `TransactionInterceptor` ，不使用注解 `tx:advice` 。
```xml
<beans>
    <!--省略其它配置-->
    <tx:advice id="1" transaction-manager="transactionManagerTest">
        <tx:attributes>
            <tx:method name="*" rollback-for="Exception"/>
        </tx:attributes>
    </tx:advice>
    
    <!--使用2这个bean替换1这个bean就好了。毕竟注解只是为了配置方便，但是功能有缺陷-->
    <bean id="2" class="org.springframework.transaction.interceptor.TransactionInterceptor">
        <property name="transactionAttributeSource">
            <bean class="org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource">
                <property name="nameMap">
                    <map>
                        <entry key="*">
                            <bean class="org.springframework.transaction.interceptor.RuleBasedTransactionAttribute">
                                <!--指定事务-->
                                <property name="qualifier" value="transactionManagerTest"/>
                                <property name="rollbackRules">
                                    <list >
                                        <bean class="org.springframework.transaction.interceptor.RollbackRuleAttribute">
                                            <constructor-arg type="java.lang.Class" value="java.lang.Exception"/>
                                        </bean>
                                    </list>
                                </property>
                            </bean>
                        </entry>
                    </map>
                </property>
            </bean>
        </property>
    </bean>
</beans>
```

#### 方案二：

使用全注解的方式，原因如下。
1. spring 在解析注解 `@Transactional` 的时候，会将 `value` 的值写入到 `qualifier` 中，和上面 `2` 的配置一样。如果不写，则是空的（会使用默认事务）。所以建议 `@Transactional` 一定要带上 `value` 属性，指定具体的事务管理器。
2. `<tx:annotation-driven transaction-manager="transactionManager"/>` 会将属性 `transaction-manager` 的值设置到 `TransactionInterceptor` 的父类 `TransactionAspectSupport` 的 `transactionManagerBeanName` 属性中。如果不指定 `transaction-manager` ，设置的也是 `transactionManager` 。
3. 事务执行的时候，在 `TransactionAspectSupport#determineTransactionManager()` 中，先检查 `qualifier` ，再检查 `transactionManagerBeanName` ，如果这两个都没有值，则获取配置的 `TransactionManager bean` 对象。
4. 如上面原因说的那样，再结合以上三点，由于这个 `TransactionManager bean` 对象是放在 `SoftReference` 中的，随时可能会丢失。所以通过 `tx:advice` 配置的事务是严重不靠谱的，它会丢失`bean`，在单事务管理下没有问题，但是在多事务管理下，就会出现 `org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type [org.springframework.transaction.PlatformTransactionManager] is defined: expected single matching bean but found 2: transactionManager,transactionManagerTest` 这样的异常。所以使用全注解的方式，可以保证事务执行的正确性，另外，可以避免 `tx:advice` 在多事务下出现的异常。
5. `<tx:annotation-driven transaction-manager="transactionManager"/>` 在 xml 中可以配置多个，但是只有第一个会生效，后面的不会创建新的 `TransactionInterceptor` 实例。

## 总结

这个问题总算是解决了，不再出现事务错误的问题了。谨记一点，能使用注解就使用注解 `<tx:annotation-driven transaction-manager="transactionManager"/>` 和 `@Transactional`，避免使用 `<tx:advice />` 这个配置项。
