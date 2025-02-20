package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.entity.VipPrice;
import org.example.service.VipPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vipPrice")
@Api(tags = "会员价格管理")
public class VipPriceController {

    @Autowired
    private VipPriceService vipPriceService;

    /**
     * 获取所有会员价格
     */
    @GetMapping("/all")
    @ApiOperation(value = "获取所有会员价格")
    public ResponseEntity<List<VipPrice>> getAllVipPrices() {
        return ResponseEntity.ok(vipPriceService.getAllVipPrices());
    }

    /**
     * 获取指定会员类型的价格
     */
    @GetMapping("/{vipType}")
    @ApiOperation(value = "获取指定会员类型的价格")
    public ResponseEntity<VipPrice> getPriceByType(@PathVariable String vipType) {
        return ResponseEntity.ok(vipPriceService.getPriceByType(vipType));
    }

    /**
     * 更新会员价格（仅管理员可操作）
     */
    @PutMapping("/update")
    @ApiOperation(value = "更新会员价格")
    public ResponseEntity<String> updateVipPrice(@RequestBody VipPrice vipPrice) {
        vipPriceService.updateVipPrice(vipPrice);
        return ResponseEntity.ok("VIP 价格更新成功");
    }
}
