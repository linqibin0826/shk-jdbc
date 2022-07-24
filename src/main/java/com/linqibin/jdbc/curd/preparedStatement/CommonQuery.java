package com.linqibin.jdbc.curd.preparedStatement;

import com.linqibin.jdbc.curd.domain.Customer;
import com.linqibin.jdbc.curd.domain.Order;
import com.linqibin.jdbc.curd.util.JDBCUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用查询方法
 * @author lqb
 * @date 2022/7/23
 */
@SuppressWarnings("all")
public class CommonQuery {

    /**
     * 获取一个对象的通用查询方法
     * @param sql
     * @param tClass
     * @param args
     * @param <T>
     * @return
     */
    public <T> T queryForObject(String sql, Class<T> tClass, Object ...args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConnection();
            // 准备预编译sql
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                // 为每一个参数赋值
                ps.setObject(i + 1, args[i]);
            }
            // 执行查询
            rs = ps.executeQuery();
            // 封装结果集
            ResultSetMetaData metaData = rs.getMetaData();
            if (rs.next()) {
                T result = tClass.newInstance();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object value = rs.getObject(columnLabel);
                    // 利用反射封装
                    try {
                        Field field = tClass.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(result, value);
                    } catch (Exception e) {}
                }
                return result;
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, ps, rs);
        }
        return null;
    }

    /**
     * 获取多条记录的通过查询方法
     * @param sql
     * @param tClass
     * @param args
     * @param <T>
     * @return
     */
    public <T> List<T> queryForList(String sql, Class<T> tClass, Object ...args) {
        List<T> resultList = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            resultList = new ArrayList<>();
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            // 设置参数
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            // 处理结果集
            while (rs.next()) {
                T item = tClass.newInstance();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i + 1);
                    Object value = rs.getObject(columnLabel);
                    try {
                        Field field = tClass.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(item, value);
                    } catch (Exception e) {
                    }
                }
                resultList.add(item);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn, ps, rs);
        }
        return resultList;
    }

    @Test
    public void testQueryOneObject() {
        Customer customer = queryForObject("select * from customers where id = ?", Customer.class, 1);
        System.out.println(customer);
    }

    @Test
    public void testQueryObjects() {
        List<Customer> query = queryForList("select * from customers", Customer.class);
        query.forEach(System.out::println);
        System.out.println("--------------------分割线-------------------------");
        List<Order> orders = queryForList("select order_id orderId, order_name orderName, order_date orderDate from `order`", Order.class);
        orders.forEach(System.out::println);
    }


}
