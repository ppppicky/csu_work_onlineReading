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

    /**
     * 更新书籍的收费设置信息
     */
    void updateChargeDetails(ChargeDTO chargeDTO);

    /**
     * 设置书籍收费状态（改为使用 DTO）
     */
    void setBookChargeStatus(SetChargeStatusDTO setChargeStatusDTO);
}
