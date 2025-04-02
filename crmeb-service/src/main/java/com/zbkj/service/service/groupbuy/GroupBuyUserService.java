package com.zbkj.service.service.groupbuy;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyUserSearchRequest;
import com.zbkj.common.vo.PreOrderInfoDetailVo;

import java.util.List;

/**
* @author dazongzi
* @description GroupBuyUserService 接口
* @date 2024-08-13
*/
public interface GroupBuyUserService extends IService<GroupBuyUser> {

    List<GroupBuyUser> getList(GroupBuyUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 拼团成功查询最新的参与用户
     * @param groupBuyActivityId 拼团id
     * @param limit 限制数量
     * @return  获取到的用户列表
     */
    List<GroupBuyUser> getLastGroupBuyUserList(Integer groupBuyActivityId, Integer limit);

    /**
     * 拼团成功查询最新的参与用户
     * @param groupBuyActivityId 拼团id
     * @param limit 限制数量
     * @return  获取到的用户列表
     */
    List<GroupBuyUser> getLastGroupBuyUserList(Integer groupBuyActivityId, Integer merId, Integer limit);

    /**
     * 获取当前拼团活动下的已成功的购买数量
     * @param activityId 拼团活动id
     * @return 购买数量累加
     */
    Integer getGroupBuySalesForAfterPay(Integer activityId);

    /**
     * 获取成功的拼团人数
     * @return 成熟成功的拼团人数
     */
    Integer getGroupBuyUserDoneTotalCount();

    /**
     * 获取成功的拼团人数
     * @return 成熟成功的拼团人数
     */
    Integer getGroupBuyUserDoneTotalCount(Integer merId);

    /**
     * 根据拼团活动id获取已成团订单数
     *
     * @param productId 拼团商品id
     * @return 拼团成团订单数
     */
    Integer getOrderDoneCountByProductIdAndActivityId(Integer productId, Integer activityId);
    /**
     * 获取 当前拼团活动的成功的拼团人
     * @param activityId 当前拼团活动
     * @return 成功的拼团人数
     */
    List<GroupBuyUser> getGroupBuyUserDoneForActivityId(Integer activityId);

    /**
     * 获取当前 拼团订单下已经参与的人
     * @param recordId 拼团id
     * @return 拼团用户列表
     */
    List<GroupBuyUser> getGroupBuyUserAfterPayForRecordId(Integer recordId, Integer limit);


    /** 获取当前 拼团订单成单的人员
     * @param recordId 拼团id
     * @return 拼团用户列表
     */
    List<GroupBuyUser> getGroupBuyUserAfterOderDoneForRecordId(Integer recordId, Integer limit);


    /**
     * 根据拼团记录ID获取拼团用户列表
     * @param recordId 拼团记录ID
     * @return 当前拼团用户列表
     */
    List<GroupBuyUser> getListByGroupRecordId(Integer recordId);

    /**
     * 获取当前用户在当前拼团活动下的拼团记录
     * @param userId 用户id
     * @param groupBuyActivityId 拼团活动id
     * @param groupRecordId 拼团记录id
     * @param orderStatus 订单状态 （0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
     * @return 拼团记录列表
     */
    List<GroupBuyUser> getCurrentGroupBuyActivityRecordListByUserId(Integer userId, Integer groupBuyActivityId, Integer groupRecordId, Integer orderStatus);

    // 当前活动的拼团id正在拼的数量
    List<GroupBuyUser> getCurrentGroupBuyActivityRecordList(Integer groupBuyActivityId, Integer userId,  Integer groupRecordId);

    /**
     * 根据订单编号 获取用户购买记录
     */
    GroupBuyUser getByOrderNo(String orderNo);

    /**
     * 拼团支付成功后
     * @param orderNo 订单编号
     * @param uid 用户id
     * @param avatar 用户头像
     * @param nickName 用户昵称
     */
    void beforePay(String orderNo, Integer payNum, Integer uid, String avatar, String nickName);


    /**
     * 拼团商品 支付后
     * @param ptOrderNo 订单号
     */
    void afterPay(String ptOrderNo);
}
