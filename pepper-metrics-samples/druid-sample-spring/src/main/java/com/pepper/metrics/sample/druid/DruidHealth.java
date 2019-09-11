package com.pepper.metrics.sample.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.pepper.metrics.integration.druid.DruidHealthTracker;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Description:
 *
 * @author zhiminxu
 */
public class DruidHealth {

    public static void main(String[] args) throws SQLException, InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/spring-context.xml");
        context.start();
        DruidDataSource dataSource = context.getBean("dataSource", DruidDataSource.class);

        // 向DruidHealthTracker中添加数据源，即可收集健康信息
        DruidHealthTracker.addDataSource("ad", dataSource);

        Connection connection = dataSource.getConnection();
        String sql = "select * from config";
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        while (true) {
            ps = connection.prepareStatement(sql);
            resultSet = ps.executeQuery();
//            JdbcUtils.printResultSet(resultSet);
            System.out.println("test");
            Thread.sleep(ThreadLocalRandom.current().nextLong(5000));
        }
//        System.out.println(resultSet.getFetchSize());


//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                ResultSet resultSet = null;
//                try {
//                    while (true) {
//                        resultSet = ps.executeQuery();
//                        System.out.println(resultSet.getFetchSize());
//                        Thread.sleep(ThreadLocalRandom.current().nextLong(5000));
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    if (resultSet != null) {
//                        try {
//                            resultSet.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }).start();
//        }


//        ps.close();
//        connection.close();
//        Thread.sleep(Integer.MAX_VALUE);
    }
}
