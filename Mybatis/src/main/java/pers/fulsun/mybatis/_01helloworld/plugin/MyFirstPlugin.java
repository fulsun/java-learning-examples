package pers.fulsun.mybatis._01helloworld.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "parameterize", args = java.sql.Statement.class)})
public class MyFirstPlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 1. 获取原始 StatementHandler
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        // 2. 获取 BoundSql（包含 SQL 和参数）
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");

        // 3. 获取原始 SQL（带 ? 占位符）
        String originalSql = boundSql.getSql();

        // 4. 获取参数对象（如 Integer、User 等）
        Object parameterObject = boundSql.getParameterObject();

        // 5. 手动拼接带参数的 SQL（简单示例，仅支持基本类型）
        String finalSql = getFullSql(originalSql, parameterObject);

        // 6. 打印完整 SQL
        System.out.println("执行的完整 SQL：" + finalSql);

        // 7. 继续执行后续逻辑
        return invocation.proceed();
    }

    /**
     * 手动拼接带参数的 SQL（简单实现，仅支持基本类型）
     */
    private String getFullSql(String sql, Object parameterObject) {
        if (parameterObject == null) {
            return sql; // 无参数，直接返回原 SQL
        }

        // 如果是基本类型（Integer、String 等）
        if (parameterObject instanceof Integer || parameterObject instanceof Long
                || parameterObject instanceof String || parameterObject instanceof Double) {
            return sql.replaceFirst("\\?", "'" + parameterObject.toString() + "'");
        }

        // 如果是 Map 或 POJO（需要更复杂的解析）
        // 这里简单示例，实际项目建议使用第三方库（如 p6spy 或 log4jdbc）
        return sql + " [参数对象: " + parameterObject + "]";
    }


    @Override
    public Object plugin(Object target) {
        // TODO Auto-generated method stub
        //我们可以借助Plugin的wrap方法来使用当前Interceptor包装我们目标对象
        System.out.println("MyFirstPlugin...plugin:mybatis将要包装的对象"+target);
        Object wrap = Plugin.wrap(target, this);
        //返回为当前target创建的动态代理
        return wrap;
    }

    @Override
    public void setProperties(Properties properties) {
        System.out.println("插件配置的信息："+properties);
    }
}
