package pers.fulsun.mybatis._01helloworld;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.fulsun.mybatis._01helloworld.Mapper.UserMapper;
import pers.fulsun.mybatis._01helloworld.pojo.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

/**
 * 接口式编程:
 * 1.指定xml配置文件的位置，并使用Resources类加载
 * 2.使用SqlSessionFactoryBuilder创建SqlSessionFactory
 * 3.使用SqlSessionFactory创建SqlSession对象
 * 4.使用SqlSession获取Mapper接口的实例
 * mapper接口没有实现类，但是mybatis会为这个接口生成一个代理对象。
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
            // 会为接口自动的创建一个代理对象，代理对象去执行增删改查方法
            UserMapper mapper = session.getMapper(UserMapper.class);
            System.out.println(mapper.getClass());
            User user = mapper.selectUserById(1);
            System.out.println(user);
        }
        testBatchInsert(sqlSessionFactory);
    }

    private static void testBatchInsert(SqlSessionFactory sqlSessionFactory) {
        CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(() -> {
            // 1. 启用批处理执行器
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            UserMapper mapper = sqlSession.getMapper(UserMapper.class);

            final int batchSize = 1000; // 保持每1000条提交一次
            Long startTime = System.currentTimeMillis();

            try {
                for (int i = 1000000; i < 2000000; i++) {
                    User user = new User();
                    user.setName("name" + i);
                    user.setAge(10);
                    mapper.insertUser(user);

                    // 2. 启用分批提交和缓存清理
                    if ((i + 1) % batchSize == 0) {
                        sqlSession.commit();
                        sqlSession.clearCache();  // 防止OOM
                    }
                }
                // 3. 提交最后一批
                sqlSession.commit();
            } finally {
                // 4. 确保资源释放
                sqlSession.close();
            }

            Long endTime = System.currentTimeMillis();
            LOGGER.info("优化后插入耗时：{} ms", endTime - startTime);
            countDownLatch.countDown();
        }).start();

        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("执行完毕");
    }
}
