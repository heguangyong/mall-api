package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.request.MerchantExpressSearchRequest;

/**
* @author HZW
* @description MerchantExpressService 接口
* @date 2024-05-08
*/
public interface EmployeeMerchantExpressService extends IService<MerchantExpress> {


    /**
     * 商户物流公司分页列表
     */
    PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request);

    /**
     * 查询物流公司面单模板
     *
     * @param com 快递公司编号
     */
    JSONObject template(String com);

}
