package org.example.service;

import org.example.dto.ChargeDTO;
import org.example.dto.SetChargeStatusDTO;

import java.util.Optional;

public interface ChargeService {

    /**
     * 根据书籍 ID 获取收费信息
     *
     * @param bookId 书籍 ID
     * @return ChargeManagement
     */
    Optional<ChargeDTO> getChargeInfoByBookId(int bookId);

    void updateChargeDetails(ChargeDTO chargeDTO);

    void setBookChargeStatus(SetChargeStatusDTO setChargeStatusDTO);
}
