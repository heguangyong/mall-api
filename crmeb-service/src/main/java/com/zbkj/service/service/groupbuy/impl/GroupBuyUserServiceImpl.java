package com.zbkj.service.service.groupbuy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.enums.GroupBuyRecordEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyRecordForCache;
import com.zbkj.common.request.groupbuy.GroupBuyUserSearchRequest;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.groupby.GroupBuyUserDao;
import com.zbkj.service.service.OrderService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dazongzi
 * @description GroupBuyUserServiceImpl 接口实现
 * @date 2024-08-13
 */
@Service
public class GroupBuyUserServiceImpl extends ServiceImpl<GroupBuyUserDao, GroupBuyUser> implements GroupBuyUserService {

    private final Logger logger = LoggerFactory.getLogger(GroupBuyUserServiceImpl.class);
    @Resource
    private GroupBuyUserDao dao;
    @Resource
    private GroupBuyRecordService groupBuyRecordService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GroupBuyUserService groupBuyUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<GroupBuyUser>
     * @author dazongzi
     * @since 2024-08-13
     */
    @Override
    public List<GroupBuyUser> getList(GroupBuyUserSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 GroupBuyUser 类的多条件查询
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        GroupBuyUser model = new GroupBuyUser();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 拼团查询最新的参与用户
     *
     * @param recordId 拼团id
     * @param limit    限制数量
     * @return 获取到的用户列表
     */
    @Override
    public List<GroupBuyUser> getLastGroupBuyUserList(Integer recordId, Integer limit) {
        LambdaQueryWrapper<GroupBuyUser> query = Wrappers.lambdaQuery();
        query.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
        query.eq(GroupBuyUser::getIsLeader, 1);
        if (ObjectUtil.isNotNull(recordId)) {
            query.eq(GroupBuyUser::getGroupRecordId, recordId);
        }
        if (ObjectUtil.isNull(limit)) {
            throw new CrmebException("查询参与拼团活动 限制不能为空");
        }
        query.orderByDesc(GroupBuyUser::getId);
        query.last("limit " + limit);
        return dao.selectList(query);
    }

    /**
     * 拼团查询最新的参与用户
     *
     * @param recordId 拼团id
     * @param limit    限制数量
     * @return 获取到的用户列表
     */
    @Override
    public List<GroupBuyUser> getLastGroupBuyUserList(Integer recordId, Integer merId, Integer limit) {
        LambdaQueryWrapper<GroupBuyUser> query = Wrappers.lambdaQuery();
        query.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
        query.eq(GroupBuyUser::getIsLeader, 1);
        query.eq(GroupBuyUser::getMerId, merId);
        if (ObjectUtil.isNotNull(recordId)) {
            query.eq(GroupBuyUser::getGroupRecordId, recordId);
        }
        if (ObjectUtil.isNull(limit)) {
            throw new CrmebException("查询参与拼团活动 限制不能为空");
        }
        query.orderByDesc(GroupBuyUser::getId);
        query.last("limit " + limit);
        return dao.selectList(query);
    }

    /**
     * 获取当前拼团活动下的已成功的购买数量
     *
     * @param activityId 拼团活动id
     * @return 购买数量累加
     */
    @Override
    public Integer getGroupBuySalesForAfterPay(Integer activityId) {
        LambdaQueryWrapper<GroupBuyUser> query = Wrappers.lambdaQuery();
        query.eq(GroupBuyUser::getGroupActivityId, activityId);
        query.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        // 退款状态为未退款
        query.eq(GroupBuyUser::getRefundStatus, OrderConstants.ORDER_REFUND_STATUS_NOT_APPLY);

        List<GroupBuyUser> groupBuyUsers = dao.selectList(query);
        Integer total = 0;
        if (!groupBuyUsers.isEmpty()) {
            total = groupBuyUsers.stream().map(GroupBuyUser::getPayNum).reduce(Integer::sum).get();
        }
        return total;
    }

    /**
     * 获取成功的拼团人数 注意这里是系统全部的拼团成功人数
     *
     * @return 成功的拼团人数
     */
    @Override
    public Integer getGroupBuyUserDoneTotalCount() {
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        // 支付成功的数量就开始算
        lambdaQueryWrapper.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 获取成功的拼团人数 注意这里是系统全部的拼团成功人数
     *
     * @return 成功的拼团人数
     */
    @Override
    public Integer getGroupBuyUserDoneTotalCount(Integer merId) {
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        // 支付成功的数量就开始算
        lambdaQueryWrapper.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        lambdaQueryWrapper.eq(GroupBuyUser::getMerId, merId);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 根据拼团活动id获取已成团订单数
     *
     * @param productId 拼团商品id
     * @return 拼团成团订单数
     */
    @Override
    public Integer getOrderDoneCountByProductIdAndActivityId(Integer productId, Integer activityId) {
        if (ObjectUtil.isEmpty(productId)) {
            throw new CrmebException("团购商品id 不能为空");
        }
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyUser::getProductGroupId, productId);
        lambdaQueryWrapper.eq(GroupBuyUser::getGroupActivityId, activityId);
        List<GroupBuyUser> groupBuyRecords = dao.selectList(lambdaQueryWrapper);
        Integer total = 0;
        if (!groupBuyRecords.isEmpty()) {
            total = groupBuyRecords.stream().map(GroupBuyUser::getPayNum).reduce(Integer::sum).get();
        }
        return total;
    }

    /**
     * 获取 当前拼团活动的成功的拼团人数
     *
     * @param activityId 当前拼团活动
     * @return 成功的拼团人数
     */
    @Override
    public List<GroupBuyUser> getGroupBuyUserDoneForActivityId(Integer activityId) {
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
        lambdaQueryWrapper.eq(GroupBuyUser::getGroupRecordId, activityId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取当前 拼团订单下已经参与的人
     *
     * @param recordId 拼团id
     * @return 拼团用户列表
     */
    @Override
    public List<GroupBuyUser> getGroupBuyUserAfterPayForRecordId(Integer recordId, Integer limit) {
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
        lambdaQueryWrapper.eq(GroupBuyUser::getGroupRecordId, recordId);
        if (limit > 0) { // 以免万人团 查询时指定的安全值
            lambdaQueryWrapper.last("limit " + limit);
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取当前 拼团订单成单的人员
     *
     * @param recordId 拼团id
     * @return 拼团用户列表
     */
    @Override
    public List<GroupBuyUser> getGroupBuyUserAfterOderDoneForRecordId(Integer recordId, Integer limit) {
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyUser::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        lambdaQueryWrapper.eq(GroupBuyUser::getGroupRecordId, recordId);
        if (limit > 0) { // 以免万人团 查询时指定的安全值
            lambdaQueryWrapper.last("limit " + limit);
        }
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据拼团记录ID获取拼团用户列表
     *
     * @param recordId 拼团记录ID
     * @return 当前拼团用户列表
     */
    @Override
    public List<GroupBuyUser> getListByGroupRecordId(Integer recordId) {
        LambdaQueryWrapper<GroupBuyUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyUser::getGroupRecordId, recordId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取当前用户在当前拼团活动下的拼团记录
     *
     * @param userId             用户id
     * @param groupBuyActivityId 拼团活动id
     * @param groupRecordId      拼团记录id
     * @param recordStatus       状态：0=进行中，10=已成功，-1=已失败
     * @return 拼团记录列表
     */
    @Override
    public List<GroupBuyUser> getCurrentGroupBuyActivityRecordListByUserId(Integer userId, Integer groupBuyActivityId, Integer groupRecordId, Integer recordStatus) {
        LambdaQueryWrapper<GroupBuyUser> query = Wrappers.lambdaQuery();
        query.eq(GroupBuyUser::getGroupUid, userId)
                .eq(GroupBuyUser::getGroupActivityId, groupBuyActivityId);

        if (groupRecordId > 0) {
            query.eq(GroupBuyUser::getGroupRecordId, groupRecordId);
            if (ObjectUtil.isNull(recordStatus)) {
                query.eq(GroupBuyUser::getRecordStatus, recordStatus);
            }

        }

        return dao.selectList(query);
    }

    /**
     * 当前活动的拼团id正在拼的数量
     */
    @Override
    public List<GroupBuyUser> getCurrentGroupBuyActivityRecordList(Integer groupBuyActivityId, Integer userId, Integer groupRecordId) {
        LambdaQueryWrapper<GroupBuyUser> query = Wrappers.lambdaQuery();
        query.eq(GroupBuyUser::getGroupActivityId, groupBuyActivityId);
        if (userId > 0) {
            query.eq(GroupBuyUser::getGroupUid, userId);
        }
        if (groupRecordId > 0) {
            query.eq(GroupBuyUser::getGroupRecordId, groupRecordId);
        }
        return dao.selectList(query);
    }

    /**
     * 根据订单编号 获取用户购买记录
     *
     * @param orderNo 订单编号
     */
    @Override
    public GroupBuyUser getByOrderNo(String orderNo) {
        LambdaQueryWrapper<GroupBuyUser> query = Wrappers.lambdaQuery();
        query.eq(GroupBuyUser::getOrderId, orderNo);
        query.last(" limit 1");
        return dao.selectOne(query);
    }

    /**
     * 拼团支付之前
     *
     * @param orderNo  订单编号
     * @param uid      用户id
     * @param avatar   用户头像
     * @param nickName 用户昵称
     */
    @Override
    public void beforePay(String orderNo, Integer payNum, Integer uid, String avatar, String nickName) {
        GroupBuyUser groupBuyUser = new GroupBuyUser();
        groupBuyUser.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
        groupBuyUser.setOrderStatus(OrderConstants.ORDER_STATUS_WAIT_PAY);// 拼团订单待支付
        groupBuyUser.setIsLeader(0);
        groupBuyUser.setGroupUid(uid);

        groupBuyUser.setOrderId(orderNo);
        groupBuyUser.setGroupAvatar(avatar);
        groupBuyUser.setGroupNickname(nickName);
        groupBuyUser.setPayNum(payNum);
        save(groupBuyUser);


    }

    /**
     * 拼团商品预下单替换订单号
     *
     * @param ptOrderNo PT订单号
     */
    @Override
    public void afterPay(String ptOrderNo) {
        Order currentOrder = orderService.getMerchantOrderNoByPtOrderNoForGroupBuy(ptOrderNo);

        LambdaQueryWrapper<GroupBuyUser> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GroupBuyUser::getOrderId, ptOrderNo);
        GroupBuyUser groupBuyUser = dao.selectOne(queryWrapper);
        logger.info("拼团订单编号修改对应拼团订单状态: ${}:", JSONObject.toJSONString(currentOrder));
        // 添加拼团记录  - 这里添加只是为了存储当前拼团下单数据，等待支付成功后会根据支付结果重新更新购买记录中的订单号为最终商户的订单号
        String cacheKey = StrUtil.format(RedisConstants.PINTUAN_ORDER_RECORD_KEY, ptOrderNo);
        if (!redisUtil.exists(cacheKey))
            throw new CrmebException("拼团记录读取缓存出错");
        GroupBuyRecordForCache groupBuyRecordForCache = JSONObject.parseObject(redisUtil.get(cacheKey).toString(), GroupBuyRecordForCache.class);
        GroupBuyRecord groupBuyRecord = groupBuyRecordService.newGroupBuyRecordOrContinueBuy(currentOrder.getOrderNo(),
                groupBuyRecordForCache.getPayNum(),
                groupBuyRecordForCache.getGroupBuyActivityId(),
                groupBuyRecordForCache.getProductId(),
                groupBuyRecordForCache.getGroupBuyRecordId(),
                groupBuyRecordForCache.getFictiStatus(),
                groupBuyRecordForCache.getMerId(),
                groupBuyRecordForCache.getAttrValueId(),
                groupBuyRecordForCache.getId(),
                groupBuyRecordForCache.getNickname(),
                groupBuyRecordForCache.getAvatar());

        groupBuyUser.setOrderId(currentOrder.getOrderNo());
        groupBuyUser.setOrderStatus(OrderConstants.ORDER_STATUS_WAIT_SHIPPING);
        groupBuyUser.setGroupActivityId(groupBuyRecordForCache.getGroupBuyActivityId());
        groupBuyUser.setGroupRecordId(groupBuyRecord.getGroupBuyingId());
        groupBuyUser.setProductGroupId(groupBuyRecordForCache.getProductId());
        groupBuyUser.setMerId(groupBuyRecordForCache.getMerId());
        if (Objects.equals(groupBuyRecordForCache.getId(), groupBuyRecord.getGroupLeaderUid())) {
            groupBuyUser.setIsLeader(1);
        }
        dao.updateById(groupBuyUser);

        logger.info("拼团分单后 - 替换订单编号完毕: ${}:", currentOrder.getOrderNo());

        // 根据用户购买记录写入拼团还是开团


        GroupBuyRecord currentGroupRecord = groupBuyRecordService.getById(groupBuyUser.getGroupRecordId());

        // 成团 真团，人数够了就成团 - 仅拼团
        if (ObjectUtil.isNotNull(currentGroupRecord) && currentGroupRecord.getBuyingCountNum().equals(currentGroupRecord.getBuyingNum())) {
            logger.info("真人拼团判断成功 - 正在执行");
            logger.info("真人拼团成功 ${}", JSONObject.toJSONString(currentGroupRecord));
            LambdaUpdateWrapper<GroupBuyRecord> zrUpdateWrapper = new LambdaUpdateWrapper<GroupBuyRecord>();
            zrUpdateWrapper.eq(GroupBuyRecord::getGroupBuyingId, currentGroupRecord.getGroupBuyingId())
                    .set(GroupBuyRecord::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
            groupBuyRecordService.update(zrUpdateWrapper);
            logger.info("真人拼团成 拼团记录写入完成");

            List<GroupBuyUser> listByGroupRecordList = groupBuyUserService.getListByGroupRecordId(currentGroupRecord.getGroupBuyingId());
            logger.info("拼团真人拼团即时成功 调用拼团成功事件:listByGroupRecordList:{}", JSONObject.toJSONString(listByGroupRecordList));
            logger.info("拼团真人拼团即时成功 调用拼团成功事件:currentGroupRecord:{}", JSONObject.toJSONString(currentGroupRecord));
            groupBuyRecordService.groupSuccess(currentGroupRecord, listByGroupRecordList);
            logger.info("真人拼团判断成功 - 正在结束");
        }

    }
}

