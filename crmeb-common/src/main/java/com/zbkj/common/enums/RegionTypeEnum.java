package com.zbkj.common.enums;

/**
 * 区域枚举
 *
 * @Author 指缝de阳光
 * @Date 2022/7/20 15:04
 * @Version 1.0
 */
public enum RegionTypeEnum {

    COUNTRY("国家", 0),
    PROVINCE("省", 1),
    CITY("市", 2),
    AREA("区", 3),
    STREET("街道", 4);

    private final String name;
    private final Integer value;

    RegionTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
