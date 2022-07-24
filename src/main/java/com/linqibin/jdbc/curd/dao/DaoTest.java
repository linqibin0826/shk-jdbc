package com.linqibin.jdbc.curd.dao;

import com.linqibin.jdbc.curd.dao.impl.CustomerDAO;
import com.linqibin.jdbc.curd.dao.impl.CustomerDAOImpl;
import com.linqibin.jdbc.curd.domain.Customer;
import com.linqibin.jdbc.curd.util.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

/**
 * 测试功能
 * @author lqb
 * @date 2022/7/24
 */
public class DaoTest {

    @Test
    public void test01() {
        Connection conn = JDBCUtil.getConnection();
        CustomerDAO dao = new CustomerDAOImpl();
        List<Customer> all = dao.getAll(conn);
        JDBCUtil.closeResource(null, conn);
    }
}
