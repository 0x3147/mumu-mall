package com.kk.mall.controller;

import com.kk.mall.common.ApiRestResponse;
import com.kk.mall.common.Constant;
import com.kk.mall.exception.ImoocMallExceptionEnum;
import com.kk.mall.model.pojo.User;
import com.kk.mall.model.request.AddCategoryReq;
import com.kk.mall.service.CategoryService;
import com.kk.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class CategoryController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session, AddCategoryReq addCategoryReq) {
        if (addCategoryReq.getName() == null || addCategoryReq.getType() == null || addCategoryReq.getOrderNum() == null || addCategoryReq.getParentId() == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NANE_NOT_NULL);
        }
        User currenctUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (currenctUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        boolean adminRole = userService.checkAdminRole(currenctUser);
        if (adminRole) {
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }
}
