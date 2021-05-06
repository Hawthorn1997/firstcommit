package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;



/**
 * 持有用户信息，用来代替session对象
 */
@Component
public class HostHolder {

    //多线程相关
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    //清理
    public void clear(){
        users.remove();
    }


}