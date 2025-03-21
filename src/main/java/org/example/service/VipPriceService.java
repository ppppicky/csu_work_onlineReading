package org.example.service;

import org.example.entity.VipPrice;
import java.util.List;

public interface VipPriceService {
    List<VipPrice> getAllVipPrices();

    VipPrice getPriceByType(String vipType);

    VipPrice updateVipPrice(VipPrice vipPrice);
}
