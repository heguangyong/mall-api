package com.zbkj.common.enums;

/** 移动端 商家管理 权限枚举
 * @Auther: 大粽子
 * @Date: 2024/6/12 15:28
 * @Description: 描述对应的业务场景
 */
public enum MerchantEmployee {
    // 权限: 1订单管理，2商品管理，3售后管理，4代客下单，5订单核销，6统计
    ROLE_ORDER("订单管理", 1),
    ROLE_PRODUCT("商品管理", 2),
    ROLE_REFUND("售后管理", 3),
    ROLE_VISIT("代客下单", 4),
    ROLE_ORDER_CHECK("订单核销", 5),
    ROLE_STATISTICS("统计", 6),
    ;
    private final String roleName;
    private final Integer roleValue;

    MerchantEmployee(String roleName, Integer roleValue) {
        this.roleName = roleName;
        this.roleValue = roleValue;
    }


    public String getRoleName() {
        return roleName;
    }

    public Integer getRoleValue() {
        return roleValue;
    }
}
