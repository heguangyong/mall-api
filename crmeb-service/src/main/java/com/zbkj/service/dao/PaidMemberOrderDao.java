package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.response.PaidMemberOrderResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 付费会员订单表 Mapper 接口
 * </p>
 *
 * @author hzw
 * @since 2024-05-10
 */
public interface PaidMemberOrderDao extends BaseMapper<PaidMemberOrder> {

    List<PaidMemberOrderResponse> findPlatformPage(Map<String, Object> map);
}
