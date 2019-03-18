package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.UserBanException;
import com.aihuishou.bi.md.front.auth.exception.UserNotActivationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Resource
    private DataSource dataSource;

    public User findByActiveCode(String activeCode) throws SQLException {
        String sql = "select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode,enable from user where activation_code=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), activeCode);
    }

    public User findByOpenId(String openId) throws SQLException {
        String sql = "select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode,enable from user where open_id=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), openId);
    }

    public User findById(Long id) throws SQLException {
        String sql = "select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode,enable from user where id=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), id);
    }

    public List<User> all() {
        String sql = "select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode,enable from user";
        try {
            return new QueryRunner(dataSource).query(sql, new BeanListHandler<User>(User.class));
        } catch (SQLException e) {
            log.error("", e);
            return null;
        }
    }

    public void updateActivationCode(Long uid, String code) throws SQLException {
        String sql = "update user set activation_code=? where id=?";
        new QueryRunner(dataSource).update(sql, code, uid);
    }

    public void updateActive(Long uid, boolean enable) throws SQLException {
        String sql = "update user set enable=? where id=?";
        new QueryRunner(dataSource).update(sql, enable, uid);
    }

    public User checkActive(String openId) {
        User user = null;
        try {
            user = findByOpenId(openId);
        } catch (SQLException e) {
            log.error("openId:" + openId, e);
        }
        if (user == null || !user.getActive()) {//需要激活
            log.warn("UserNotActivationException openId:" + openId);
            throw new UserNotActivationException();
        } else if (!user.getEnable()) {//用户被禁止
            log.warn("UserBanException openId:"+openId);
            throw new UserBanException();
        } else {
            return user;
        }
    }

    /**
     * 执行激活
     *
     * @param openId
     * @param activationCode
     * @throws SQLException
     */
    public boolean active(String openId, String activationCode) throws SQLException {
        String sql = "update user set open_id=?,active=1 where activation_code=? and active=0 and enable=1";
        return new QueryRunner(dataSource).update(sql, openId, activationCode) > 0;
    }
}
