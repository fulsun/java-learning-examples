package pers.fulsun.mybatis._01helloworld.Mapper;

import pers.fulsun.mybatis._01helloworld.pojo.User;

public interface UserMapper {
    // 根据id查询用户
    User selectUserById(int id);
}