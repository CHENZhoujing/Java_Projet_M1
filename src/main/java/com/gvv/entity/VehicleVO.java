package com.gvv.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@TableName("v_vehicle")
public class VehicleVO {
    private int vehicleId;
    private String brandName;
    private String vehicleTypeName;
    private BigDecimal prise;
    private Timestamp storageTime;
    private boolean saleStatus;
}
