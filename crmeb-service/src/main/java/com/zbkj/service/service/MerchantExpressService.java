package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.request.MerchantExpressSearchRequest;

/**
* @author HZW
* @description MerchantExpressService 接口
* @date 2024-05-08
*/
public interface MerchantExpressService extends IService<MerchantExpress> {

    /**
     * 关联物流公司
     * @param expressId 物流公司ID
     * @return 关联结果
     */
    Boolean relate(Integer expressId);

    /**
     * 更新商户物流公司
     * @param merchantExpress 商户物流公司
     */
    Boolean updateMerchantExpress(MerchantExpress merchantExpress);

    /**
     * 商户物流公司分页列表
     */
    PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request);

    /**
     * 商户物流公司开关
     * @param id 商户物流公司ID
     */
    Boolean openSwitch(Integer id);

    /**
     * 商户物流公司默认开关
     * @param id 商户物流公司ID
     */
    Boolean defaultSwitch(Integer id);

    /**
     * 商户物流公司删除
     * @param id 商户物流公司ID
     */
    Boolean delete(Integer id);

    Boolean deleteByExpressId(Integer expressId);
}