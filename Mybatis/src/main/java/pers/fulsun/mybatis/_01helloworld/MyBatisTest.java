package pers.fulsun.mybatis._01helloworld;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fulsun.mybatis._01helloworld.Mapper.UserMapper;
import pers.fulsun.mybatis._01helloworld.pojo.User;

import java.io.IOException;
import java.io.InputStream;

/**
 * 接口式编程:
 * 1.指定xml配置文件的位置，并使用Resources类加载
 * 2.使用SqlSessionFactoryBuilder创建SqlSessionFactory
 * 3.使用SqlSessionFactory创建SqlSession对象
 * 4.使用SqlSession获取Mapper接口的实例
 *   mapper接口没有实现类，但是mybatis会为这个接口生成一个代理对象。
 * 5.执行sql语句
 */
public class MyBatisTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisTest.class);
    public static void main(String[] args) throws IOException {
        // mybatis的全局配置文件：包含数据库连接池信息，事务管理器信息等...系统运行环境信息
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            //会为接口自动的创建一个代理对象，代理对象去执行增删改查方法
            UserMapper mapper = session.getMapper(UserMapper.class);
            System.out.println(mapper.getClass());
            User user = mapper.selectUserById(1);
            System.out.println(user);
        }
    }
}
