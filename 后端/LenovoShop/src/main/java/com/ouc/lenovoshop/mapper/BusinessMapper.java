package com.ouc.lenovoshop.mapper;

import com.ouc.lenovoshop.entity.Business;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//business相关接口
public interface BusinessMapper {
    int insert(Business business);
    int deleteById(Integer id);
    int updateById(Business business);
    Business selectById(Integer id);
    List<Business> selectAll(Business business);

    @Select("select id, name as username, '' as password, name, phone, email, '' as avatar, '' as role, description, status from business where name = #{username}")
    Business selectByUsername(String username);
}