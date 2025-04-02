package com.zbkj.front.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.MerchantDeliveryPersonnelSearchRequest;
import com.zbkj.common.response.MerchantDeliveryPersonnelPageResponse;

/**
 * 移动端商家管理 - 配送人员Service
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/12
 */
public interface EmployeeMerchantDeliveryPersonnelService {

    /**
     * 商户配送人员分页列表
     */
    PageInfo<MerchantDeliveryPersonnelPageResponse> findPage(MerchantDeliveryPersonnelSearchRequest request);

}
