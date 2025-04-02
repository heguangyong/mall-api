package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.request.RechargeOrderSearchRequest;
import com.zbkj.common.request.UserRechargeRequest;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.RechargePackageResponse;

import java.util.List;

/**
 * RechargeOrderService 接口
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
public interface RechargeOrderService extends IService<RechargeOrder> {

    /**
     * 获取充值套餐
     *
     * @return UserRechargeResponse
     */
    RechargePackageResponse getRechargePackage();

    /**
     * 创建用户充值订单
     *
     * @param request 用户下单参数
     */
    OrderPayResultResponse userRechargeOrderCreate(UserRechargeRequest request);

    /**
     * 充值订单分页列表
     * @param request 请求参数
     * @return PageInfo
     */
    PageInfo<RechargeOrder> getAdminPage(RechargeOrderSearchRequest request);


    /**
     * 获取订单
     * @param outTradeNo 商户系统内部的订单号
     */
    RechargeOrder getByOutTradeNo(String outTradeNo);

    /**
     * 支付成功后置处理
     * @param rechargeOrder 支付订单
     */
    Boolean paySuccessAfter(RechargeOrder rechargeOrder);

    /**
     * 获取某一天的充值记录
     * @param date 日期 yyyy-MM-dd
     * @return 充值记录
     */
    List<RechargeOrder> findByDate(String date);

    /**
     * 获取充值订单
     * @param orderNo 充值单号
     * @return 充值订单
     */
    RechargeOrder getByOrderNo(String orderNo);

    /**
     * 获取待上传微信发货管理订单
     */
    List<RechargeOrder> findAwaitUploadWechatList();
}
