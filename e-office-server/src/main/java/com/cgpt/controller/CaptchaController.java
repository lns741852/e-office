package com.cgpt.controller;

import com.cgpt.utils.RedisUtils;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.cgpt.config.captcha.CaptchaConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;

@RestController
public class CaptchaController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "驗證碼")
    @GetMapping(value = "/captcha",produces = "image/jpeg")
    public void captcha(HttpServletRequest request, HttpServletResponse response){
        //定义response输出类型为image/jpeg
        response.setDateHeader("Expires",0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        //---------------------------生成验证码----------------------
        String text = defaultKaptcha.createText();
        System.out.println("驗證碼:  " + text);


        //redis 取代 session
        redisUtils.set("captcha"+request.getSession().getId(),text);
        //request.getSession().setAttribute("captcha",text);

        //生成圖片
        BufferedImage image = defaultKaptcha.createImage(text);
        try (
                BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream())
        ){

            //寫入圖片中
            ImageIO.write(image,"jpg",outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
