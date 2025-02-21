package org.example.service.Impl;

import org.example.dto.ChargeDTO;
import org.example.dto.SetChargeStatusDTO;
import org.example.entity.ChargeManagement;
import org.example.mapper.ChargeMapper;
import org.example.repository.BookRepository;
import org.example.repository.ChargeRepository;
import org.example.service.ChargeService;
import org.example.util.GlobalException;
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
    public Optional<ChargeDTO> getChargeInfoByBookId(int bookId) {
        ChargeManagement chargeManagement= chargeRepository.findByBook_BookId(bookId)
                .orElseThrow(() -> new GlobalException.BookNotFoundException("book not existed"));
        ChargeDTO chargeDTO=new ChargeDTO();
        chargeDTO.setCmId(chargeManagement.getCmId());
        chargeDTO.setChargeMoney(chargeManagement.getChargeMoney());
        chargeDTO.setBookId(bookId);
        chargeDTO.setFreeChapter(chargeManagement.getFreeChapter());
        chargeDTO.setIsVipFree(chargeManagement.getIsVipFree());

        return null;
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

        if (isCharge == 1 || isCharge == 2) {
            // 若设为收费，但 charge_management 表中无记录，则创建默认收费信息
            if (!chargeMapper.getChargeInfoByBookId(bookId).isPresent()) {
                ChargeDTO chargeDTO = new ChargeDTO();
                chargeDTO.setBookId(bookId);
                chargeDTO.setFreeChapter(0);
                chargeDTO.setChargeMoney(new java.math.BigDecimal("0.00"));
                chargeDTO.setIsVipFree((byte) isCharge);
                chargeMapper.insertChargeDetails(chargeDTO);
            }else {
                chargeMapper.updateVipChargeStatus(bookId, isCharge);
            }
        } else {
            // 若设为免费，则删除 charge_management 记录
            chargeMapper.deleteChargeByBookId(bookId);
        }
    }
}
