package com.zbkj.service.service.groupbuy.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.enums.GroupBuyRecordEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.express.ShippingTemplatesFree;
import com.zbkj.common.model.express.ShippingTemplatesRegion;
import com.zbkj.common.model.groupbuy.GroupBuyActivity;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.MerchantOrder;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.model.wechat.video.PayComponentProduct;
import com.zbkj.common.model.wechat.video.PayComponentProductSku;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.*;
import com.zbkj.common.request.groupbuy.GroupBuyRecordForCache;
import com.zbkj.common.request.groupbuy.GroupBuyRecordSearchRequest;
import com.zbkj.common.response.OrderNoResponse;
import com.zbkj.common.response.groupbuy.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.*;
import com.zbkj.service.dao.groupby.GroupBuyRecordDao;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivityService;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author dazongzi
* @description GroupBuyRecordServiceImpl 接口实现
* @date 2024-08-13
*/
@Service
public class GroupBuyRecordServiceImpl extends ServiceImpl<GroupBuyRecordDao, GroupBuyRecord> implements GroupBuyRecordService {


    private final Logger logger = LoggerFactory.getLogger(GroupBuyRecordServiceImpl.class);
    @Resource
    private GroupBuyRecordDao dao;
    @Autowired
    private GroupBuyActivityService groupBuyActivityService;
    @Autowired
    private GroupBuyActivitySkuService groupBuyActivitySkuService;
    @Autowired
    private GroupBuyUserService groupBuyUserService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private MerchantOrderService merchantOrderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ShippingTemplatesService shippingTemplatesService;
    @Autowired
    private ShippingTemplatesFreeService shippingTemplatesFreeService;
    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;
    @Autowired
    private PayComponentProductService payComponentProductService;
    @Autowired
    private PayComponentProductSkuService payComponentProductSkuService;
    @Autowired
    private CdkeyLibraryService cdkeyLibraryService;
    @Autowired
    private CardSecretService cardSecretService;
    @Autowired
    private SystemFormService systemFormService;
    @Autowired
    private RefundOrderManagerService refundOrderManagerService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author dazongzi
    * @since 2024-08-13
    * @return List<GroupBuyRecord>
    */
    @Override
    public PageInfo<GroupBuyActivityRecordAdminListResponse> getList(GroupBuyRecordSearchRequest request, PageParamRequest pageParamRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        HashMap<String, Object> map = new HashMap<>();
        map.put("recordStatus", request.getRecordStatus());
        if (StrUtil.isNotBlank(request.getGroupActivityName())) {
            map.put("groupActivityName", URLUtil.decode(request.getGroupActivityName()));
        }
        if (StrUtil.isNotBlank(request.getProductName())) {
            map.put("productName", URLUtil.decode(request.getProductName()));
        }
        if (StrUtil.isNotBlank(request.getStartTime())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getStartTime() + "," + request.getEndTime());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (admin.getMerId() > 0) {
            map.put("merId", admin.getMerId());
        } else {
            if (StrUtil.isNotBlank(request.getMerName())) {
                map.put("merName", URLUtil.decode(request.getMerName()));
            }
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        Page<GroupBuyRecord> groupBuyRecordsPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<GroupBuyActivityRecordAdminListResponse> responseList = dao.findAdminList(map);
        return CommonPage.copyPageInfo(groupBuyRecordsPage, responseList);
    }


    /**
     *  获取团购记录的表头数量
     * 状态：0=进行中，10=已成功，-1=已失败
     * @param request request
     * @param systemAdmin 当前登录管理员
     * @return GroupBuyActivityRecordListHeaderCount
     */
    @Override
    public GroupBuyActivityRecordListHeaderCount getListHeaderCount(GroupBuyRecordPageNumRequest request, SystemAdmin systemAdmin) {
        GroupBuyActivityRecordListHeaderCount headerCount = new GroupBuyActivityRecordListHeaderCount();
        headerCount.setIngNum(getListHeaderCountNum(request, 0, systemAdmin.getMerId()));
        headerCount.setSuccessNum(getListHeaderCountNum(request, 10, systemAdmin.getMerId()));
        headerCount.setFailNum(getListHeaderCountNum(request, -1, systemAdmin.getMerId()));
        return headerCount;
    }

    /**
     * 拼团开团记录详情
     *
     * @param id 开团记录ID
     */
    @Override
    public GroupBuyRecordDetailResponse getByAdminDetail(Integer id) {
        GroupBuyRecordDetailResponse response = new GroupBuyRecordDetailResponse();
        GroupBuyRecord groupBuyRecord = getByIdException(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0 && !groupBuyRecord.getMerId().equals(admin.getMerId())) {
            throw new CrmebException("拼团开团记录不存在");
        }

        response.setGroupBuyingId(groupBuyRecord.getGroupBuyingId());
        response.setRecordStatus(groupBuyRecord.getRecordStatus());
        response.setBuyingCountNum(groupBuyRecord.getBuyingCountNum());
        response.setYetBuyingNum(groupBuyRecord.getYetBuyingNum());
        response.setCreateTime(groupBuyRecord.getCreateTime());
        response.setEndTime(groupBuyRecord.getEndTime());
        // 商品数据
        Product product = productService.getById(groupBuyRecord.getProductGroupId());
        response.setProductName(product.getName());
        response.setProductImage(product.getImage());
        // 成员数据
        List<GroupBuyUser> groupBuyUserList = groupBuyUserService.getListByGroupRecordId(groupBuyRecord.getGroupBuyingId());
        List<GroupBuyRecordDetailMemberResponse> memberResponseList = groupBuyUserList.stream().map(groupBuyUser -> {
            GroupBuyRecordDetailMemberResponse memberResponse = new GroupBuyRecordDetailMemberResponse();
            User user = userService.getById(groupBuyUser.getGroupUid());
            Order order = orderService.getByOrderNo(groupBuyUser.getOrderId());
            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(groupBuyUser.getOrderId());
            OrderDetail orderDetail = orderDetailList.get(0);
            memberResponse.setId(groupBuyUser.getId());
            memberResponse.setMemberId(groupBuyUser.getGroupUid());
            memberResponse.setMemberNickname(user.getNickname());
            memberResponse.setMemberAvatar(user.getAvatar());
            memberResponse.setOrderNo(groupBuyUser.getOrderId());
            memberResponse.setOrderDetailSku(orderDetail.getSku());
            memberResponse.setPayNum(groupBuyUser.getPayNum());
            memberResponse.setPayPrice(order.getPayPrice());
            memberResponse.setRefundNum(orderDetail.getRefundNum());
            memberResponse.setIsLeader(groupBuyUser.getIsLeader());
            memberResponse.setCreateTime(groupBuyUser.getCreateTime());
            return memberResponse;
        }).collect(Collectors.toList());
        response.setMemberDataList(memberResponseList);
        return response;
    }

    private GroupBuyRecord getByIdException(Integer id) {
        GroupBuyRecord groupBuyRecord = getById(id);
        if (ObjectUtil.isNull(groupBuyRecord) || groupBuyRecord.getIsDel().equals(1)) {
            throw new CrmebException("拼团开团记录不存在");
        }
        return groupBuyRecord;
    }

    private Integer getListHeaderCountNum(GroupBuyRecordPageNumRequest request, Integer recordStatus, Integer merId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("recordStatus", recordStatus);
        if (StrUtil.isNotBlank(request.getGroupActivityName())) {
            map.put("groupActivityName", URLUtil.decode(request.getGroupActivityName()));
        }
        if (StrUtil.isNotBlank(request.getProductName())) {
            map.put("productName", URLUtil.decode(request.getProductName()));
        }
        if (StrUtil.isNotBlank(request.getStartTime())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getStartTime() + "," + request.getEndTime());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (merId > 0) {
            map.put("merId", merId);
        } else {
            if (StrUtil.isNotBlank(request.getMerName())) {
                map.put("merName", URLUtil.decode(request.getMerName()));
            }
        }
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        return dao.getAdminListHeaderCount(map);
    }


    /**
     * 根据拼团活动id获取已成团订单数
     *
     * @param groupBuyActivityId 拼团活动id
     * @return 拼团成团订单数
     */
    @Override
    public Integer getOrderDoneCount(Integer groupBuyActivityId) {
        if(ObjectUtil.isEmpty(groupBuyActivityId)){
            throw new CrmebException("团购id 不能为空");
        }
        LambdaQueryWrapper<GroupBuyRecord> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyRecord::getGroupActivityId, groupBuyActivityId);
        lambdaQueryWrapper.eq(GroupBuyRecord::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 创建订单 拼团
     *
     * @param orderRequest 订单参数
     * @param orderInfoVo  订单信息
     * @param user         用户信息
     * @return 订单号
     */
    @Override
    public OrderNoResponse validationBeforeCreateOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user) {
        OrderNoResponse response = new OrderNoResponse();
        // 团购商品均为单独购买，不加入购物车，所以这里应该只会出现一个商家信息，并且只会有一个商品
        for (PreMerchantOrderVo preMerchantOrderVo : orderInfoVo.getMerchantOrderVoList()) {
            for (PreOrderInfoDetailVo preOrderInfoDetailVo : preMerchantOrderVo.getOrderInfoList()) {
                // 团购商品库存校验 拼团商品本身 和 关联的具体商品
                GroupBuyActivityResponse groupBuyActivity = groupBuyActivityService.getGroupBuyActivityForFront(preOrderInfoDetailVo.getGroupBuyActivityId());
                // 校验活动
                if (ObjectUtil.isNull(groupBuyActivity)) {
                    throw new CrmebException("拼团活动不存在");
                }
                // 活动限购条件 是否在活动有效范围内
                if(!groupBuyActivity.getGroupProcess().equals(1)){ // 活动进程  0=未开始 1=进行中 2=已结束
                    throw new CrmebException("当前拼团活动已失效");
                }

                // 校验活动商品
                GroupBuyActivitySku currentActivitySku = groupBuyActivitySkuService.getByAttrId(groupBuyActivity.getId(), preOrderInfoDetailVo.getAttrValueId());
                if (ObjectUtil.isNull(currentActivitySku)) {
                    throw new CrmebException("拼团商品不存在");
                }
                if(currentActivitySku.getQuota() < preOrderInfoDetailVo.getPayNum()){
                    throw new CrmebException("拼团商品当前库存不足");
                }

                // 查询当前拼团活动是否还有余量 根据eb_group_buy_record的id来判断，有就是拼团，没有就是开团
                // 开团逻辑校验
                List<GroupBuyUser> currentGroupBuy =
                        groupBuyUserService.getCurrentGroupBuyActivityRecordListByUserId(user.getId(), groupBuyActivity.getId(), preOrderInfoDetailVo.getGroupBuyRecordId(), -1);
                // 计算当前登录的用户购买数量
                Integer currentUserBuyedCount = 0;
                if(!currentGroupBuy.isEmpty()){
                    currentUserBuyedCount = currentGroupBuy.stream().map(GroupBuyUser::getPayNum).reduce(Integer::sum).get();
                }
                logger.info("当前的购买的人员:{}", JSONObject.toJSONString(currentGroupBuy));

                List<GroupBuyUser> currentGroupBuyEd = new ArrayList<>();
                Integer historyGroupBuyCount = 0; // 历史已经购买过的
                if(preOrderInfoDetailVo.getGroupBuyRecordId() > 0){
                    currentGroupBuyEd =
                            groupBuyUserService.getCurrentGroupBuyActivityRecordList(groupBuyActivity.getId(), 0,preOrderInfoDetailVo.getGroupBuyRecordId());
                    if(!currentGroupBuyEd.isEmpty()){
                        historyGroupBuyCount = currentGroupBuyEd.stream().map(GroupBuyUser::getPayNum).reduce(Integer::sum).get();
                    }
                    logger.info("已经参与当前拼团的人员:{}", JSONObject.toJSONString(currentGroupBuyEd));
                }

                // 团购限量 真团 getAllQuota = 0 为不限购
                if(groupBuyActivity.getAllQuota() > 0 && (groupBuyActivity.getAllQuota() <= currentGroupBuy.size())){
                    throw new CrmebException("拼团人数已达上线");
                }
                // 当前判断真实拼团人数
                logger.info("当前拼团购买件数:{}", preOrderInfoDetailVo.getPayNum());
                logger.info("当前拼团历史已经购买件数:{}", historyGroupBuyCount);
                int total = preOrderInfoDetailVo.getPayNum() + historyGroupBuyCount;
                // 当前拼团购买是否超限，
                if((groupBuyActivity.getAllQuota() > 0 && preOrderInfoDetailVo.getPayNum() > groupBuyActivity.getAllQuota())){
                    throw new CrmebException("真人拼团已达上限");
                }
                // 当前拼团时候满员
                if(currentGroupBuyEd.size() > groupBuyActivity.getBuyCount()){
                    response.setGroupBuyIsFull(1);
                    return response;
                }
                // 限购开团
                int total_kai = currentUserBuyedCount + preOrderInfoDetailVo.getPayNum();
                // 开团限购上限
                if(groupBuyActivity.getAllQuota() > 0 && total_kai > groupBuyActivity.getAllQuota()){
                    throw new CrmebException("开团已达上限");
                }
                // 限购单次
                if(groupBuyActivity.getOncQuota() > 0 && preOrderInfoDetailVo.getPayNum() > groupBuyActivity.getOncQuota()){
                    response.setGroupBuyIsFull(1);
                    return response;
                }
                if(groupBuyActivity.getAllQuota() == 0 && groupBuyActivity.getBuyCount() > 0 && currentGroupBuyEd.size() >= groupBuyActivity.getBuyCount()){
                    response.setGroupBuyIsFull(1);
                    return response;
                }
                if(groupBuyActivity.getAllQuota() > 0 && groupBuyActivity.getBuyCount() > 0 && currentGroupBuyEd.size() >= groupBuyActivity.getBuyCount()){
                    response.setGroupBuyIsFull(1);
                    return response;
                }

                // 拼团商品库存
                if(currentActivitySku.getQuota() <= 0){
                    throw new CrmebException("拼团商品库存不足");
                }

                // 根据活动id查询当前是否有待支付订单
                Integer waitPayCount = orderDetailService.getByGroupBuyActivityId(preOrderInfoDetailVo.getGroupBuyActivityId(), preOrderInfoDetailVo.getProductId(), user.getId());
                total = total +waitPayCount;
                logger.info("拼团 有 待支付订单数量 :{}", waitPayCount);
                int buyNowCount = preOrderInfoDetailVo.getPayNum() + currentUserBuyedCount;
                logger.info("拼团 当前用户购买数量：{}", buyNowCount);
                if((groupBuyActivity.getAllQuota() > 0 && buyNowCount > groupBuyActivity.getAllQuota())){
                    throw new CrmebException("限购已达上限 4");
                }

            }
        }
        return response;
    }

    /**
     * 扣减库存并处理拼团后续业务逻辑
     *
     * @param orderNo      订单号
     * @param orderRequest 订单参数
     * @param orderInfoVo  订单信息
     * @param user         用户信息
     */
    @Override
    public void subStock(String orderNo, CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user) {
        logger.info("拼团扣减库存开始：${}", JSON.toJSONString(orderInfoVo));
        // 正确数据下面的循环只会执行一次
        for (PreMerchantOrderVo preMerchantOrderVo : orderInfoVo.getMerchantOrderVoList()) {
            for (PreOrderInfoDetailVo preOrderInfoDetailVo : preMerchantOrderVo.getOrderInfoList()) {
                // 扣减库存  eb_group_buy_activity_sku
                groupBuyActivitySkuService.subSKUStock(preOrderInfoDetailVo.getGroupBuyActivityId(), preOrderInfoDetailVo.getAttrValueId(), preOrderInfoDetailVo.getPayNum());
            }
        }

    }


    /**
     * 获取拼团活动成功用户列表
     *
     * @param groupActivityId 拼团活动id
     * @param limit           限制数量
     * @return
     */
    @Override
    public List<GroupBuyRecord> getGroupBuyUserActivityDoneList(Integer groupActivityId, Integer userId, Integer productId, int limit) {
        LambdaQueryWrapper<GroupBuyRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GroupBuyRecord::getGroupActivityId, groupActivityId);
        queryWrapper.eq(GroupBuyRecord::getProductGroupId, productId);
        queryWrapper.eq(GroupBuyRecord::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());

        queryWrapper.last("limit " + limit);
        return dao.selectList(queryWrapper);
    }


    /**
     * 开团或者拼团记录
     * @param groupActivityId 拼团活动id
     * @param groupProductId 拼团商品
     * @param groupBuyRecordId 拼团记录id
     * @param merId 商户id
     */
    @Override
    public GroupBuyRecord newGroupBuyRecordOrContinueBuy(String orderNo, Integer payNum, Integer groupActivityId,
                                                         Integer groupProductId, Integer groupBuyRecordId, Integer fictiStatus, Integer merId,
                                                         Integer skuid, Integer uid, String nickname, String avatar) {
        GroupBuyRecord record;
        GroupBuyActivity currentActivity = groupBuyActivityService.getById(groupActivityId);
        // 开团
        if(groupBuyRecordId == 0){
            currentActivity.setTotalActivityBegin(currentActivity.getTotalActivityBegin() + 1); // 拼团活动 开团数 统计

            record = new GroupBuyRecord();
            record.setProductGroupId(groupProductId);
            record.setMerId(merId);
            record.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
            record.setBuyingCountNum(currentActivity.getBuyCount());
            record.setBuyingNum(1); // 开团的第一个人数
            record.setYetBuyingNum(1); // 已参团人数
            record.setShowGroup(currentActivity.getShowGroup());
            record.setGroupLeaderUid(uid);
            record.setGroupLeaderNickname(nickname);
            record.setGroupActivityId(groupActivityId);
            record.setSkuid(skuid);
            record.setFictiStatus(fictiStatus);
            // 计算结束日期 当前时间在拼团有效时间范围内，加上拼团有效期来计算，生成结束日期和时间
            DateTime currentDate = DateUtil.date();
            DateTime groupDoneTime = DateUtil.offsetHour(currentDate, currentActivity.getValidHour());
            record.setEndTime(groupDoneTime);
            save(record);
            redisUtil.lPush(TaskConstants.TASK_GROUP_BUY_RECORD_STATUS_KEY, record.getGroupBuyingId());
            logger.info("拼团-开团-任务加入:{}", record.getGroupBuyingId());
        }else{
            record = getById(groupBuyRecordId);
            record.setBuyingNum(record.getBuyingNum() + 1); // 真实人数
            record.setYetBuyingNum(record.getYetBuyingNum() + 1); // 已参团人数
            updateById(record);
        }
        // 写入用户购买记录 eb_group_buy_user
        // 更新活动参与的数据统计
        // 参团订单数统计
        currentActivity.setTotalOrderBegin(currentActivity.getTotalOrderBegin() + 1);
        groupBuyActivityService.updateById(currentActivity);
        return record;
    }

    /**
     * 创建订单 拼团
     *
     * @param orderRequest 订单参数
     * @param orderInfoVo  订单信息
     * @param user         用户信息
     * @return 订单号
     */
    @Override
    public OrderNoResponse createOrder(CreateOrderRequest orderRequest, PreOrderInfoVo orderInfoVo, User user, String perOrderKey) {
        UserAddress userAddress = null;
        List<OrderMerchantRequest> orderMerchantRequestList = orderRequest.getOrderMerchantRequestList();
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
            orderRequest.setAddressId(0);
        } else {
            if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_EXPRESS))) {
                if (ObjectUtil.isNull(orderRequest.getAddressId())) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择收货地址");
                }
                userAddress = userAddressService.getById(orderRequest.getAddressId());
                if (ObjectUtil.isNull(userAddress) || userAddress.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "收货地址有误");
                }
            }
            if (orderMerchantRequestList.stream().anyMatch(e -> e.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP))) {
                orderMerchantRequestList.forEach(m -> {
                    if (m.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                        Merchant merchant = merchantService.getByIdException(m.getMerId());
                        if (!merchant.getIsTakeTheir()) {
                            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("{}没有配置店铺地址，请联系客服", merchant.getName()));
                        }
                    }
                });
            }
        }
// 校验商品库存
        List<MyRecord> skuRecordList = validateProductStock(orderInfoVo);

        orderInfoVo.getMerchantOrderVoList().forEach(e -> {
            orderMerchantRequestList.forEach(o -> {
                if (o.getMerId().equals(e.getMerId())) {
                    e.setShippingType(o.getShippingType());
                    e.setUserCouponId(o.getUserCouponId());
                    e.setCouponFee(BigDecimal.ZERO);
                    e.setMerCouponFee(BigDecimal.ZERO);
                }
            });
        });
        // 计算订单各种价格
        getFreightFee_V_1_8(orderInfoVo, userAddress, user.getIsPaidMember());
        orderInfoVo.setPlatUserCouponId(orderRequest.getPlatUserCouponId());
        orderInfoVo.setPlatCouponFee(BigDecimal.ZERO);
        orderInfoVo.setCouponFee(BigDecimal.ZERO);
        orderInfoVo.setMerCouponFee(BigDecimal.ZERO);

        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();

        // 拼团前校验
        OrderNoResponse responseValidation = validationBeforeCreateOrder(orderRequest, orderInfoVo, user);
        logger.info("拼团满员 = {}", JSON.toJSONString(responseValidation));
        if(ObjectUtil.isNotNull(responseValidation.getGroupBuyIsFull()) && responseValidation.getGroupBuyIsFull().equals(1)) return  responseValidation;

        // 平台订单
        Order order = new Order();
        String orderNo = CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_PLATFORM);
        order.setOrderNo(orderNo);
        order.setMerId(0);
        order.setUid(user.getId());
        order.setTotalNum(orderInfoVo.getOrderProNum());
        order.setProTotalPrice(orderInfoVo.getProTotalFee());
        order.setTotalPostage(orderInfoVo.getFreightFee());
        order.setSvipDiscountPrice(orderInfoVo.getSvipDiscountPrice());
        order.setTotalPrice(order.getProTotalPrice().add(order.getTotalPostage()));
        order.setCouponPrice(orderInfoVo.getCouponFee());
        order.setUseIntegral(merchantOrderVoList.stream().mapToInt(PreMerchantOrderVo::getUseIntegral).sum());
        order.setIntegralPrice(merchantOrderVoList.stream().map(PreMerchantOrderVo::getIntegralPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        order.setPayPrice(order.getTotalPrice().subtract(order.getSvipDiscountPrice()).subtract(order.getCouponPrice()).subtract(order.getIntegralPrice()));
        order.setPayPostage(order.getTotalPostage());
        order.setPaid(false);
        order.setCancelStatus(OrderConstants.ORDER_CANCEL_STATUS_NORMAL);
        order.setLevel(OrderConstants.ORDER_LEVEL_PLATFORM);
        order.setType(orderInfoVo.getType());// 默认普通订单
        order.setSecondType(orderInfoVo.getSecondType());
        order.setMerCouponPrice(orderInfoVo.getMerCouponFee());
        order.setPlatCouponPrice(orderInfoVo.getPlatCouponFee());
        order.setPlatCouponId(orderInfoVo.getPlatUserCouponId());
        order.setIsSvip(user.getIsPaidMember());
        order.setSystemFormId(orderInfoVo.getSystemFormId());
        order.setOrderExtend(StrUtil.isNotBlank(orderRequest.getOrderExtend()) ? systemAttachmentService.clearPrefix(orderRequest.getOrderExtend(), systemAttachmentService.getCdnUrl()) : "");

        // 拼团用到
        PreOrderInfoDetailVo detailVoForGroupBuy = new PreOrderInfoDetailVo();
        Integer merId = 0;

        // 商户订单
        List<Integer> couponIdList = CollUtil.newArrayList();
        if (orderRequest.getPlatUserCouponId() > 0) {
            couponIdList.add(orderRequest.getPlatUserCouponId());
        }
        List<MerchantOrder> merchantOrderList = new ArrayList<>();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            MerchantOrder merchantOrder = new MerchantOrder();
            merchantOrder.setOrderNo(order.getOrderNo());
            merchantOrder.setMerId(merchantOrderVo.getMerId());
            merchantOrder.setUid(user.getId());
            for (OrderMerchantRequest om : orderMerchantRequestList) {
                if (om.getMerId().equals(merchantOrderVo.getMerId())) {
                    if (StrUtil.isNotBlank(om.getRemark())) {
                        merchantOrder.setUserRemark(om.getRemark());
                    }
                    merchantOrder.setShippingType(om.getShippingType());
                    break;
                }
            }
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                    || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                    || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
                merchantOrder.setRealName(user.getNickname());
                merchantOrder.setUserPhone(user.getPhone());
                merchantOrder.setUserAddress("");
            } else if (merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merchantOrder.setUserAddress(merchantOrderVo.getMerName());
                merchantOrder.setVerifyCode(String.valueOf(CrmebUtil.randomCount(1111111111, 999999999)));
            } else {
                merchantOrder.setRealName(userAddress.getRealName());
                merchantOrder.setUserPhone(userAddress.getPhone());
                String userAddressStr = userAddress.getProvince() + userAddress.getCity() + userAddress.getDistrict() + userAddress.getStreet() + userAddress.getDetail();
                merchantOrder.setUserAddress(userAddressStr);
            }
            merchantOrder.setTotalNum(merchantOrderVo.getProTotalNum());
            merchantOrder.setProTotalPrice(merchantOrderVo.getProTotalFee());
            merchantOrder.setTotalPostage(merchantOrderVo.getFreightFee());
            merchantOrder.setTotalPrice(merchantOrder.getProTotalPrice().add(merchantOrder.getTotalPostage()));
            merchantOrder.setPayPostage(merchantOrder.getTotalPostage());
            merchantOrder.setUseIntegral(merchantOrderVo.getUseIntegral());
            merchantOrder.setIntegralPrice(merchantOrderVo.getIntegralPrice());
            merchantOrder.setCouponId(merchantOrderVo.getUserCouponId());
            if (merchantOrder.getCouponId() > 0) {
                couponIdList.add(merchantOrder.getCouponId());
            }
            merchantOrder.setSvipDiscountPrice(merchantOrderVo.getSvipDiscountPrice());
            List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList();
            BigDecimal merCouponPrice = detailVoList.stream().map(PreOrderInfoDetailVo::getMerCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantOrder.setMerCouponPrice(merCouponPrice);
            BigDecimal platCouponPrice = detailVoList.stream().map(PreOrderInfoDetailVo::getPlatCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            merchantOrder.setPlatCouponPrice(platCouponPrice);
            merchantOrder.setCouponPrice(merchantOrder.getMerCouponPrice().add(merchantOrder.getPlatCouponPrice()));
            merchantOrder.setPayPrice(merchantOrder.getTotalPrice().subtract(merchantOrder.getSvipDiscountPrice()).subtract(merchantOrder.getCouponPrice()).subtract(merchantOrder.getIntegralPrice()));
            merchantOrder.setGainIntegral(0);
            merchantOrder.setType(order.getType());
            merchantOrder.setSecondType(order.getSecondType());
            merchantOrder.setIsSvip(order.getIsSvip());

            merchantOrderList.add(merchantOrder);

            for (PreOrderInfoDetailVo detailVo : detailVoList) {
                OrderDetail orderDetail = new OrderDetail();
                detailVoForGroupBuy = detailVo;
                orderDetail.setOrderNo(order.getOrderNo());
                orderDetail.setMerId(merchantOrder.getMerId());
                merId = merchantOrder.getMerId();
                orderDetail.setUid(user.getId());
                orderDetail.setProductId(detailVo.getProductId());
                orderDetail.setProductName(detailVo.getProductName());
                orderDetail.setImage(detailVo.getImage());
                orderDetail.setAttrValueId(detailVo.getAttrValueId());
                orderDetail.setSku(detailVo.getSku());
                orderDetail.setPrice(detailVo.getPrice());
                orderDetail.setPayNum(detailVo.getPayNum());
                orderDetail.setWeight(detailVo.getWeight());
                orderDetail.setVolume(detailVo.getVolume());
                orderDetail.setProductType(detailVo.getProductType());
                orderDetail.setProductMarketingType(detailVo.getProductMarketingType());
                orderDetail.setSubBrokerageType(detailVo.getSubBrokerageType());
                orderDetail.setBrokerage(detailVo.getBrokerage());
                orderDetail.setBrokerageTwo(detailVo.getBrokerageTwo());
                orderDetail.setFreightFee(detailVo.getFreightFee());

                orderDetail.setUseIntegral(detailVo.getUseIntegral());
                orderDetail.setIntegralPrice(detailVo.getIntegralPrice());
                orderDetail.setPayPrice(BigDecimal.ZERO);

                orderDetail.setIsSvip(order.getIsSvip());
                orderDetail.setIsPaidMemberProduct(detailVo.getIsPaidMember());
                orderDetail.setVipPrice(detailVo.getVipPrice());

                orderDetail.setMerCouponPrice(detailVo.getMerCouponPrice());
                orderDetail.setPlatCouponPrice(detailVo.getPlatCouponPrice());
                orderDetail.setCouponPrice(detailVo.getMerCouponPrice().add(detailVo.getPlatCouponPrice()));
                orderDetail.setProRefundSwitch(detailVo.getProRefundSwitch());
                BigDecimal detailPayPrice = orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getPayNum().toString())).add(orderDetail.getFreightFee()).subtract(orderDetail.getCouponPrice()).subtract(orderDetail.getIntegralPrice());

                if (detailPayPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new CrmebException("订单详情价格计算错误，详情价格不能小于0");
                }
                orderDetail.setPayPrice(detailPayPrice);
                orderDetail.setGroupBuyActivityId(detailVoForGroupBuy.getGroupBuyActivityId());
                orderDetailList.add(orderDetail);
            }
        }

        PreOrderInfoDetailVo finalDetailVoForGroupBuy = detailVoForGroupBuy;
        Integer finalMerId = merId;
        Boolean execute = transactionTemplate.execute(e -> {
            Boolean result = true;
                if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)) { // 云盘订单
                    MyRecord skuRecord = skuRecordList.get(0);
                    // 扣减库存
                    // 云盘商品口库存
                    result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                        return result;
                    }
                    // 云盘商品规格扣库存
                    result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("attrValueVersion"));
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                        return result;
                    }
                    orderDetailList.forEach(detail -> {
                        detail.setExpand(skuRecord.getStr("expand"));
                    });
                }
                if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) { // 卡密订单
                    MyRecord skuRecord = skuRecordList.get(0);
                    // 扣减库存
                    // 卡密商品口库存
                    result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                        return result;
                    }
                    // 卡密商品规格扣库存
                    result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("type"), skuRecord.getInt("attrValueVersion"));
                    if (!result) {
                        e.setRollbackOnly();
                        logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                        return result;
                    }
                    Integer cdkeyId = skuRecord.getInt("cdkeyId");
                    List<Integer> csIdList = new ArrayList<>();
                    for (int i = 0; i < skuRecord.getInt("num"); i++) {
                        CardSecret cardSecret = cardSecretService.consume(cdkeyId);
                        if (ObjectUtil.isNull(cardSecret)) {
                            e.setRollbackOnly();
                            logger.error("生成订单消费卡密失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                            return Boolean.FALSE;
                        }
                        csIdList.add(cardSecret.getId());
                    }
                    String cardSecretIds = StrUtil.join(",", csIdList);
                    orderDetailList.forEach(detail -> {
                        detail.setCardSecretIds(cardSecretIds);
                    });
                    cdkeyLibraryService.operationUseNum(cdkeyId, csIdList.size(), Constants.OPERATION_TYPE_ADD);
                }
                if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {// 视频号订单
                    MyRecord skuRecord = skuRecordList.get(0);
                    // 商品规格表扣库存
                    productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, ProductConstants.PRODUCT_TYPE_COMPONENT, skuRecord.getInt("attrValueVersion"));
                    // 视频商品规格表扣库存
                    payComponentProductSkuService.operationStock(skuRecord.getInt("skuId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, skuRecord.getInt("skuVersion"));
                    // 视频号商品扣库存
                    payComponentProductService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                }
                if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_NORMAL)) { // 普通商品
                    // 扣减库存
                    for (MyRecord skuRecord : skuRecordList) {
                        // 普通商品口库存
                        result = productService.operationStock(skuRecord.getInt("productId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT);
                        if (!result) {
                            e.setRollbackOnly();
                            logger.error("生成订单扣减商品库存失败,预下单号：{},商品ID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("productId"));
                            return result;
                        }
                        // 普通商品规格扣库存
                        result = productAttrValueService.operationStock(skuRecord.getInt("attrValueId"), skuRecord.getInt("num"), Constants.OPERATION_TYPE_SUBTRACT, ProductConstants.PRODUCT_TYPE_NORMAL, skuRecord.getInt("attrValueVersion"));
                        if (!result) {
                            e.setRollbackOnly();
                            logger.error("生成订单扣减商品sku库存失败,预下单号：{},商品skuID：{}", orderRequest.getPreOrderNo(), skuRecord.getInt("attrValueId"));
                            return result;
                        }
                    }
                }
            if (order.getType().equals(OrderConstants.ORDER_TYPE_PITUAN)) {
                order.setGroupBuyRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
                // 设置拼团订单支付状态 = 支付前
                logger.info("创建订单 - 开始扣减拼团库存了: ${}", JSON.toJSONString(order));
                subStock(order.getOrderNo(), orderRequest, orderInfoVo, user);
                // 写入用户购买记录 eb_group_buy_user
                groupBuyUserService.beforePay(orderNo, finalDetailVoForGroupBuy.getPayNum(), user.getId(), user.getAvatar(), user.getNickname());
                GroupBuyActivity currentActivity = groupBuyActivityService.getById(finalDetailVoForGroupBuy.getGroupBuyActivityId());
                GroupBuyRecordForCache cache = new  GroupBuyRecordForCache();
                cache.setGroupBuyActivityId(finalDetailVoForGroupBuy.getGroupBuyActivityId());
                cache.setGroupBuyRecordId(finalDetailVoForGroupBuy.getGroupBuyRecordId());
                cache.setProductId(finalDetailVoForGroupBuy.getProductId());
                cache.setAttrValueId(finalDetailVoForGroupBuy.getAttrValueId());
                cache.setPayNum(finalDetailVoForGroupBuy.getPayNum());
                cache.setMerId(finalMerId);
                cache.setAvatar(user.getAvatar());
                cache.setId(user.getId());
                cache.setNickname(user.getNickname());
                cache.setFictiStatus(currentActivity.getFictiStatus());
                redisUtil.set(StrUtil.format(RedisConstants.PINTUAN_ORDER_RECORD_KEY, orderNo), JSONObject.toJSONString(cache));
            }

            orderService.save(order);
            merchantOrderService.saveBatch(merchantOrderList);
            orderDetailService.saveBatch(orderDetailList);

            if (CollUtil.isNotEmpty(couponIdList)) {
                couponUserService.useBatch(couponIdList);
            }
            // 生成订单日志
            orderStatusService.createLog(order.getOrderNo(), OrderStatusConstants.ORDER_STATUS_CREATE, OrderStatusConstants.ORDER_LOG_MESSAGE_CREATE);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("订单生成失败");
        }

        // 删除缓存订单
        if (redisUtil.exists(perOrderKey)) {
            redisUtil.delete(perOrderKey);
        }

        // 加入自动未支付自动取消队列
        redisUtil.lPush(TaskConstants.ORDER_TASK_REDIS_KEY_AUTO_CANCEL_KEY, order.getOrderNo());

        OrderNoResponse response = new OrderNoResponse();
        response.setOrderNo(order.getOrderNo());
        response.setPayPrice(order.getPayPrice());
        return response;
    }

    /**
     * 拼团商品预下单
     * @param detailRequest 拼团商品预下单参数
     * @return 预下单对象
     */
    @Override
    public PreMerchantOrderVo validatePreOrderGroupBuy(PreOrderDetailRequest detailRequest) {
        if (ObjectUtil.isNull(detailRequest.getProductId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品编号不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getAttrValueId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格属性值不能为空");
        }
        if (ObjectUtil.isNull(detailRequest.getProductNum()) || detailRequest.getProductNum() <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买数量必须大于0");
        }
        Product product = productService.getById(detailRequest.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
        }
//        if (!product.getIsShow()) {
//            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已下架，请刷新后重新选择");
//        }
        if (product.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
        }
        // 查询商品规格属性值信息
        ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailRequest.getAttrValueId(),
                product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
        }
        if (!attrValue.getIsShow()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息无效，请刷新后重新选择");
        }
        if (attrValue.getStock() < detailRequest.getProductNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
        }
        Merchant merchant = new Merchant();

        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            User user = userService.getInfo();
            if (user.getIntegral() < (attrValue.getRedeemIntegral() * detailRequest.getProductNum())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分不足，无法购买此商品");
            }
        } else {
            merchant = merchantService.getByIdException(product.getMerId());
            if (!merchant.getIsSwitch()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新选择商品");
            }
            if (!merchant.getIsTakeTheir() && product.getDeliveryMethod().equals("2")) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此商品配送方式设置错误，请联系客服");
            }
        }
        GroupBuyActivitySku groupBuyActivitySku = groupBuyActivitySkuService.getByAttrId(detailRequest.getGroupBuyActivityId(), detailRequest.getAttrValueId());

        // 构造PreMerchantOrderVo对象
        PreMerchantOrderVo merchantOrderVo = new PreMerchantOrderVo();
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            merchantOrderVo.setMerId(0);
            merchantOrderVo.setMerName("");
            merchantOrderVo.setTakeTheirSwitch(false);
            merchantOrderVo.setIsSelf(false);
        } else {
            merchantOrderVo.setMerId(merchant.getId());
            merchantOrderVo.setMerName(merchant.getName());
            merchantOrderVo.setTakeTheirSwitch(merchant.getIsTakeTheir());
            merchantOrderVo.setIsSelf(merchant.getIsSelf());
        }
        merchantOrderVo.setType(OrderConstants.ORDER_TYPE_PITUAN);
        merchantOrderVo.setSecondType(product.getType());
        merchantOrderVo.setFreightFee(BigDecimal.ZERO);
        merchantOrderVo.setCouponFee(BigDecimal.ZERO);
        merchantOrderVo.setUserCouponId(0);
        merchantOrderVo.setDeliveryMethodMer(product.getDeliveryMethod());
        PreOrderInfoDetailVo detailVo = new PreOrderInfoDetailVo();
        detailVo.setProductId(product.getId());
        detailVo.setProductName(product.getName());
        detailVo.setAttrValueId(attrValue.getId());
        detailVo.setSku(attrValue.getSku());
        detailVo.setPrice(groupBuyActivitySku.getActivePrice());
        detailVo.setPayPrice(groupBuyActivitySku.getActivePrice());
        detailVo.setPayNum(detailRequest.getProductNum());
        detailVo.setImage(StrUtil.isNotBlank(attrValue.getImage()) ? attrValue.getImage() : product.getImage());
        detailVo.setVolume(attrValue.getVolume());
        detailVo.setWeight(attrValue.getWeight());
        detailVo.setTempId(product.getTempId());
        detailVo.setSubBrokerageType(product.getIsSub() ? 1 : 2);
        detailVo.setBrokerage(attrValue.getBrokerage());
        detailVo.setBrokerageTwo(attrValue.getBrokerageTwo());
        detailVo.setIsPaidMember(product.getIsPaidMember());
        detailVo.setVipPrice(BigDecimal.ZERO);
        if (detailVo.getSubBrokerageType() == 2) {
            String firstRatio = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_FIRST_RATIO);
            String secondRatio = systemConfigService.getValueByKey(SysConfigConstants.RETAIL_STORE_BROKERAGE_SECOND_RATIO);
            detailVo.setBrokerage(StrUtil.isNotBlank(firstRatio) ? Integer.parseInt(firstRatio) : 0);
            detailVo.setBrokerageTwo(StrUtil.isNotBlank(secondRatio) ? Integer.parseInt(secondRatio) : 0);
        }
        detailVo.setProductType(product.getType());
        detailVo.setProductMarketingType(product.getMarketingType());
        detailVo.setDeliveryMethod(product.getDeliveryMethod());
        detailVo.setProRefundSwitch(product.getRefundSwitch());
        List<PreOrderInfoDetailVo> infoList = CollUtil.newArrayList();
        infoList.add(detailVo);
        if (product.getSystemFormId() > 0) {
            merchantOrderVo.setSystemFormId(product.getSystemFormId());
            SystemForm systemForm = systemFormService.getById(product.getSystemFormId());
            merchantOrderVo.setSystemFormValue(systemForm.getFormValue());
        }
        if (product.getType().equals(5) || product.getType().equals(6)) {
            merchantOrderVo.setShippingType(3);
        } else {
            if (product.getDeliveryMethod().equals("2")) {
                merchantOrderVo.setShippingType(2);
            } else {
                merchantOrderVo.setShippingType(1);
            }
        }

        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_INTEGRAL)) {
            detailVo.setRedeemIntegral(attrValue.getRedeemIntegral());
        }
        merchantOrderVo.setOrderInfoList(infoList);
        return merchantOrderVo;
    }


    /**
     * 计算拼团记录时候完成 用作后台任务
     * @param groupBuyRecordId 拼团记录id
     * @return 拼团记录结果
     */
    private Boolean doneGroupBuyRecord(Integer groupBuyRecordId) {
        GroupBuyRecord currentGroupBuyRecord = getById(groupBuyRecordId);
        if(ObjectUtil.isNull(currentGroupBuyRecord)){
            logger.error("拼团记录不存在，id={}", groupBuyRecordId);
            return Boolean.TRUE;
        }
        if(!currentGroupBuyRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode())){
            logger.error("拼团记录不是在拼团中的状态，id={}", groupBuyRecordId);
            return Boolean.TRUE;
        }
        logger.info("拼团任务正在执行的拼团数据 :{}", JSONObject.toJSONString(currentGroupBuyRecord));
        logger.info("当前任务差时:{}", currentGroupBuyRecord.getEndTime().getTime() - System.currentTimeMillis());
        // 根据现在时间和拼团记录的结束时间对比，查看拼团是否到结束时间，有效时间内已经拼满的直接成团，时间到还没满团的，则拼团失败，修改失败状态后该订单全部发起退款
        if(currentGroupBuyRecord.getEndTime().getTime() < System.currentTimeMillis()){
            // 真团
            if(currentGroupBuyRecord.getYetBuyingNum().equals(currentGroupBuyRecord.getBuyingCountNum())
                    && currentGroupBuyRecord.getFictiStatus().equals(0)){
                // 满员时并且时间到的时候，同时检查当前订单是否均已支付 未支付不成团
                List<GroupBuyUser> groupBuyUserList = groupBuyUserService.getListByGroupRecordId(currentGroupBuyRecord.getGroupBuyingId());
                // 获取到当前拼团记录下的所有拼团用户后检查时候都支付成功
                List<GroupBuyUser> groupBuyUserAsPayed =
                        groupBuyUserList.stream().filter(groupBuyUser -> groupBuyUser.getOrderStatus().equals(OrderConstants.ORDER_STATUS_WAIT_SHIPPING)).collect(Collectors.toList());
                logger.info("拼团任务 - 当前拼团任务中有未支付订单 :{}", JSON.toJSONString(groupBuyUserAsPayed));
                if(groupBuyUserAsPayed.size() != groupBuyUserList.size()){
                    logger.info("拼团 - 真人 - 失败开始执行");
                    groupFailed(currentGroupBuyRecord);
                    logger.info("拼团 - 真人 - 失败结束执行");
                }else{
                    // 拼团成功
                    logger.info("拼团 - 真人 - 成功开始执行");
                    groupSuccess(currentGroupBuyRecord, groupBuyUserList);
                    logger.info("拼团 - 真人 成功结束执行");
                }
                return Boolean.TRUE;
            }else if(currentGroupBuyRecord.getFictiStatus().equals(1)){
            // 虚拟成团
                int countNeed = currentGroupBuyRecord.getBuyingCountNum() - currentGroupBuyRecord.getYetBuyingNum();
                // 满员时并且时间到的时候，同时检查当前订单是否均已支付 未支付不成团
                List<GroupBuyUser> groupBuyUserList = groupBuyUserService.getListByGroupRecordId(currentGroupBuyRecord.getGroupBuyingId());
                logger.info("拼团 - 虚拟 - 成功开始执行");
                groupSuccess(currentGroupBuyRecord, groupBuyUserList);
                logger.info("拼团 - 虚拟 成功结束执行");
                return Boolean.TRUE;
            }else{
            // 拼团失败
                logger.info("拼团 - 失败开始执行");
                groupFailed(currentGroupBuyRecord);
                logger.info("拼团 - 失败结束执行");
                return Boolean.TRUE;
            }
        }else{
            logger.info("拼团任务执行没有找到要计算的任务！！");
        }

        return Boolean.FALSE;
    }


    @Override
    public void groupSuccess(GroupBuyRecord currentGroupBuyRecord, List<GroupBuyUser> groupBuyUserList) {
        logger.info("真人拼团即时成功的也会调用");
        logger.info("正在执行拼团 成团任务 START :currentGroupBuyRecord| {}", JSONObject.toJSONString(currentGroupBuyRecord));
        logger.info("正在执行拼团 成团任务 START :groupBuyUserList| {}", JSONObject.toJSONString(groupBuyUserList));
        currentGroupBuyRecord.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        updateById(currentGroupBuyRecord);
        List<Order> orderListForGroupUpdate = new ArrayList<>();
        groupBuyUserList.forEach(groupBuyUser -> {
            groupBuyUser.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
            // 修改拼团订单状态为拼团成功
            Order order = orderService.getByOrderNo(groupBuyUser.getOrderId());
            order.setGroupBuyRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
            // 判断是否核销订单
            MerchantOrder merchantOrder = merchantOrderService.getOneByOrderNo(order.getOrderNo());
            if(merchantOrder.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)){
                order.setStatus(OrderConstants.ORDER_STATUS_AWAIT_VERIFICATION);
            }
            orderListForGroupUpdate.add(order);
        });
        logger.info("更新的订单有：{}", orderListForGroupUpdate);
        orderService.updateBatchById(orderListForGroupUpdate);
        logger.info("更新的订单状态成功");
        groupBuyUserService.updateBatchById(groupBuyUserList);
        // 更新成团数和成团订单数
        GroupBuyActivity activity = groupBuyActivityService.getById(currentGroupBuyRecord.getGroupActivityId());
        activity.setTotalActivityDone(activity.getTotalActivityDone() +1);
        activity.setTotalOrderDone(activity.getTotalOrderDone() + groupBuyUserList.size());
        groupBuyActivityService.updateById(activity);
        logger.info("正在执行拼团 成团任务 END");
        // 判断是否虚拟商品 执行自动发货
        for (Order order : orderListForGroupUpdate) {
            if (order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD) || order.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)) {
                orderService.virtualShipment(order);
            }
        }

    }



    private void groupFailed(GroupBuyRecord currentGroupBuyRecord) {
        logger.info("正在执行拼团 失败任务 START :currentGroupBuyRecord| {}", JSONObject.toJSONString(currentGroupBuyRecord));
        // 拼团失败
        currentGroupBuyRecord.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_FAIL.getCode());
        updateById(currentGroupBuyRecord);
        // 修改用户拼团订单状态
        List<GroupBuyUser> groupBuyUserList = groupBuyUserService.getListByGroupRecordId(currentGroupBuyRecord.getGroupBuyingId());
        logger.info("拼团失败的用户:{}", JSONObject.toJSONString(groupBuyUserList));
        groupBuyUserList.forEach(groupBuyUser -> {
            groupBuyUser.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_FAIL.getCode());
            // 修改拼团订单状态为拼团成功
            Order order = orderService.getByOrderNo(groupBuyUser.getOrderId());
            order.setGroupBuyRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_FAIL.getCode());
            // 调用批量退款任务 这里调用的商家主动退款功能
            logger.info("拼团失败 - 开始退款");
//            List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            List<OrderDetail> orderDetailList = new ArrayList<>();

            // 整单退款
            orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
            for (int i = 0; i < orderDetailList.size(); ) {
                OrderDetail orderDetail = orderDetailList.get(i);
                if (orderDetail.getApplyRefundNum() > 0) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "选择的退款商品订单详情存在退款申请，无法操作直接退款,订单详情ID=" + orderDetail.getId());
                }
                if (orderDetail.getPayNum() <= orderDetail.getRefundNum()) {
                    orderDetailList.remove(i);
                    continue;
                }
                orderDetail.setApplyRefundNum(orderDetail.getPayNum() - orderDetail.getRefundNum());
                i++;
            }
            refundOrderManagerService.merchantDirectRefund(order, orderDetailList);
            orderService.updateById(order);
            logger.info("拼团失败 - 结束退款");
        });
        groupBuyUserService.updateBatchById(groupBuyUserList);
    }


    /**
     * 拼团订单 订单退款
     */
    @Override
    public void groupBuyOrderRefund() {
        String redisKey = TaskConstants.TASK_GROUP_BUY_RECORD_STATUS_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("GroupBuyRecordServiceImpl.groupBuyOrderRefund | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderNoData = redisUtil.getRightPop(redisKey, 10L);
            if (ObjectUtil.isNull(orderNoData)) {
                continue;
            }
            try {
                logger.info("拼图订单正在执行的任务 | 获取到数据:" + orderNoData);
                Boolean result = doneGroupBuyRecord(Integer.valueOf(orderNoData.toString()));
                if (!result) {
                    redisUtil.lPush(redisKey, orderNoData);
                }
            } catch (Exception e) {
                logger.error("拼团订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderNoData);
            }
        }
    }

    /**
     * 拼团订单 订单退款 超卖
     */
    @Override
    public void groupBuyOrderRefundEt() {
        rollBackIsBuyed();
    }

    /*************************************** 私有方法 *********************************************/

    private List<MyRecord> validateProductStock(PreOrderInfoVo orderInfoVo) {
        List<MyRecord> recordList = CollUtil.newArrayList();
        if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIDEO)) {// 视频号订单
            // 查询商品信息 视频号都是单品下单
            List<PreOrderInfoDetailVo> detailVos = orderInfoVo.getMerchantOrderVoList().get(0).getOrderInfoList();
            PayComponentProduct product = payComponentProductService.getById(detailVos.get(0).getProductId());
            if (ObjectUtil.isNull(product)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品信息不存在，请刷新后重新选择");
            }
            if (product.getIsDel()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已删除，请刷新后重新选择");
            }
            if (!product.getStatus().equals(5)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品已下架，请刷新后重新选择");
            }
            if (product.getStock().equals(0) || product.getStock() < detailVos.get(0).getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品库存不足，请刷新后重新选择");
            }
            // 查询商品规格属性值信息
            ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(detailVos.get(0).getAttrValueId(), detailVos.get(0).getProductId(), ProductConstants.PRODUCT_TYPE_COMPONENT);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息不存在，请刷新后重新选择");
            }
            if (!attrValue.getIsShow()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格信息无效，请刷新后重新选择");
            }
            if (attrValue.getStock().equals(0) || attrValue.getStock() < detailVos.get(0).getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品规格库存不足，请刷新后重新选择");
            }
            // 查询视频商品规格属性值
            PayComponentProductSku productSku = payComponentProductSkuService.getByProIdAndAttrValueId(product.getId(), attrValue.getId());
            if (ObjectUtil.isNull(productSku)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品sku信息不存在，请刷新后重新选择");
            }
            if (productSku.getStockNum().equals(0) || productSku.getStockNum() < detailVos.get(0).getPayNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品sku库存不足，请刷新后重新选择");
            }

            MyRecord record = new MyRecord();
            record.set("productId", product.getId());
            record.set("attrValueId", detailVos.get(0).getAttrValueId());
            record.set("attrValueVersion", attrValue.getVersion());
            record.set("num", detailVos.get(0).getPayNum());
            record.set("skuId", productSku.getId());
            record.set("skuVersion", productSku.getVersion());
            recordList.add(record);
            return recordList;
        }
        // 普通商品
        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        merchantOrderVoList.forEach(merchantOrderVo -> {
            Merchant merchant = merchantService.getByIdException(merchantOrderVo.getMerId());
            if (!merchant.getIsSwitch()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户已关闭，请重新下单");
            }
            merchantOrderVo.getOrderInfoList().forEach(info -> {
                // 查询商品信息
                Product product = productService.getById(info.getProductId());
                if (ObjectUtil.isNull(product) || product.getIsDel()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品信息不存在");
                }
                // 排除审核失败的商品
                if(product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) ||
                        product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) throw new CrmebException("当前商品为审核或者审核拒绝");

                if (product.getStock().equals(0) || info.getPayNum() > product.getStock()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
                }
                // 查询商品规格属性值信息
                ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(
                        info.getAttrValueId(), info.getProductId(), product.getType(), product.getMarketingType());
                if (ObjectUtil.isNull(attrValue)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品规格信息不存在");
                }
                if (attrValue.getStock() < info.getPayNum()) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
                }
                MyRecord record = new MyRecord();
                record.set("productId", info.getProductId());
                record.set("num", info.getPayNum());
                record.set("attrValueId", info.getAttrValueId());
                record.set("attrValueVersion", attrValue.getVersion());
                record.set("type", attrValue.getType());
                record.set("marketingType", attrValue.getMarketingType());
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
                    record.set("expand", attrValue.getExpand());
                }
                if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
                    record.set("cdkeyId", attrValue.getCdkeyId());
                }
                record.set("isPaidMember", product.getIsPaidMember());
                record.set("vipPrice", attrValue.getVipPrice());
                recordList.add(record);
            });
        });
        return recordList;
    }

    private void getFreightFee_V_1_8(PreOrderInfoVo orderInfoVo, UserAddress userAddress, Boolean userIsPaidMember) {
        BigDecimal freightFee = BigDecimal.ZERO;

        List<PreMerchantOrderVo> merchantOrderVoList = orderInfoVo.getMerchantOrderVoList();
        for (PreMerchantOrderVo merchantOrderVo : merchantOrderVoList) {
            if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CLOUD)
                    || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_CDKEY)
                    || orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_VIRTUALLY)) {
                orderInfoVo.setAddressId(0);
                merchantOrderVo.setFreightFee(BigDecimal.ZERO);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            BigDecimal storePostage = BigDecimal.ZERO;
            if (merchantOrderVo.getShippingType().equals(OrderConstants.ORDER_SHIPPING_TYPE_PICK_UP)) {
                merchantOrderVo.setFreightFee(storePostage);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            if (orderInfoVo.getSecondType().equals(OrderConstants.ORDER_SECOND_TYPE_INTEGRAL)) {
                // 积分订单默认全国包邮
                merchantOrderVo.setFreightFee(storePostage);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            if (ObjectUtil.isNull(userAddress) || userAddress.getCityId() <= 0) {
                merchantOrderVo.setFreightFee(storePostage);
                merchantOrderVo.getOrderInfoList().forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                continue;
            }
            // 按模板构建MAP
            Map<Integer, MyRecord> tempMap = CollUtil.newHashMap();
            // 运费根据商品计算
//            Map<Integer, MyRecord> proMap = CollUtil.newHashMap();
            merchantOrderVo.getOrderInfoList().forEach(e -> {
                Integer tempId = e.getTempId();
                BigDecimal proPrice;
                if (e.getIsPaidMember() && userIsPaidMember) {
                    proPrice = e.getVipPrice().multiply(BigDecimal.valueOf(e.getPayNum()));
                } else {
                    proPrice = e.getPrice().multiply(BigDecimal.valueOf(e.getPayNum()));
                }
                if (tempMap.containsKey(tempId)) {
                    MyRecord record = tempMap.get(tempId);
                    record.set("totalPrice", record.getBigDecimal("totalPrice").add(proPrice));
                    record.set("totalNum", record.getInt("totalNum") + e.getPayNum());
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", record.getBigDecimal("weight").add(weight));
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", record.getBigDecimal("volume").add(volume));
                } else {
                    MyRecord record = new MyRecord();
                    record.set("totalPrice", proPrice);
                    record.set("totalNum", e.getPayNum());
                    record.set("tempId", tempId);
                    BigDecimal weight = e.getWeight().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("weight", weight);
                    BigDecimal volume = e.getVolume().multiply(BigDecimal.valueOf(e.getPayNum()));
                    record.set("volume", volume);
                    tempMap.put(tempId, record);
                }
            });

            // 指定包邮（单品运费模板）> 指定区域配送（单品运费模板）
            int districtId = userAddress.getDistrictId();
            for (Map.Entry<Integer, MyRecord> m : tempMap.entrySet()) {
                MyRecord record = m.getValue();
                Integer tempId = record.getInt("tempId");
                ShippingTemplates shippingTemplate = shippingTemplatesService.getById(tempId);
                if (ObjectUtil.isNull(shippingTemplate) || shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_ALL)) {
                    merchantOrderVo.getOrderInfoList().stream()
                            .filter(e -> e.getTempId().equals(m.getKey()))
                            .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                    continue;
                }
                if (shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_DEFINED)) {
                    ShippingTemplatesFree shippingTemplatesFree = shippingTemplatesFreeService.getByTempIdAndCityId(tempId, districtId);
                    if (ObjectUtil.isNotNull(shippingTemplatesFree)) {
                        BigDecimal totalPrice = record.getBigDecimal("totalPrice");
                        if (totalPrice.compareTo(shippingTemplatesFree.getPrice()) >= 0) {
                            merchantOrderVo.getOrderInfoList().stream()
                                    .filter(e -> e.getTempId().equals(m.getKey()))
                                    .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                            continue;
                        }
                        if (shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_NUMBER)) {
                            if (BigDecimal.valueOf(record.getInt("totalNum")).compareTo(shippingTemplatesFree.getNumber()) >= 0) {
                                merchantOrderVo.getOrderInfoList().stream()
                                        .filter(e -> e.getTempId().equals(m.getKey()))
                                        .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                                continue;
                            }
                        }
                        BigDecimal surplus = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT)
                                ? record.getBigDecimal("weight") : record.getBigDecimal("volume");
                        if (surplus.compareTo(shippingTemplatesFree.getNumber()) >= 0) {
                            merchantOrderVo.getOrderInfoList().stream()
                                    .filter(e -> e.getTempId().equals(m.getKey()))
                                    .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                            continue;
                        }
                    }
                }
                ShippingTemplatesRegion shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, districtId);
                if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                    shippingTemplatesRegion = shippingTemplatesRegionService.getByTempIdAndCityId(tempId, 0);
                }
                if (shippingTemplate.getAppoint().equals(ShippingTemplatesConstants.APPOINT_TYPE_PART)) {
                    if (ObjectUtil.isNull(shippingTemplatesRegion)) {
                        merchantOrderVo.getOrderInfoList().stream()
                                .filter(e -> e.getTempId().equals(m.getKey()))
                                .forEach(info -> info.setFreightFee(BigDecimal.ZERO));
                        continue;
                    }
                }
                BigDecimal postageFee = BigDecimal.ZERO;
                // 判断计费方式：件数、重量、体积
                switch (shippingTemplate.getType()) {
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_NUMBER: // 件数
                        // 判断件数是否超过首件
                        Integer num = record.getInt("totalNum");
                        if (num <= shippingTemplatesRegion.getFirst().intValue()) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            postageFee = shippingTemplatesRegion.getFirstPrice();
                        } else {// 超过首件的需要计算续件
                            int renewalNum = num - shippingTemplatesRegion.getFirst().intValue();
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            // 更新运费计算公式 2024-4-15
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(new BigDecimal(String.valueOf(renewalNum)).divide(shippingTemplatesRegion.getRenewal(), 0, RoundingMode.UP));
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            postageFee = shippingTemplatesRegion.getFirstPrice().add(renewalPrice);
                        }
                        List<PreOrderInfoDetailVo> detailVoList = merchantOrderVo.getOrderInfoList().stream().filter(e -> e.getTempId().equals(tempId)).collect(Collectors.toList());
                        if (detailVoList.size() == 1) {
                            detailVoList.get(0).setFreightFee(postageFee);
                        } else {
                            Integer tempNum = num;
                            for (int i = 0; i < detailVoList.size(); i++) {
                                PreOrderInfoDetailVo detail = detailVoList.get(i);
                                if (detailVoList.size() == (i + 1)) {
                                    detail.setFreightFee(postageFee);
                                    break;
                                }
                                // 更新运费计算公式 2024-4-15
                                BigDecimal multiply = postageFee.multiply(new BigDecimal(detail.getPayNum().toString())).divide(new BigDecimal(tempNum.toString()), 2, RoundingMode.UP);
                                if (postageFee.compareTo(multiply) < 0) {
                                    multiply = postageFee;
                                }
                                detail.setFreightFee(multiply);
                                postageFee = postageFee.subtract(multiply);
                                tempNum = tempNum - detail.getPayNum();
                            }
                        }
                        break;
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT: // 重量
                    case ShippingTemplatesConstants.CHARGE_MODE_TYPE_VOLIME: // 体积
                        BigDecimal surplus = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT) ? record.getBigDecimal("weight") : record.getBigDecimal("volume");
                        if (surplus.compareTo(shippingTemplatesRegion.getFirst()) <= 0) {
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice());
                            postageFee = shippingTemplatesRegion.getFirstPrice();
                        } else {// 超过首件的需要计算续件
                            BigDecimal renewalNum = surplus.subtract(shippingTemplatesRegion.getFirst());
                            // 剩余件数/续件 = 需要计算的续件费用的次数
                            // 更新运费计算公式 2024-4-15
                            BigDecimal renewalPrice = shippingTemplatesRegion.getRenewalPrice().multiply(renewalNum.divide(shippingTemplatesRegion.getRenewal(), 0, RoundingMode.UP));
                            storePostage = storePostage.add(shippingTemplatesRegion.getFirstPrice()).add(renewalPrice);
                            postageFee = shippingTemplatesRegion.getFirstPrice().add(renewalPrice);
                        }
                        List<PreOrderInfoDetailVo> infoDetailVoList = merchantOrderVo.getOrderInfoList().stream().filter(e -> e.getTempId().equals(tempId)).collect(Collectors.toList());
                        if (infoDetailVoList.size() == 1) {
                            infoDetailVoList.get(0).setFreightFee(postageFee);
                        } else {
                            BigDecimal tempSurplus = surplus;
                            for (int i = 0; i < infoDetailVoList.size(); i++) {
                                PreOrderInfoDetailVo detail = infoDetailVoList.get(i);
                                if (infoDetailVoList.size() == (i + 1)) {
                                    detail.setFreightFee(postageFee);
                                    break;
                                }
                                BigDecimal wv = shippingTemplate.getType().equals(ShippingTemplatesConstants.CHARGE_MODE_TYPE_WEIGHT) ? detail.getWeight() : detail.getVolume();
                                // 更新运费计算公式 2024-4-15
                                BigDecimal multiply = postageFee.multiply(wv.multiply(new BigDecimal(detail.getPayNum().toString()))).divide(tempSurplus, 2, RoundingMode.HALF_UP);
                                if (postageFee.compareTo(multiply) < 0) {
                                    multiply = postageFee;
                                }
                                detail.setFreightFee(multiply);
                                postageFee = postageFee.subtract(multiply);
                                tempSurplus = tempSurplus.subtract(wv.multiply(new BigDecimal(detail.getPayNum().toString())));
                            }
                        }
                        break;
                }
            }
            merchantOrderVo.setFreightFee(storePostage);
            freightFee = freightFee.add(storePostage);
        }
        orderInfoVo.setFreightFee(freightFee);
    }

    @Override
    public List<GroupBuyRecord> getGroupRecordListForNotExist(Integer groupActivityId, Integer userId,
                                                              Integer productId,
                                                              int limit){
        return dao.getGroupRecordListForNotExist(groupActivityId, userId, productId, limit);
    }


    /**
     * 计算超卖后退款
     */
    @Override
    public Boolean rollBackIsBuyed() {
        // 退款超拼后订单 查询拼团记录中已经拼团成功的记录 再根活动id 获取对应的真人成团数量 ，再根据拼团记录查询已购买用户，这个记录中拼团成功的为真实拼团成功的，拼团中的为超卖
        LambdaQueryWrapper<GroupBuyRecord> groupBuyRecordQueryWrapper = new LambdaQueryWrapper<>();
        groupBuyRecordQueryWrapper.eq(GroupBuyRecord::getRecordStatus, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode());
        List<GroupBuyRecord> groupBuyRecords = dao.selectList(groupBuyRecordQueryWrapper);
        groupBuyRecords.forEach(groupBuyRecord -> {
            // 拼团成功记录中获取到对应的活动id
            LambdaQueryWrapper<GroupBuyUser> groupBuyUserQueryWrapper = new LambdaQueryWrapper<>();
            groupBuyUserQueryWrapper.eq(GroupBuyUser::getGroupActivityId, groupBuyRecord.getGroupActivityId());
            groupBuyUserQueryWrapper.eq(GroupBuyUser::getGroupRecordId, groupBuyRecord.getGroupBuyingId());
            List<GroupBuyUser> groupBuyUserListAll = groupBuyUserService.list(groupBuyUserQueryWrapper);
            GroupBuyActivity currentABA = groupBuyActivityService.getById(groupBuyRecord.getGroupActivityId());
            if(currentABA.getBuyCount() < groupBuyUserListAll.size()){
                // 拼团失败退款
                logger.info("拼团失败退款");
                List<GroupBuyUser> buyUserWaiteRe = groupBuyUserListAll.stream().filter(groupBuyUser -> groupBuyUser.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode())).collect(Collectors.toList());
                buyUserWaiteRe.forEach(user -> {
                    // 拼团超卖退款
                    Order order = orderService.getByOrderNo(user.getOrderId());
                    order.setGroupBuyRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_FAIL.getCode());
                    // 调用批量退款任务 这里调用的商家主动退款功能
                    logger.info("拼团超卖 - 开始退款");
                    List<OrderDetail> orderDetailList = new ArrayList<>();

                    // 整单退款
                    orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
                    for (int i = 0; i < orderDetailList.size(); ) {
                        OrderDetail orderDetail = orderDetailList.get(i);
                        if (orderDetail.getApplyRefundNum() > 0) {
                            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "选择的退款商品订单详情存在退款申请，无法操作直接退款,订单详情ID=" + orderDetail.getId());
                        }
                        if (orderDetail.getPayNum() <= orderDetail.getRefundNum()) {
                            orderDetailList.remove(i);
                            continue;
                        }
                        orderDetail.setApplyRefundNum(orderDetail.getPayNum() - orderDetail.getRefundNum());
                        i++;
                    }
                    refundOrderManagerService.merchantDirectRefund(order, orderDetailList);
                    orderService.updateById(order);
                    logger.info("拼团超卖 - 退款成功:{}", order.getOrderNo());
                });
            }
        });
        return true;
    }
}

