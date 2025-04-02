package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.OrderDetailDao;
import com.zbkj.service.service.OrderDetailService;
import com.zbkj.service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
*  OrderDetailServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail> implements OrderDetailService {

    @Resource
    private OrderDetailDao dao;
    @Autowired
    private OrderService orderService;

    /**
     * 根据主订单号获取
     * @param orderNo 订单编号
     * @return List
     */
    @Override
    public List<OrderDetail> getByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderDetail::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 订单商品评论列表
     * @param userId 用户id
     * @param isReply 是否评价
     * @param pageRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderDetail> getReplyList(Integer userId, Boolean isReply, PageParamRequest pageRequest) {
        Page<OrderDetail> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<OrderDetail> orderDetailList = dao.findReplyList(userId, isReply ? 1 : 0);
        return CommonPage.copyPageInfo(page, orderDetailList);
    }

    /**
     * 订单收货
     * @param orderNo 订单号
     */
    @Override
    public Boolean takeDelivery(String orderNo) {
        LambdaUpdateWrapper<OrderDetail> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(OrderDetail::getIsReceipt, true);
        wrapper.eq(OrderDetail::getOrderNo, orderNo);
        return update(wrapper);
    }

    /**
     * 售后申请列表(可申请售后列表)
     * @param request 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderDetail> findAfterSaleApplyList(Integer uid, CommonSearchRequest request) {
        Page<OrderDetail> page = PageHelper.startPage(request.getPage(), request.getLimit());
        String keywords = StrUtil.isNotBlank(request.getKeywords()) ? URLUtil.decode(request.getKeywords()) : "";
        List<OrderDetail> orderDetailList = dao.findAfterSaleApplyList(uid, keywords);
        return CommonPage.copyPageInfo(page, orderDetailList);
    }

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return Integer
     */
    @Override
    public Integer getSalesNumByDateAndProductId(String date, Integer proId) {
        return dao.getSalesNumByDateAndProductId(date, proId);
    }

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSalesByDateAndProductId(String date, Integer proId) {
        return dao.getSalesByDateAndProductId(date, proId);
    }

    /**
     * 订单发货获取订单详情列表
     * @param orderNo 订单号
     * @return 订单详情列表
     */
    @Override
    public List<OrderDetail> getShipmentByOrderNo(String orderNo) {
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.select(OrderDetail::getId, OrderDetail::getProductName, OrderDetail::getImage, OrderDetail::getSku,
                OrderDetail::getPayNum, OrderDetail::getDeliveryNum, OrderDetail::getRefundNum, OrderDetail::getPrice,
                OrderDetail::getPayPrice, OrderDetail::getRefundPrice, OrderDetail::getApplyRefundNum);
        lqw.eq(OrderDetail::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 获取待评价数量
     * @return 待评价数量
     */
    @Override
    public Integer getAwaitReplyCount(Integer userId) {
        return dao.getAwaitReplyCount(userId);
    }

    /**
     * 是否已购买（已收货后才算已购）
     */
    @Override
    public Boolean isPurchased(Integer proId, Integer userId) {
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.select(OrderDetail::getId);
        lqw.eq(OrderDetail::getProductId, proId);
        lqw.eq(OrderDetail::getUid, userId);
        lqw.gt(OrderDetail::getMerId, 0);
        lqw.eq(OrderDetail::getIsReceipt, 1);
        lqw.last("limit 1");
        OrderDetail orderDetail = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(orderDetail) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 获取已购商品列表
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<OrderDetail> findPurchasedList(Integer userId, PageParamRequest pageParamRequest) {
        Page<OrderDetail> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.select(OrderDetail::getId, OrderDetail::getProductId);
        lqw.eq(OrderDetail::getUid, userId);
        lqw.gt(OrderDetail::getMerId, 0);
        lqw.eq(OrderDetail::getIsReceipt, 1);
        lqw.eq(OrderDetail::getProductMarketingType, 0);
        lqw.in(OrderDetail::getProductType, 0,2,5,6);
        lqw.groupBy(OrderDetail::getProductId);
        lqw.orderByDesc(OrderDetail::getId);
        List<OrderDetail> detailList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, detailList);
    }

    /**
     * 通过订单号获取订单商品详情Map
     * @param orderNoList 订单号列表
     */
    @Override
    public Map<String, List<OrderDetail>> getMapByOrderNoList(List<String> orderNoList) {
        if (CollUtil.isEmpty(orderNoList)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.in(OrderDetail::getOrderNo, orderNoList);
        List<OrderDetail> detailList = dao.selectList(lqw);
        if (CollUtil.isEmpty(detailList)) {
            return new HashMap<>();
        }
        return detailList.stream().collect(Collectors.groupingBy(OrderDetail::getOrderNo));
    }

    /**
     * @param groupBuyActivityId
     * @return
     */
    @Override
    public Integer getByGroupBuyActivityId(Integer groupBuyActivityId, Integer productId, Integer uid) {
        Integer waitPayNum = 0;
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.eq(OrderDetail::getGroupBuyActivityId, groupBuyActivityId);
        lqw.eq(OrderDetail::getProductId, productId);
        lqw.eq(OrderDetail::getUid, uid);
//        lqw.eq(OrderDetail::getAttrValueId, skuId);
//        lqw.last("limit 1");
        List<OrderDetail> orderDetailList = dao.selectList(lqw);
        if(!orderDetailList.isEmpty()){
            for (OrderDetail orderDetail : orderDetailList) {
                Order o = orderService.getByOrderNo(orderDetail.getOrderNo());
                // 待支付的订单 !o.getPaid() ||
                if(o.getStatus().equals(OrderConstants.ORDER_STATUS_WAIT_PAY)){
                    waitPayNum += orderDetail.getPayNum();
                }
            }
            return waitPayNum;
        }



        return waitPayNum;
    }

    @Override
    public Integer getOrderRefundNum(String orderNo) {
        LambdaQueryWrapper<OrderDetail> lqw = Wrappers.lambdaQuery();
        lqw.select(OrderDetail::getRefundNum);
        lqw.eq(OrderDetail::getOrderNo, orderNo);
        List<OrderDetail> orderDetailList = dao.selectList(lqw);
        if (CollUtil.isEmpty(orderDetailList)) {
            return 0;
        }
        return orderDetailList.stream().mapToInt(OrderDetail::getRefundNum).sum();
    }
}

