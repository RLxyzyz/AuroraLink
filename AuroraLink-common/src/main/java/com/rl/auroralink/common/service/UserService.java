package com.rl.auroralink.common.service;


import com.rl.auroralink.common.model.User;

/**
 * 用户服务
 *
 */
public interface UserService {

    /**
     * 获取用户
     *
     * @param user
     * @return
     */
    User getUser(User user);

    /**
     * 用于测试 mock 接口返回值
     *
     * @return
     */
    default short getNumber() {
        return 1;
    }
}
