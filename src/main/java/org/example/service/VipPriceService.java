package org.example.service;

import org.example.entity.VipPrice;
import java.util.List;

public interface VipPriceService {
    List<VipPrice> getAllVipPrices();  // 获取所有 VIP 价格

    VipPrice getPriceByType(String vipType); // 按类型获取价格

    VipPrice updateVipPrice(VipPrice vipPrice); // 更新价格
}
