//package org.example.mapper;
//
//import org.apache.ibatis.annotations.*;
//import org.example.entity.PaymentRecord;
//import org.springframework.security.core.parameters.P;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Mapper
//public interface PaymentRMapper {
//    @Select("SELECT * FROM PaymentRecord WHERE UserId = #{userId} ORDER BY PayTime DESC")
//    List<PaymentRecord> getPaymentRByUser(@Param("userId") int userId);
//
//    @Select(("select* from PaymentRecord where payTime=#{time}"))
//    List<PaymentRecord> getPaymentRByTime(@Param("payTime")LocalDateTime time);
////    // 根据支付金额范围查询支付记录
////    @Select("SELECT * FROM PaymentRecord WHERE PayMoney BETWEEN #{minAmount} AND #{maxAmount}")
////    List<PaymentRecord> getPaymentsWithinRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
//}
//
