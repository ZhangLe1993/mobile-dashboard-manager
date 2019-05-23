package com.aihuishou.bi.md.back.user;

import com.aihuishou.bi.md.front.auth.Group;
import com.aihuishou.bi.md.front.auth.GroupService;
import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/back/user")
public class UserC {

    @Resource
    private UserService userService;

    @Resource
    private GroupService groupService;

    @RequestMapping(value = "", produces = "application/json;charset=utf-8")
    public ResponseEntity users(@RequestParam(value = "key",required = false,defaultValue = "") String key,
                                @RequestParam(value = "page_index",defaultValue = "1",required = false) int pageIndex,
                                @RequestParam(value = "page_size",defaultValue = "10",required = false) int pageSize) {
        List<User> users = userService.all(key,pageIndex,pageSize);
        Map<String,Object> root=new HashMap<>();
        root.put("data",users);
        root.put("total",userService.count(key));
        return new ResponseEntity(root, HttpStatus.OK);
    }



    @GetMapping("/ban")
    public void disable(@RequestParam("uid") Long uid, @RequestParam("enable") Boolean enable) throws SQLException {
        userService.updateEnable(uid, enable);
    }

    @RequestMapping(value = "/group/list", produces = "application/json;charset=utf-8")
    public ResponseEntity groups(@RequestParam(value = "employee_no", required = false) String employeeNo) throws SQLException {
        List<Group> groups = null;
        if(StringUtils.isBlank(employeeNo)) {
            groups = groupService.all();
        } else {
            groups = groupService.getByEmployeeNo(employeeNo);
        }
        Map<String,Object> root = new HashMap<>();
        root.put("data", groups);
        root.put("total", groups.size());
        return new ResponseEntity(root, HttpStatus.OK);
    }

    @PostMapping(value = "/empower", produces = "application/json;charset=utf-8")
    public ResponseEntity Authorized(@RequestBody String json) throws SQLException {
        if(StringUtils.isBlank(json)) {
            return new ResponseEntity(ImmutableMap.of("data","parameters is not allowed null"), HttpStatus.BAD_REQUEST);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        if(jsonObject != null) {
            String employeeNo = jsonObject.getString("employee_no");
            List<Integer> groupIds = JSONArray.parseArray(jsonObject.getString("group_ids"), Integer.class);
            if(StringUtils.isNotBlank(employeeNo) && groupIds != null) {
                int res = groupService.insertUserGroup(employeeNo, groupIds);
                if(res == 0) {
                    return new ResponseEntity(ImmutableMap.of("data","success"), HttpStatus.OK);
                } else if(res == 1) {
                    return new ResponseEntity(ImmutableMap.of("data","all group has exists or all group has removed "), HttpStatus.ACCEPTED);
                } else {
                    return new ResponseEntity(ImmutableMap.of("data","authorized to user failure"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        return new ResponseEntity(ImmutableMap.of("data","parameters is not invalid "), HttpStatus.BAD_REQUEST);
    }
}
