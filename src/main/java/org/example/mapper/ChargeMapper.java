//package org.example.mapper;
//
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//import org.apache.ibatis.annotations.Update;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
//@Mapper
//public interface ChargeMapper {
//
//    @Select("SELECT BookId, FreePage, ChargeMoney, Is_VIPFree FROM ChargeManagement WHERE CM_Id = #{cmId}")
//    Map<String,Object>getChargeInfo(@Param("cmId") int cmId);
//
//    @Update("ChargeManagement SET FreePage = #{freePage}, ChargeMoney = #{chargeMoney}, Is_VIPFree = #{isVipFree} WHERE BookId = #{bookId}")
//    void updateChargeDetails(@Param("bookId") int bookId, @Param("freePage") int freePage,
//                             @Param("chargeMoney") BigDecimal chargeMoney, @Param("isVipFree") boolean isVipFree);
//}
