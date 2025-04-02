package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.MerchantDeliveryPersonnel;
import com.zbkj.common.request.MerchantDeliveryPersonnelSaveRequest;
import com.zbkj.common.request.MerchantDeliveryPersonnelSearchRequest;
import com.zbkj.common.response.MerchantDeliveryPersonnelPageResponse;

/**
 * @author HZW
 * @description MerchantDeliveryPersonnelService 接口
 * @date 2024-11-04
 */
public interface MerchantDeliveryPersonnelService extends IService<MerchantDeliveryPersonnel> {

    /**
     * 商户配送人员分页列表
     */
    PageInfo<MerchantDeliveryPersonnelPageResponse> findPage(MerchantDeliveryPersonnelSearchRequest request);

    /**
     * 商户配送人员分页列表
     */
    PageInfo<MerchantDeliveryPersonnelPageResponse> findPageByEmployee(MerchantDeliveryPersonnelSearchRequest request, Integer merId);

    /**
     * 新增商户配送人员
     */
    Boolean add(MerchantDeliveryPersonnelSaveRequest request);

    /**
     * 删除商户配送人员
     */
    Boolean delete(Integer id);

    /**
     * 编辑商户配送人员
     */
    Boolean edit(MerchantDeliveryPersonnelSaveRequest request);
}