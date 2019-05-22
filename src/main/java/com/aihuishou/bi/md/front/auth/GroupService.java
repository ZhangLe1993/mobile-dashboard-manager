package com.aihuishou.bi.md.front.auth;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
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

    public List<Group> all() {
        String sql = "SELECT DISTINCT u.group_id AS id,g.group_key AS groupKey,g.description FROM user_group u LEFT JOIN md.group g ON u.group_id=g.id;";
        try {
            return new QueryRunner(dataSource).query(sql, new BeanListHandler<Group>(Group.class));
        } catch (SQLException e) {
            log.error("", e);
            return new ArrayList<>();
        }
    }
}
