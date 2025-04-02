package com.zbkj.service.service.groupbuy;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.CreateOrderRequest;
import com.zbkj.common.request.GroupBuyRecordPageNumRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.PreOrderDetailRequest;
import com.zbkj.common.request.groupbuy.GroupBuyRecordSearchRequest;
import com.zbkj.common.response.OrderNoResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityRecordAdminListResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityRecordListHeaderCount;
import com.zbkj.common.response.groupbuy.GroupBuyRecordDetailResponse;
import com.zbkj.common.vo.PreMerchantOrderVo;
import com.zbkj.common.vo.PreOrderInfoVo;

import java.util.List;

/**
* @author dazongzi
* @description GroupBuyRecordService 接口
* @date 2024-08-13
*/
public interface GroupBuyRecordService extends IService<GroupBuyRecord> {

    /**
     *  拼团团购记录列表
     * @param request 查询参数
     * @param pageParamRequest  分页参数
     * @return List<GroupBuyRecord>
     */
    PageInfo<GroupBuyActivityRecordAdminListResponse> getList(GroupBuyRecordSearchRequest request, PageParamRequest pageParamRequest);


    /**
     *  获取团购记录的表头数量
     * 状态：0=进行中，10=已成功，-1=已失败
     * @param request request
     * @param systemAdmin 当前登录管理员
     * @return GroupBuyActivityRecordListHeaderCount
     */
    GroupBuyActivityRecordListHeaderCount getListHeaderCount(GroupBuyRecordPageNumRequest request, SystemAdmin systemAdmin);

    /**
     * 拼团开团记录详情
     *
     * @param id 开团记录ID
     */
    GroupBuyRecordDetailResponse getByAdminDetail(Integer id);

    /**
     * 根据拼团活动id获取已成团订单数
     * @param groupBuyActivityId 拼团活动id
     * @return 拼团成团订单数
     */
    Integer getOrderDoneCount(Integer groupBuyActivityId);

    /**
     * 拼团下单之前的校验
     * @param orderRequest 订单参数
     * @param orderInfoVo 订单信息
     * @param user 用户信息
     */
    OrderNoResponse validationBeforeCreateOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user);

    /**
     * 扣减库存
     * @param orderNo 订单号
     * @param orderRequest 订单参数
     * @param orderInfoVo   订单信息
     * @param user 用户信息
     */
    void subStock(String orderNo, CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user);


    /**
     * 获取拼团活动成功用户列表
     * @param groupActivityId 拼团活动id
     * @param limit 限制数量
     * @return
     */
    List<GroupBuyRecord> getGroupBuyUserActivityDoneList(Integer groupActivityId, Integer userId, Integer productId, int limit);

    /**
     * 开团或者拼团记录
     * @param orderNo 订单号
     * @param groupActivityId 拼团活动id
     * @param groupProductId 拼团商品
     * @param groupBuyRecordId 拼团记录id
     * @param merId 商户id
     */
    GroupBuyRecord newGroupBuyRecordOrContinueBuy(String orderNo, Integer payNum, Integer groupActivityId,
                                                  Integer groupProductId, Integer groupBuyRecordId, Integer fictiStatus, Integer merId,
                                                  Integer skuid, Integer uid, String nickname, String avatar);

    /**
     * 拼团订单创建
     * @param orderRequest 订单参数
     * @param orderInfoVo 订单信息
     * @param user 用户信息
     * @param preOrderKey 预下单缓存key
     * @return 订单号
     */
    OrderNoResponse createOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user, String preOrderKey);


    /**
     * 拼团商品预下单
     * @param detailRequest 拼团商品预下单参数
     * @return 预下单对象
     */
    PreMerchantOrderVo validatePreOrderGroupBuy(PreOrderDetailRequest detailRequest);

    void groupSuccess(GroupBuyRecord currentGroupBuyRecord, List<GroupBuyUser> groupBuyUserList);

    Boolean rollBackIsBuyed();

    /**
     * 拼团订单 订单退款
     */
    void groupBuyOrderRefund();

    void groupBuyOrderRefundEt();

    List<GroupBuyRecord> getGroupRecordListForNotExist(Integer groupActivityId, Integer userId,
                                                       Integer productId,
                                                       int limit);
}
