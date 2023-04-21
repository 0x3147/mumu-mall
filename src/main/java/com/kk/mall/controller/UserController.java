package com.kk.mall.controller;

import com.kk.mall.common.ApiRestResponse;
import com.kk.mall.common.Constant;
import com.kk.mall.exception.ImoocMallException;
import com.kk.mall.exception.ImoocMallExceptionEnum;
import com.kk.mall.model.pojo.User;
import com.kk.mall.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }

    /**
    * @desc 注册功能
    * @Author 康佳星
    * @Date 2023-04-20 20:19:54
    * @param userName 用户名
    * @param password 密码
    */
    @PostMapping("/register")
    @ResponseBody
    public ApiRestResponse register(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password) throws ImoocMallException, NoSuchAlgorithmException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        // 密码长度检验
        if (password.length() < 8) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    /**
    * @desc 登录功能
    * @Author 康佳星
    * @Date 2023-04-20 20:20:41
    * @param userName 用户名
    * @param password 密码
    */
    @PostMapping("/login")
    @ResponseBody
    public ApiRestResponse<User> login(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password,
            HttpSession session
    ) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        // 保存用户信息时不保存密码
        user.setPassword(null);
        session.setAttribute(Constant.IMOOC_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
    * @desc 更新个性签名
    * @Author 康佳星
    * @Date 2023-04-20 20:21:17
    * @param session 服务器session
    * @param signature 个性签名
    */
    @PostMapping("/user/update")
    @ResponseBody
    public ApiRestResponse updateUserInfo(
            HttpSession session,
            @Param("signature") String signature
    ) throws ImoocMallException {
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();
        user.setId(currentUser.getId());
        user.setPersonalizedSignature(signature);
        userService.updateInformation(user);
        return ApiRestResponse.success();
    }

    /**
    * @desc 用户登出
    * @Author 康佳星
    * @Date 2023-04-20 20:21:55
    * @param session 服务器session
    */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logOut(HttpSession session) {
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
    * @desc 管理员登录
    * @Author 康佳星
    * @Date 2023-04-20 20:22:43
    * @param userName 用户名
    * @param password 密码
    */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse<User> adminLogin(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password,
            HttpSession session
    ) throws ImoocMallException {
        if (StringUtils.isEmpty(userName)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if (StringUtils.isEmpty(password)) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        User user = userService.login(userName, password);
        // 校验管理员权限
        if (userService.checkAdminRole(user)) {
            // 保存用户信息时不保存密码
            user.setPassword(null);
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
}
