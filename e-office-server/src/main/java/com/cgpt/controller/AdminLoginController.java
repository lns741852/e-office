package com.cgpt.controller;

import com.cgpt.pojo.Admin;
import com.cgpt.pojo.AdminLogin;
import com.cgpt.pojo.RespBean;
import com.cgpt.service.IAdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;


/**
 * 登入
 *
 * @author Jay
 */
@RestController
public class AdminLoginController {

    @Autowired
    private IAdminService adminService;

    @ApiOperation(value = "登入後返回token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLogin adminLogin, HttpServletRequest request){
        return adminService.login(adminLogin.getUsername(),adminLogin.getPassword(),adminLogin.getCode(),request);
    }

    @ApiOperation(value = "獲取用戶訊息")
    @GetMapping("/admin/info")
    public Optional<Admin> getAdminInfo(Principal principal){

        //使用Optional取代以下內容
        return Optional.ofNullable(principal)
                .map(Principal::getName)
                .map(adminService::getAdminByUserName)
                .map(admin -> {
                    admin.setPassword(null);
                    admin.setRoles(adminService.getRolesByAdminId(admin.getId()));
                    return admin;
                });

        //        if (principal == null){
        //            return null;
        //        }
        //
        //        String username = principal.getName();
        //        Admin admin = adminService.getAdminByUserName(username);
        //        admin.setPassword(null);
        //        admin.setRoles(adminService.getRolesByAdminId(admin.getId()));
        //        return admin;
    }

    @ApiOperation(value = "登出")
    @PostMapping("/logout")
    public RespBean logout(){
        return RespBean.success("註銷成功！");
    }
}
