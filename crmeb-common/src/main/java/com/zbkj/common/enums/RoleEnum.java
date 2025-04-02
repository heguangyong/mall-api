package com.zbkj.common.enums;

/**
 * 角色枚举
 *
 * @Author 指缝de阳光
 * @Date 2022/7/20 15:04
 * @Version 1.0
 */
public enum RoleEnum {

    SUPER_ADMIN("超管", 1),
    SUPER_MERCHANT("商户店长", 2),
    PLATFORM_ADMIN("平台管理员", 3),
    MERCHANT_ADMIN("商户管理员", 4),
    ULTRA_VIRES_ADMIN("越权管理", 9);
    
    private final String role;
    private final Integer value;

    RoleEnum(String role, Integer value) {
        this.role = role;
        this.value = value;
    }

    public String getRole() {
        return role;
    }

    public Integer getValue() {
        return value;
    }
}
