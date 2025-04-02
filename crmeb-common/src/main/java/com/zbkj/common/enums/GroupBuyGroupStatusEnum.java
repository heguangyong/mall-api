package com.zbkj.common.enums;

import lombok.Getter;

/**
 * @author stivepeim
 * @date 2024/8/13 15:16
 * @description GroupByConstant
 */
@Getter
public enum GroupBuyGroupStatusEnum {
    //控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过'

    GROUP_BUY_ENUM_ACTIVITY_STATUS_INIT(0, "初始化"),
    GROUP_BUY_ENUM_ACTIVITY_STATUS_REFUSE(1, "已拒绝"),
    GROUP_BUY_ENUM_ACTIVITY_STATUS_CANCEL(2, "已撤销"),
    GROUP_BUY_ENUM_ACTIVITY_STATUS_AUDIT(3, "待审核"),
    GROUP_BUY_ENUM_ACTIVITY_STATUS_PASS(4, "已通过");
    private String name;
    private Integer code;

    GroupBuyGroupStatusEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
