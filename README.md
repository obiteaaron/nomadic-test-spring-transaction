# 说明

此工程用于验证spring事务`tx:annotation-driven`在使用中可能存在的问题。起因是在某实际工程中，配置了多个`tx:annotation-driven`以及多个`org.springframework.jdbc.datasource.DataSourceTransactionManager`导致最终事务失效的问题。

## 重点

1. `<tx:annotation-driven />` 只需要配置一个即可，如果配置了多个，事务以第一个为准，其它属性项，以级别最高的为准
2. 如果使用默认值`transactionManager`则`<tx:annotation-driven />`中可以不指定属性`transaction-manager`
3. 第一个`<tx:annotation-driven />`加载时，指定的事务会成为默认事务，无论指定的事务管理器的名称是否是`transactionManager`
4. 使用注解`@Transactional`时，如果不指定具体的事务管理器，会使用上面这个默认事务处理器
5. 如果在多个配置文件中配置了`<tx:annotation-driven />`，无法保证启动时的加载顺序，此时默认事务管理器就有可能会变
6. 为保证使用的事务永远是期望的，应该做到在使用注解`@Transactional`时，指明使用的事务处理器的id、name或者qualifier
