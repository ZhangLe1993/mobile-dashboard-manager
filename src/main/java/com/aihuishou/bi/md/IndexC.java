package com.aihuishou.bi.md;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexC {
    @RequestMapping("_health_check")
    public ResponseEntity healthCheck(){
        return new ResponseEntity("1.0",HttpStatus.OK);
    }
}
