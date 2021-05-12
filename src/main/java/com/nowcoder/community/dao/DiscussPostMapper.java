package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //分页查询 动态sql
    //offset:每页起始行的行号
    //limit:每页最多显示多少条数据
    List<DiscussPost> selectDisscussPosts(int userId, int offset, int limit);

    //Param注解用于给参数起别名，如果方法只有一个参数并且在<if>里使用就必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    //增加帖子的方法
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子详情
    DiscussPost selectDisscussPostById(int id);

    //comment_count 冗余帖子数量
    int updateCommentCount(int id, int commentCount);


}
