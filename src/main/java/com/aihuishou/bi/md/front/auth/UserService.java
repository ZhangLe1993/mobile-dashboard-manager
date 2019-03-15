package com.aihuishou.bi.md.front.auth;

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

    public User findByOpenId(String openId) throws SQLException {
        String sql="select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode from user where open_id=?";
        return new QueryRunner(dataSource).query(sql,new BeanHandler<User>(User.class),openId);
    }

    public User findById(Long id) throws SQLException {
        String sql="select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode from user where id=?";
        return new QueryRunner(dataSource).query(sql,new BeanHandler<User>(User.class),id);
    }

    public List<User> all(){
        String sql="select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode from user";
        try {
            return new QueryRunner(dataSource).query(sql,new BeanListHandler<User>(User.class));
        } catch (SQLException e) {
            log.error("",e);
            return null;
        }
    }

    public void updateActivationCode(Long uid, String code) throws SQLException {
        String sql="update user set activation_code=? where id=?";
        new QueryRunner(dataSource).update(sql,code,uid);
    }

    public void updateActive(Long uid, boolean enable) throws SQLException {
        String sql="update user set active=? where id=?";
        new QueryRunner(dataSource).update(sql,enable,uid);
    }

    public User checkActive(String openId){
        User user = null;
        try {
            user = findByOpenId(openId);
        } catch (SQLException e) {
            log.error("openId:"+openId,e);
        }
        if (user == null) {//未登记的公司职员
            log.warn("UnknowEmployeeException openId:"+openId);
            throw new UnknowEmployeeException();
        } else if (!user.getActive()) {//需要激活
            log.warn("UserNotActivationException openId:"+openId);
            throw new UserNotActivationException();
        } else {
            return user;
        }
    }
}
