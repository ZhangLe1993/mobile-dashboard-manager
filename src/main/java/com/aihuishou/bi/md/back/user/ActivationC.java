package com.aihuishou.bi.md.back.user;

import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @PostMapping("/update-activation-code")
    public ResponseEntity update(@RequestParam("user-id") Long uid) throws SQLException {
        log.info("user-id:"+uid+" update activation code");
        User user = userService.findById(uid);
        if(!user.getActive()){//未激活可更新
            userService.updateActivationCode(uid,UUID.randomUUID().toString());
            return new ResponseEntity(HttpStatus.OK);
        }else{
            return new ResponseEntity("active already,code is freeze",HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping("/activation-img/{uid}")
    public void view(@PathVariable("uid") Long userId, HttpServletResponse response) throws WriterException, IOException, SQLException {
        User user = userService.findById(userId);
        String content = user.getActivationCode();//二维码内容 即 激活码
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 400, 400,hints);
        ImageWrite.writeToStream(bitMatrix, "jpg", response.getOutputStream());
    }

    @RequestMapping("/disable/{uid}")
    public void disable(@PathVariable("uid")Long uid) throws SQLException {
        userService.updateActive(uid,false);
    }
}
