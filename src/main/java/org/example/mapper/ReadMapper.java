//package org.example.mapper;
//
//
//import org.apache.ibatis.annotations.*;
//import org.example.entity.ReadRecord;
//
//@Mapper
//public interface ReadMapper {
//    @Select("SELECT * FROM ReadRecord WHERE UserId = #{userId} ORDER BY LastReadTime DESC LIMIT 1")
//    ReadRecord getLastReadRecordByUser(@Param("userId") int userId);
//
//    @Update("UPDATE ReadRecord SET LastReadPage = #{lastReadPage}, LastReadTime = NOW() WHERE ReadR_Id = #{readRecordId}")
//    void updateReadRecord(@Param("readRecordId") int readRecordId, @Param("lastReadPage") int lastReadPage);
//
//
//}
