package com.aihuishou.bi.md.back.user;

import com.aihuishou.bi.md.front.auth.Group;
import com.aihuishou.bi.md.front.auth.GroupService;
import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        if(Strings.isBlank(employeeNo)) {
            groups = groupService.all();
        } else {
            groups = groupService.getByEmployeeNo(employeeNo);
        }
        Map<String,Object> root = new HashMap<>();
        root.put("data", groups);
        root.put("total", groups.size());
        return new ResponseEntity(root, HttpStatus.OK);
    }
}
