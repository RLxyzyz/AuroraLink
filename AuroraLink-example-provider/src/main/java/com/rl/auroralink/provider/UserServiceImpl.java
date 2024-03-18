package com.rl.auroralink.provider;

import com.rl.auroralink.common.model.User;
import com.rl.auroralink.common.service.UserService;


/**
 * @author 任磊
 * @version 1.0
 * @project rl-rpc
 * @description 用户服务实现类
 * @date 2024/3/16 11:18:59
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名"+user.getName());
        return user;
    }
}
