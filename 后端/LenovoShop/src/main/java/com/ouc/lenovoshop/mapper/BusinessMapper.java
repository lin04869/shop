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

    @Select("select * from business where username = #{username}")
    Business selectByUsername(String username);
}