package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.OrderSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.OrderExcelVo;
import com.zbkj.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExcelServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private MerchantOrderService merchantOrderService;

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportOrder(OrderSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (systemAdmin.getMerId() > 0) {
            request.setMerId(systemAdmin.getMerId());
        }
        List<Order> orderList = orderService.findExportList(request);
        if (CollUtil.isEmpty(orderList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "没有可导出的数据！");
        }

        List<Integer> merIdList = orderList.stream().filter(e -> e.getMerId() > 0).map(Order::getMerId).distinct().collect(Collectors.toList());
        List<Integer> userIdList = orderList.stream().map(Order::getUid).distinct().collect(Collectors.toList());
        List<String> orderNoList = orderList.stream().map(Order::getOrderNo).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        Map<Integer, User> userMap = userService.getUidMapList(userIdList);
        Map<String, List<OrderDetail>> orderDetailMap = orderDetailService.getMapByOrderNoList(orderNoList);
        Map<String, List<MerchantOrder>> merchantOrderMap = merchantOrderService.getMapByOrderNoList(orderNoList);
        List<OrderExcelVo> voList = CollUtil.newArrayList();
        for (Order order : orderList) {
            OrderExcelVo vo = new OrderExcelVo();
            vo.setType(getOrderType(order.getType()));
            vo.setOrderNo(order.getOrderNo());
            vo.setMerName(order.getMerId() > 0 ? merchantMap.get(order.getMerId()).getName() : "");
            vo.setUserNickname(userMap.get(order.getUid()).getNickname() + "|" + order.getUid());
            vo.setPayPrice(order.getPayPrice().toString());
            vo.setPaidStr(order.getPaid() ? "已支付" : "未支付");
            vo.setPayType(getOrderPayType(order.getPayType()));
            vo.setPayChannel(getOrderPayChannel(order.getPayChannel()));
            vo.setStatus(getOrderStatus(order.getStatus()));
            vo.setRefundStatus(getOrderRefundStatus(order.getRefundStatus()));
            vo.setCreateTime(CrmebDateUtil.dateToStr(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.setProductInfo(getOrderProductInfo(orderDetailMap.get(order.getOrderNo())));
            vo.setRealName(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getRealName()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getRealName());
            vo.setUserPhone(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getUserPhone()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getUserPhone());
            vo.setUserAddress(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getUserAddress()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getUserAddress());
            vo.setUserRemark(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getUserRemark()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getUserRemark());
            vo.setMerchantRemark(StrUtil.isBlank(merchantOrderMap.get(order.getOrderNo()).get(0).getMerchantRemark()) ? "" : merchantOrderMap.get(order.getOrderNo()).get(0).getMerchantRemark());
            voList.add(vo);
        }

        /*
          ===============================
          以下为存储部分
          ===============================
         */
        // 上传设置
        UploadUtil.setHzwServerPath((crmebConfig.getImagePath() + "/").replace(" ", "").replace("//", "/"));

        // 文件名
        String fileName = "订单导出_".concat(CrmebDateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("type", "订单类型");
        aliasMap.put("orderNo", "订单号");
        aliasMap.put("merName", "商户名称");
        aliasMap.put("userNickname", "用户昵称");
        aliasMap.put("payPrice", "实际支付金额");
        aliasMap.put("paidStr", "支付状态");
        aliasMap.put("payType", "支付方式");
        aliasMap.put("payChannel", "支付渠道");
        aliasMap.put("status", "订单状态");
        aliasMap.put("refundStatus", "退款状态");
        aliasMap.put("createTime", "创建时间");
        aliasMap.put("productInfo", "商品信息");
        aliasMap.put("realName", "收货人");
        aliasMap.put("userPhone", "收货电话");
        aliasMap.put("userAddress", "收货地址");
        aliasMap.put("userRemark", "用户备注");
        aliasMap.put("merchantRemark", "商户备注");

        return ExportUtil.exportExcel(fileName, "订单导出", voList, aliasMap);
    }

    private String getOrderProductInfo(List<OrderDetail> orderDetails) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < orderDetails.size(); i++) {
            OrderDetail orderDetail = orderDetails.get(i);
            stringBuilder.append(StrUtil.format("{}  {} * {}", orderDetail.getProductName(), orderDetail.getPayPrice(), orderDetail.getPayNum()));
            if ((i + 1) < orderDetails.size()) {
                stringBuilder.append("\r\n");
            }
        }
        return stringBuilder.toString();
    }

    private String getOrderType(Integer type) {
        String typeStr = "";
        switch (type) {
            case 0:
                typeStr = "普通";
                break;
            case 1:
                typeStr = "秒杀";
                break;
            case 2:
                typeStr = "拼团";
                break;
                // 视频号订单待定
        }
        return typeStr;
    }

    private String getOrderRefundStatus(Integer refundStatus) {
        String refundStatusStr = "";
        switch (refundStatus) {
            case 0:
                refundStatusStr = "未退款";
                break;
            case 1:
                refundStatusStr = "申请中";
                break;
            case 2:
                refundStatusStr = "部分退款";
                break;
            case 3:
                refundStatusStr = "已退款";
                break;
        }
        return refundStatusStr;
    }

    private String getOrderStatus(Integer status) {
        String statusStr = "";
        switch (status) {
            case 0:
                statusStr = "待支付";
                break;
            case 1:
                statusStr = "待发货";
                break;
            case 2:
                statusStr = "部分发货";
                break;
            case 3:
                statusStr = "待核销";
                break;
            case 4:
                statusStr = "待收货";
                break;
            case 5:
                statusStr = "已收货";
                break;
            case 6:
                statusStr = "已完成";
                break;
            case 9:
                statusStr = "已取消";
                break;
        }
        return statusStr;
    }

    private String getOrderPayChannel(String payChannel) {
        String payChannelStr = "";
        switch (payChannel) {
            case "public":
                payChannelStr = "公众号";
                break;
            case "mini":
                payChannelStr = "小程序";
                break;
            case "h5":
                payChannelStr = "微信网页支付";
                break;
            case "yue":
                payChannelStr = "余额";
                break;
            case "wechatIos":
                payChannelStr = "微信Ios";
                break;
            case "wechatAndroid":
                payChannelStr = "微信Android";
                break;
            case "alipay":
                payChannelStr = "支付宝";
                break;
            case "alipayApp":
                payChannelStr = "支付宝App";
                break;
        }
        return payChannelStr;
    }

    private String getOrderPayType(String payType) {
        String payTypeStr = "";
        switch (payType) {
            case "weixin":
                payTypeStr = "微信支付";
                break;
            case "alipay":
                payTypeStr = "支付宝支付";
                break;
            case "yue":
                payTypeStr = "余额支付";
                break;
        }
        return payTypeStr;
    }
}

