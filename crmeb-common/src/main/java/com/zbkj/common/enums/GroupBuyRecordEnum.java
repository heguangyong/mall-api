package com.zbkj.common.enums;

import lombok.Getter;

/**
 * 状态：0=进行中，10=已成功，-1=已失败
 *
 * @author stivepeim
 * @date 2024/8/30 15:18
 * @description GroupBuyRecordEnum
 */

public enum GroupBuyRecordEnum {
    GROUP_BUY_RECORD_ENUM_STATUS_INIT("进行中", 0),
    GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS("已成功", 10),
    GROUP_BUY_RECORD_ENUM_STATUS_FAIL("已失败", -1);


    private String name;

    private Integer code;

    GroupBuyRecordEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
