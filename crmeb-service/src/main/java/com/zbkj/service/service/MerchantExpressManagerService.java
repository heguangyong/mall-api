package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
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
public interface MerchantExpressManagerService extends IService<MerchantExpress> {

    /**
     * 关联物流公司
     * @param expressId 物流公司ID
     * @return 关联结果
     */
    Boolean relate(Integer expressId, SystemAdmin admin);

    /**
     * 商户物流公司分页列表
     */
    PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request, SystemAdmin admin);

    /**
     * 查询物流公司面单模板
     *
     * @param com 快递公司编号
     */
    JSONObject template(String com,SystemAdmin admin);

    /**
     * 商户物流公司开关
     * @param id 商户物流公司ID
     */
    Boolean openSwitch(Integer id, SystemAdmin admin);

    /**
     * 商户物流公司默认开关
     * @param id 商户物流公司ID
     */
    Boolean defaultSwitch(Integer id, SystemAdmin admin);

    /**
     * 商户物流公司删除
     * @param id 商户物流公司ID
     */
    Boolean delete(Integer id, SystemAdmin admin);
}
