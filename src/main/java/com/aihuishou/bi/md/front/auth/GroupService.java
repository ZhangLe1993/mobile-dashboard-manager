package com.aihuishou.bi.md.front.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class GroupService {

    @Resource
    private DataSource dataSource;

    public List<String> list(String openId) throws SQLException {
        String sql = "SELECT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u JOIN data_group g ON u.group_id=g.id WHERE u.employee_no = (SELECT employee_no FROM user WHERE open_id=?)";
        List<Group> groups = new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class), openId);
        return groups.parallelStream().map(Group::getGroupKey).collect(toList());
    }


    public List<String> getFieldByEmployeeNo(String employeeNo) throws SQLException {
        String sql = "SELECT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u JOIN data_group g ON u.group_id=g.id WHERE u.employee_no = ?";
        List<Group> groups = new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class), employeeNo);
        return groups.parallelStream().map(Group::getGroupKey).collect(toList());
    }

    public List<Group> getByEmployeeNo(String employeeNo) throws SQLException {
        String sql = "SELECT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u JOIN data_group g ON u.group_id=g.id WHERE u.employee_no = ?";
        List<Group> groups = new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class), employeeNo);
        return groups == null ? new ArrayList<>() : groups;
    }


    public List<Group> all() {
        String sql = "SELECT DISTINCT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u JOIN data_group g ON u.group_id=g.id;";
        try {
            return new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class));
        } catch (SQLException e) {
            log.error("", e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public int insertUserGroup(String employeeNo, List<Integer> ids) throws SQLException {
        String sql = "select distinct group_id from user_group where employee_no=?;";
        List<Map<String, Object>> rs = new QueryRunner(dataSource).query(sql, new MapListHandler(), employeeNo);
        List<Integer> exIds = rs.stream().map(it -> Integer.parseInt(it.get("group_id").toString())).collect(Collectors.toList());

        List<Integer> newCopy  = new ArrayList<>(ids.size());
        CollectionUtils.addAll(newCopy, new Object[ids.size()]);
        Collections.copy(newCopy, ids);

        if(exIds != null && exIds.size() != 0) {
            ids.removeAll(exIds);
            //要删除的权限
            exIds.removeAll(newCopy);
            if(exIds.size() > 0) {
                sql = "DELETE FROM user_group WHERE employee_no = '" + employeeNo + "' and group_id = ?;";
                Object[][] dp = new Object[exIds.size()][1];
                for (int i = 0; i < exIds.size(); i++) {
                    dp[i][0] = exIds.get(i);
                }
                new QueryRunner(dataSource).batch(sql, dp);
            }
        }
        if(ids.size() == 0) {
            //已经有了这些权限
            return 1;
        }
        sql = "INSERT INTO user_group(employee_no, group_id) VALUES ('" + employeeNo + "',?);";
        Object[][] params = new Object[ids.size()][1];
        for (int i = 0; i < ids.size(); i++) {
            params[i][0] = ids.get(i);
        }
        int [] res = new QueryRunner(dataSource).batch(sql, params);
        if(res.length == ids.size()) {
            //赋权成功
            return 0;
        }
        return 2;
    }
}
