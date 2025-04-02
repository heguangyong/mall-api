package com.zbkj.common.enums;

/**
 * @author stivepeim
 * @date 2024/8/13 16:51
 * @description GroupBuyActionStatusEnum
 */
public enum GroupBuyActionsProgressEnum {
    // 0=未开始 1=进行中 2=已结束
    GROUP_BUY_ENUM_ACTION_STATUS_BEFORE(0, "未开始"),
    GROUP_BUY_ENUM_ACTION_STATUS_ING(1, "进行中"),
    GROUP_BUY_ENUM_ACTION_STATUS_END(2, "已结束");
    private String name;
    private Integer code;

    GroupBuyActionsProgressEnum(Integer code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
