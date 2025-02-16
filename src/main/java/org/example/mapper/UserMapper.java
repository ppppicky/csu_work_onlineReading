package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.entity.Users;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {
    List<Users> selectUsers(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT UserId, UserName, UserSex, Is_VIP, UserCredit FROM Users")
    List<Map<String, Object>> getAllUsers();

    @Update("update Users set UserCredit = #{userCredit} where UserId = #{userId}")
    void updateUserCredit(@Param("userId") int userId, @Param("userCredit") BigDecimal userCredit);


    @Delete("delete from Users where UserId =#{userId}")
    void deleteUser(@Param("userId")int userId);
}
