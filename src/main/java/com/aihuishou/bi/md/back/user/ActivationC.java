package com.aihuishou.bi.md.back.user;

import com.aihuishou.bi.md.front.auth.SessionHelper;
import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import com.aihuishou.bi.md.utils.HttpUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/back")
public class ActivationC {

    @Resource
    private UserService userService;

    @Resource
    private SessionHelper sessionHelper;

    @PostMapping("/update-activation-code")
    public ResponseEntity update(@RequestParam("employee-no") String employeeNo) throws SQLException {
        log.info("employee-no:" + employeeNo + " update activation code");
        User user = userService.findByEmployeeNo(employeeNo);
        if (user == null) {
            user = userService.insert(employeeNo);
        }
        if (user == null) {
            return new ResponseEntity("invalid employee no", HttpStatus.FORBIDDEN);
        } else if (!user.getActive()) {//未激活可更新
            // TODO UUID.randomUUID().toString().replace("-", "").toUpperCase();
            userService.updateActivationCode(user.getId(), UUID.randomUUID().toString().replace("-", "").toUpperCase());
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity("active already,code is freeze", HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping("/merge-activation-img/{uid}")
    public void view(@PathVariable("uid") Long userId, HttpServletResponse response) throws WriterException, IOException, SQLException {
        User user = userService.findById(userId);
        String content = user.getActivationCode();//二维码内容 即 激活码
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);
        ImageWrite.writeToStream(bitMatrix, "jpg", response.getOutputStream());
    }


    /**
     * 生成小程序码
     * @param userId
     * @param response
     * @throws Exception
     */
    @RequestMapping("/activation-img/{uid}")
    public void mergeView(@PathVariable("uid") Long userId, HttpServletResponse response) throws Exception {
        String accessToken = sessionHelper.getAccessToken();
        User user = userService.findById(userId);
        String scene = user.getActivationCode().replace("-", "").toUpperCase();//二维码内容 即 激活码
        String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken;
        JSONObject params = new JSONObject();
        params.put("scene", scene);
        //params.put("page","pages/statement/index");
        byte[] result = HttpUtil.urlPost(url, params);
        //byte[] photo = result.getBytes();
        OutputStream os  = null;
        InputStream fis = null;
        try{
            os = response.getOutputStream();
            int count = 0;
            fis = new ByteArrayInputStream(result);
            byte[] buffer = new byte[1024];
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally {
            if(fis != null) {
                fis.close();
            }
            if(os != null) {
                os.close();
            }
        }
    }

}
