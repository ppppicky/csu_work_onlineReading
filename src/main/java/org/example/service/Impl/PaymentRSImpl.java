//package org.example.service.Impl;
//
//import org.example.entity.PaymentRecord;
//import org.example.mapper.PaymentRMapper;
//import org.example.repository.PaymentRRepository;
//import org.example.service.PaymentRService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//
//@Service
//public class PaymentRSImpl implements PaymentRService {
//    private PaymentRMapper paymentRMapper;
//    private PaymentRRepository paymentRRepository;
//
//    @Autowired
//    public PaymentRSImpl(PaymentRMapper mapper, PaymentRRepository repository) {
//        paymentRMapper = mapper;
//        paymentRRepository = repository;
//    }
//
//    @Override
//    public List<PaymentRecord> getPaymentRByUser(int userId) {
//        return paymentRMapper.getPaymentRByUser(userId);
//    }
//
//    @Override
//    public List<PaymentRecord> getPaymentRByEventType(String type) {
//        return paymentRRepository.findByPayEvent(type);
//    }
//
//    @Override
//    public void addPaymentRecord(PaymentRecord paymentRecord) {
//        paymentRecord.setPayTime(LocalDateTime.now());
//        paymentRRepository.save(paymentRecord);
//    }
//}
