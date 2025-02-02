package org.example.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {
    @Select("SELECT BookId, BookName, Author, BookCover FROM Book WHERE BookTypeId = #{bookTypeId}")
    List<Map<String, Object>> getBooksByCategory(@Param("bookTypeId") int bookTypeId);

    @Update("UPDATE Book SET Is_Charge = #{isCharge} WHERE BookId = #{bookId}")
    void updateBookChargeStatus(@Param("bookId") int bookId, @Param("isCharge") boolean isCharge);

    @Delete("delete from Book where BookId =#{bookId}")
    void deleteBook(@Param("bookId")int bookId);
}
