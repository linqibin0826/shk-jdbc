package com.linqibin.jdbc.curd.preparedStatement;

import com.linqibin.jdbc.curd.domain.Customer;
import com.linqibin.jdbc.curd.util.JDBCUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 *
 * @author lqb
 * @date 2022/7/23
 */
public class CustomerQuery {

    public Customer queryCustomer(String sql, Object ...args) {
        try {
            Connection conn = JDBCUtil.getConnection();
            // 准备预编译sql
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                // 为每一个参数赋值
                ps.setObject(i + 1, args[i]);
            }
            // 执行查询
            ResultSet rs = ps.executeQuery();
            // 封装结果集
            ResultSetMetaData metaData = rs.getMetaData();
            if (rs.next()) {
                Customer result = new Customer();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object value = rs.getObject(columnLabel);
                    // 利用反射封装
                    try {
                        Field field = Customer.class.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(result, value);
                    } catch (Exception e) {}
                }
                return result;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Test
    public void test01() {
        String sql = "select * from customers where id = ?";
        Customer customer = queryCustomer(sql, 1);
        System.out.println(customer);
    }
}
