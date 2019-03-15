package com.aihuishou.bi.md.back.user;

import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/back")
public class UserC {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/user", produces = "application/json;charset=utf-8")
    public ResponseEntity users(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        List<User> users = userService.all();
        return new ResponseEntity(users, HttpStatus.OK);
    }
}
