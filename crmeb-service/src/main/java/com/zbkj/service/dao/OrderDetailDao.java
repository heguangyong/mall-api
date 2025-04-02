package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.order.OrderDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 订单详情表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface OrderDetailDao extends BaseMapper<OrderDetail> {

    /**
     * 订单商品评论列表
     * @param userId 用户id
     * @param isReply 是否评价，0-未评价，1-已评价
     */
    List<OrderDetail> findReplyList(@Param("userId") Integer userId, @Param("isReply") Integer isReply);

    /**
     * 售后申请列表(可申请售后列表)
     * @param uid 用户id
     * @param keywords 订单号/商品名称
     */
    List<OrderDetail> findAfterSaleApplyList(@Param("uid") Integer uid, @Param("keywords") String keywords);

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     */
    Integer getSalesNumByDateAndProductId(@Param("date") String date, @Param("proId")  Integer proId);

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     */
    BigDecimal getSalesByDateAndProductId(@Param("date") String date, @Param("proId")  Integer proId);

    /**
     * 获取待评价数量
     * @return 待评价数量
     */
    Integer getAwaitReplyCount(@Param("userId") Integer userId);
}
