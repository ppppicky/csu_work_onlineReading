package org.example.service.Impl;

import org.example.entity.VipPrice;
import org.example.repository.VipPriceRepository;
import org.example.service.VipPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VipPriceServiceImpl implements VipPriceService {
    @Autowired
    private VipPriceRepository vipPriceRepository;

    @Override
    public List<VipPrice> getAllVipPrices() {
        return vipPriceRepository.findAll();
    }

    @Override
    public VipPrice getPriceByType(String vipType) {
        return vipPriceRepository.findByVipType(vipType)
                .orElseThrow(() -> new RuntimeException("VIP 类型不存在: " + vipType));
    }

    @Override
    public VipPrice updateVipPrice(VipPrice vipPrice) {
        Optional<VipPrice> optionalPrice = vipPriceRepository.findByVipType(vipPrice.getVipType());
        if (optionalPrice.isPresent()) {
            VipPrice existingVipPrice = optionalPrice.get();
            existingVipPrice.setPrice(vipPrice.getPrice());
            existingVipPrice.setUpdateTime(LocalDateTime.now()); // 更新修改时间
            return vipPriceRepository.save(existingVipPrice);
        } else {
            throw new RuntimeException("VIP 类型不存在: " + vipPrice.getVipType());
        }
    }
}
