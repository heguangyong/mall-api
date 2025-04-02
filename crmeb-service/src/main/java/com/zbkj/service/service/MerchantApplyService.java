package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.MerchantApply;
import com.zbkj.common.request.*;
import com.zbkj.common.request.merchant.MerchantApplyAuditRequest;
import com.zbkj.common.request.merchant.MerchantApplyRemarkRequest;
import com.zbkj.common.request.merchant.MerchantApplySearchRequest;
import com.zbkj.common.request.merchant.MerchantSettledApplyRequest;

/**
 * MerchantApplyService 接口
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
public interface MerchantApplyService extends IService<MerchantApply> {

    /**
     * 分页列表
     * @param searchRequest 查询参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<MerchantApply> getAdminPage(MerchantApplySearchRequest searchRequest, PageParamRequest pageParamRequest);

    /**
     * 入驻审核
     * @param request 审核参数
     * @return Boolean
     */
    Boolean audit(MerchantApplyAuditRequest request);

    /**
     * 备注
     * @param request 备注参数
     * @return Boolean
     */
    Boolean remark(MerchantApplyRemarkRequest request);

    /**
     * 商户入驻申请
     * @param request 申请参数
     * @return Boolean
     */
    Boolean settledApply(MerchantSettledApplyRequest request);

    /**
     * 商户入驻申请记录
     * @param uid 申请用户id
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<MerchantApply> findSettledRecord(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 商户名称是否存在
     * @param name 商户名
     * @return Boolean
     */
    Boolean checkMerchantName(String name);

    /**
     * 商户手机号是否存在
     * @param phone 手机号
     * @return Boolean
     */
    Boolean checkMerchantPhone(String phone);
}
