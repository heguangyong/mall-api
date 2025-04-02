package com.zbkj.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Crmeb 基础配置
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Configuration
@ConfigurationProperties(prefix = "crmeb")
public class CrmebConfig{
    // 当前代码版本
    private String version;
    // 待部署域名
    private String domain;
    // #请求微信接口中专服务器
    private String wechatApiUrl;
    // #是否同步config表数据到redis
    private Boolean asyncConfig;
    // 本地图片路径配置
    private String imagePath;
    // 佣金返佣比例和上限
    private Integer retailStoreBrokerageRatio;
    // 活动边框缓存周期
    private Integer activityStyleCachedTime;
    // 活动边框参加 指定商品参加上限
    private Integer selectProductLimit;

    // 商品标签缓存分钟数
    private Long productTagCacheMinutes;
    // 不过滤任何数据的url配置
    private List<String> ignored;

    // 订单支付取消时间，单位分钟
    private Integer orderCancelTime;

    private Boolean phoneMaskSwitch;

    public Integer getRetailStoreBrokerageRatio() {
        return retailStoreBrokerageRatio;
    }

    public void setRetailStoreBrokerageRatio(Integer retailStoreBrokerageRatio) {
        this.retailStoreBrokerageRatio = retailStoreBrokerageRatio;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWechatApiUrl() {
        return wechatApiUrl;
    }

    public void setWechatApiUrl(String wechatApiUrl) {
        this.wechatApiUrl = wechatApiUrl;
    }

    public Boolean getAsyncConfig() {
        return asyncConfig;
    }

    public void setAsyncConfig(Boolean asyncConfig) {
        this.asyncConfig = asyncConfig;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getSelectProductLimit() {
        return selectProductLimit;
    }

    public void setSelectProductLimit(Integer selectProductLimit) {
        this.selectProductLimit = selectProductLimit;
    }

    public Integer getActivityStyleCachedTime() {
        return activityStyleCachedTime;
    }

    public void setActivityStyleCachedTime(Integer activityStyleCachedTime) {
        this.activityStyleCachedTime = activityStyleCachedTime;
    }

    public List<String> getIgnored() {
        return ignored;
    }

    public void setIgnored(List<String> ignored) {
        this.ignored = ignored;
    }

    public Long getProductTagCacheMinutes() {
        return productTagCacheMinutes;
    }

    public void setProductTagCacheMinutes(Long productTagCacheMinutes) {
        this.productTagCacheMinutes = productTagCacheMinutes;
    }

    public Integer getOrderCancelTime() {
        return orderCancelTime;
    }

    public void setOrderCancelTime(Integer orderCancelTime) {
        this.orderCancelTime = orderCancelTime;
    }

    public Boolean getPhoneMaskSwitch() {
        return phoneMaskSwitch;
    }

    public void setPhoneMaskSwitch(Boolean phoneMaskSwitch) {
        this.phoneMaskSwitch = phoneMaskSwitch;
    }

    @Override
    public String toString() {
        return "CrmebConfig{" +
                "version='" + version + '\'' +
                ", domain='" + domain + '\'' +
                ", wechatApiUrl='" + wechatApiUrl + '\'' +
                ", asyncConfig=" + asyncConfig +
                ", imagePath='" + imagePath + '\'' +
                ", retailStoreBrokerageRatio=" + retailStoreBrokerageRatio +
                ", activityStyleCachedTime=" + activityStyleCachedTime +
                ", selectProductLimit=" + selectProductLimit +
                ", productTagCacheMinutes=" + productTagCacheMinutes +
                ", ignored=" + ignored +
                ", orderCancelTime=" + orderCancelTime +
                '}';
    }
}
