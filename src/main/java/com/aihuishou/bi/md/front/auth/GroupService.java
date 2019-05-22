package com.aihuishou.bi.md.front.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class GroupService {

    @Resource
    private DataSource dataSource;

    public List<String> list(String openId) throws SQLException {
        String sql = "SELECT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u LEFT JOIN md.group g ON u.group_id=g.id WHERE u.employee_no = (SELECT employee_no FROM user WHERE open_id=?)";
        List<Group> groups = new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class), openId);
        return groups.parallelStream().map(Group::getGroupKey).collect(toList());
    }


    public List<String> getFieldByEmployeeNo(String employeeNo) throws SQLException {
        String sql = "SELECT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u LEFT JOIN md.group g ON u.group_id=g.id WHERE u.employee_no = ?";
        List<Group> groups = new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class), employeeNo);
        return groups.parallelStream().map(Group::getGroupKey).collect(toList());
    }

    public List<Group> getByEmployeeNo(String employeeNo) throws SQLException {
        String sql = "SELECT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u LEFT JOIN md.group g ON u.group_id=g.id WHERE u.employee_no = ?";
        List<Group> groups = new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class), employeeNo);
        return groups == null ? new ArrayList<>() : groups;
    }


    public List<Group> all() {
        String sql = "SELECT DISTINCT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u LEFT JOIN md.group g ON u.group_id=g.id;";
        try {
            return new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class));
        } catch (SQLException e) {
            log.error("", e);
            return new ArrayList<>();
        }
    }

    public int insertUserGroup(String employeeNo, List<Integer> ids) throws SQLException {
        String sql = "select group_id from user_group where employee_no=?";
        List<Integer> exIds = new QueryRunner(dataSource).query(sql, new BeanListHandler<Integer>(Integer.class), employeeNo);
        if(exIds != null && exIds.size() != 0) {
            ids.removeAll(exIds);
            if(ids.size() == 0) {
                //已经有了这些权限
                return 1;
            }
            sql = "insert into user_group(employee_no, group_id) values ('" + employeeNo + "',?);";
            Object[][] params = new Object[ids.size()][1];
            for (int i = 0; i < ids.size(); i++) {
                params[i][0] = ids.get(i);
            }
            int [] res = new QueryRunner(dataSource).batch(sql, params);
            if(res.length == ids.size()) {
                //赋权成功
                return 0;
            }
        }
        //赋权失败
        return 2;
    }
}
