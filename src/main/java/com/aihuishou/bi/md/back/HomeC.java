package com.aihuishou.bi.md.back;

import com.aihuishou.bi.md.front.cache.CacheHolder;
import com.aihuishou.bi.md.front.chart.gmv.GmvDataDateService;
import com.aihuishou.bi.md.front.notice.FormId;
import com.aihuishou.bi.md.front.notice.SendMessJob;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/back")
public class HomeC {

    @Resource
    private SendMessJob sendMessJob;

    @Resource
    private GmvDataDateService gmvDataDateService;

    @Resource
    private CacheManager cacheManager;

    @RequestMapping("/clear_md")
    public void clearMd() {
        cacheManager.getCache(CacheHolder.CACHE_NAME).clear();
    }

    @RequestMapping("/gmv_data_date")
    public String updateGmvDataDate(@RequestParam(value = "date", required = false) String date) throws ParseException {
        clearMd();
        gmvDataDateService.setLastDataDate(date);
        return new SimpleDateFormat("yyyy-MM-dd").format(gmvDataDateService.getLastDataDate());
    }

    @RequestMapping("/form_ids")
    public List<FormId> getFormIds(@RequestParam("open-id") String openId) {
        return sendMessJob.allFormIds(openId);
    }


    @RequestMapping("")
    public String index() {
        return "index.html";
    }
}
