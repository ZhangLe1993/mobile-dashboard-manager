package com.aihuishou.bi.md;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexC {
    @RequestMapping("_health_check")
    public void healthCheck(){

    }
}
