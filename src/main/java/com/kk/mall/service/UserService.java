package com.kk.mall.service;

import com.kk.mall.exception.ImoocMallException;
import com.kk.mall.model.pojo.User;

import java.security.NoSuchAlgorithmException;

public interface UserService {
    User getUser();
    void register(String userName, String password) throws ImoocMallException, NoSuchAlgorithmException;
    User login(String userName, String password) throws ImoocMallException;

    void updateInformation(User user) throws ImoocMallException;

    boolean checkAdminRole(User user);
}
