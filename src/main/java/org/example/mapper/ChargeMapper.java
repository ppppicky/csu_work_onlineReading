package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.dto.ChargeDTO;
import java.util.Optional;

@Mapper
public interface ChargeMapper {

    // 查询书籍的收费信息
    @Select("SELECT cm_id AS cmId, book_id AS bookId, free_chapter AS freeChapter, charge_money AS chargeMoney, is_vip_free AS isVipFree " +
            "FROM charge_management WHERE book_id = #{bookId}")
    Optional<ChargeDTO> getChargeInfoByBookId(@Param("bookId") int bookId);

    // 更新书籍的收费信息
    @Update("UPDATE charge_management SET free_chapter = #{freeChapter}, charge_money = #{chargeMoney}, is_vip_free = #{isVipFree} WHERE book_id = #{bookId}")
    void updateChargeDetails(ChargeDTO chargeDTO);

    // 插入新的收费信息
    @Insert("INSERT INTO charge_management (book_id, free_chapter, charge_money, is_vip_free) " +
            "VALUES (#{bookId}, #{freeChapter}, #{chargeMoney}, #{isVipFree})")
    @Options(useGeneratedKeys = true, keyProperty = "cmId")
    void insertChargeDetails(ChargeDTO chargeDTO);

    // 删除书籍收费信息（如果设置为免费）
    @Delete("DELETE FROM charge_management WHERE book_id = #{bookId}")
    void deleteChargeByBookId(@Param("bookId") int bookId);

    // 更新书籍是否收费（Book 表）
    @Update("UPDATE book SET is_charge = #{isCharge} WHERE book_id = #{bookId}")
    void updateBookChargeStatus(@Param("bookId") int bookId, @Param("isCharge") int isCharge);
}
