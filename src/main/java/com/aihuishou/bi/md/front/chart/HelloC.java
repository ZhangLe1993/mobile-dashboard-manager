package com.aihuishou.bi.md.front.chart;

import com.aihuishou.bi.md.front.auth.Sid;
import com.aihuishou.bi.md.front.auth.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/front")
public class HelloC {

    @RequestMapping("/hello")
    public ResponseEntity hello(@Sid User user){
        return new ResponseEntity("hello world "+user.getName(),HttpStatus.OK);
    }
}
