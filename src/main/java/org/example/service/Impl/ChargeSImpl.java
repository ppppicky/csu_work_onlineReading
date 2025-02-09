package org.example.service.Impl;

import org.example.dto.ChargeDTO;
import org.example.dto.SetChargeStatusDTO;
import org.example.entity.Book;
import org.example.entity.ChargeManagement;
import org.example.mapper.ChargeMapper;
import org.example.repository.BookRepository;
import org.example.repository.ChargeRepository;
import org.example.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChargeSImpl implements ChargeService {

    private final ChargeRepository chargeRepository;
    private final BookRepository bookRepository;
    private final ChargeMapper chargeMapper;

    @Autowired
    public ChargeSImpl(ChargeRepository chargeRepository, BookRepository bookRepository, ChargeMapper chargeMapper) {
        this.chargeRepository = chargeRepository;
        this.bookRepository = bookRepository;
        this.chargeMapper = chargeMapper;
    }

    @Override
    public Optional<ChargeManagement> getChargeInfoByBookId(int bookId) {
        return chargeRepository.findByBook_BookId(bookId);
    }

    @Override
    public void updateChargeDetails(ChargeDTO chargeDTO) {
        if (chargeRepository.findByBook_BookId(chargeDTO.getBookId()).isPresent()) {
            chargeMapper.updateChargeDetails(chargeDTO);
        } else {
            chargeMapper.insertChargeDetails(chargeDTO);
        }
    }

    @Override
    public void setBookChargeStatus(SetChargeStatusDTO setChargeStatusDTO) {
        int bookId = setChargeStatusDTO.getBookId();
        int isCharge = setChargeStatusDTO.getIsCharge();

        chargeMapper.updateBookChargeStatus(bookId, isCharge);

        if (isCharge == 1) {
            // 若设为收费，但 charge_management 表中无记录，则创建默认收费信息
            //isPresent取反来实现类似 isEmpty() 的功能
            if (!chargeMapper.getChargeInfoByBookId(bookId).isPresent()) {
                ChargeDTO chargeDTO = new ChargeDTO();
                chargeDTO.setBookId(bookId);
                chargeDTO.setFreeChapter(0);
                chargeDTO.setChargeMoney(new java.math.BigDecimal("0.00"));
                chargeDTO.setIsVipFree((byte) 0);
                chargeMapper.insertChargeDetails(chargeDTO);
            }
        } else {
            // 若设为免费，则删除 charge_management 记录
            chargeMapper.deleteChargeByBookId(bookId);
        }
    }
}
