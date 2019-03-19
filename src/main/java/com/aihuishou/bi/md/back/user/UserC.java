package com.aihuishou.bi.md.back.user;

import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/back/user")
public class UserC {

    @Resource
    private UserService userService;

    @RequestMapping(value = "", produces = "application/json;charset=utf-8")
    public ResponseEntity users() {
        List<User> users = userService.all();
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @GetMapping("/ban")
    public void disable(@RequestParam("uid") Long uid, @RequestParam("enable") Boolean enable) throws SQLException {
        userService.updateEnable(uid, enable);
    }
}
