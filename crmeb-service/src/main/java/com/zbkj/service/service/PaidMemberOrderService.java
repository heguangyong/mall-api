package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.request.PaidMemberOrderSearchRequest;
import com.zbkj.common.response.PaidMemberOrderInfoResponse;
import com.zbkj.common.response.PaidMemberOrderResponse;

import java.util.List;

/**
* @author hzw
* @description PaidMemberOrderService 接口
* @date 2024-05-10
*/
public interface PaidMemberOrderService extends IService<PaidMemberOrder> {

    /**
     * 付费会员订单分页列表
     */
    PageInfo<PaidMemberOrderResponse> findPlatformPage(PaidMemberOrderSearchRequest request);

    /**
     * 付费会员订单详情
     */
    PaidMemberOrderInfoResponse platformInfo(String orderNo);

    /**
     * 用户是否购买过试用类型会员卡
     */
    Boolean userIsPayTrial(Integer userId);

    /**
     * 用户是否第一次购买该套餐
     */
    Boolean userIsFirstPay(Integer userId, Integer cardId);

    /**
     * 获取订单
     *
     * @param outTradeNo 商户系统内部的订单号
     */
    PaidMemberOrder getByOutTradeNo(String outTradeNo);

    /**
     * 支付成功后置处理
     *
     * @param svipOrder 支付订单
     */
    Boolean paySuccessAfter(PaidMemberOrder svipOrder);

    /**
     * 获取用户购买svip订单记录
     *
     * @param userId 用户ID
     */
    List<PaidMemberOrder> findUserSvipOrderRecord(Integer userId);

    PaidMemberOrder getByOrderNo(String orderNo);

    /**
     * 获取待上传微信发货管理订单
     */
    List<PaidMemberOrder> findAwaitUploadWechatList();
}