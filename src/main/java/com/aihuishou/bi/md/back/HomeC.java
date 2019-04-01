package com.aihuishou.bi.md.back;

import com.aihuishou.bi.md.front.chart.gmv.GmvService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/back")
public class HomeC {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private GmvService gmvService;

    @RequestMapping("/clear_md")
    public void clearCache() {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.flushDb();
                return null;
            }
        });
    }

    @RequestMapping("/gmv_data_date")
    public String updateGmvDataDate(@RequestParam(value = "date", required = false) String date) throws ParseException {
        clearCache();
        gmvService.setLastDataDate(date);
        return new SimpleDateFormat("yyyy-MM-dd").format(gmvService.getLastDataDate());
    }

    @RequestMapping("")
    public String index() {
        return "index.html";
    }
}
