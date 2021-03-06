package com.nowcoder.community.util;

public interface CommunityConstant {
    //激活成功
    int ACTIVATION_SUCCESS = 0;
    //重复激活
    int ACTIVATION_REPEAT = 1;
    //激活失败
    int ACTIVATION_FAILURE = 2;


    //默认状态的登录凭证的超时时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    //记住状态的登录凭证超时时间
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    //帖子实体类型
    int ENTITY_TYPE = 1;

    //帖子评论
    int ENTITY_TYPE_COMMENT = 2;

    //用户
    int ENTITY_TYPE_USER = 3;

    //kafka事件的主题
    //评论
    String TOPIC_COMMENT = "comment";
    //点赞
    String TOPIC_LIKE = "like";
    //关注
    String TOPIC_FOLLOW = "follow";


    //系统用户id
    int SYSTEM_USER_ID = 1;

    //普通用户权限
    String AUTHORITY_USER = "user";
    //管理员
    String AUTHORITY_ADMIN = "admin";
    //版主
    String AUTHORITY_MODERATOR = "moderator";

}
