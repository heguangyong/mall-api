package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.order.RefundOrder;
import com.zbkj.common.response.MerchantRefundOrderPageResponse;
import com.zbkj.common.response.PlatformRefundOrderPageResponse;
import com.zbkj.common.response.RefundOrderInfoResponse;
import com.zbkj.common.response.RefundOrderResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 退款单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface RefundOrderDao extends BaseMapper<RefundOrder> {

    /**
     * 退款订单详情
     * @param refundOrderNo 退款订单号
     * @return RefundOrderInfoResponse
     */
    RefundOrderInfoResponse getRefundOrderDetailByRefundOrderNo(@Param("refundOrderNo") String refundOrderNo);

    List<RefundOrderResponse> findSearchList(Map<String, Object> map);

    List<RefundOrder> findCanAutoRevokeOrderList(@Param("revokeTime") String revokeTime);

    List<PlatformRefundOrderPageResponse> getPlatformAdminPage(Map<String, Object> map);

    List<MerchantRefundOrderPageResponse> getMerchantAdminPage(Map<String, Object> map);

    Integer getMerchantAdminPageCount(Map<String, Object> map);
}
