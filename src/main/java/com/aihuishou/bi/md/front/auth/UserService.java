package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.UserBanException;
import com.aihuishou.bi.md.front.auth.exception.UserNotActivationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
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
                "active,activation_code as activationCode,enable,is_admin as isAdmin from user where activation_code=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), activeCode);
    }

    public User findByOpenId(String openId) throws SQLException {
        String sql = "select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode,enable,is_admin as isAdmin from user where open_id=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), openId);
    }

    public User findById(Long id) throws SQLException {
        String sql = "select id,name,employee_no as employeeNo,open_id as openId," +
                "active,activation_code as activationCode,enable,is_admin as isAdmin from user where id=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), id);
    }

    public User findByObId(String obId) throws SQLException {
        String sql = "select a.id,a.name,a.employee_no as employeeNo,a.open_id as openId," +
                "a.active,a.activation_code as activationCode,a.enable,a.is_admin as isAdmin from user a " +
                "join dim_observer_account b on a.employee_no=b.observer_account_employee_no where b.observer_account_id=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), Integer.parseInt(obId));
    }

    public List<User> all(String key,int pageIndex,int pageSize) {
        String sql = "select a.id,COALESCE(a.name,b.observer_account_user_name) as name,COALESCE(a.employee_no,b.observer_account_employee_no) as employeeNo,\n" +
                "a.open_id as openId,a.active,a.activation_code as activationCode,a.enable,a.is_admin as isAdmin \n" +
                "from user a right join dim_observer_account b on a.employee_no=b.`observer_account_employee_no` \n" +
                "where b.`observer_account_name` like ? or b.`observer_account_user_name` like ? or b.`observer_account_employee_no` like ? order by COALESCE(a.id,10000) limit ?,? ";
        try {
            key="%"+key+"%";
            int a=(pageIndex-1)*pageSize;
            return new QueryRunner(dataSource).query(sql, new BeanListHandler<User>(User.class),key,key,key,a,pageSize);
        } catch (SQLException e) {
            log.error("", e);
            return null;
        }
    }

    public void updateActivationCode(Long uid, String code) throws SQLException {
        String sql = "update user set activation_code=? where id=?";
        new QueryRunner(dataSource).update(sql, code, uid);
    }

    public void updateEnable(Long uid, boolean enable) throws SQLException {
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

    public Long count(String key) {
        String sql = "select count(*) from dim_observer_account b  \n" +
                "where b.`observer_account_name` like ? or b.`observer_account_user_name` like ? or b.`observer_account_employee_no` like ?";
        try {
            key="%"+key+"%";
            return new QueryRunner(dataSource).query(sql, new ScalarHandler<Long>(),key,key,key);
        } catch (SQLException e) {
            log.error("", e);
            return null;
        }
    }

    public User findByEmployeeNo(String employeeNo) throws SQLException {
        String sql = "select a.id,a.name,a.employee_no as employeeNo,a.open_id as openId," +
                "a.active,a.activation_code as activationCode,a.enable,a.is_admin as isAdmin from user a " +
                "where a.employee_no=?";
        return new QueryRunner(dataSource).query(sql, new BeanHandler<User>(User.class), employeeNo);
    }

    public User insert(String employeeNo) throws SQLException {
        String sql="select observer_account_user_name from dim_observer_account where observer_account_employee_no=?";
        String name = new QueryRunner(dataSource).query(sql, new ScalarHandler<String>(), employeeNo);
        if(name==null){
            return null;
        }
        sql="insert into user(name,employee_no) values(?,?)";
        new QueryRunner(dataSource).execute(sql,name,employeeNo);
        return findByEmployeeNo(employeeNo);
    }
}
