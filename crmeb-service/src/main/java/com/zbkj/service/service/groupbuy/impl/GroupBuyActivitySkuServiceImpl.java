package com.zbkj.service.service.groupbuy.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.OrderConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.enums.GroupBuyGroupStatusEnum;
import com.zbkj.common.enums.GroupBuyRecordEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.groupbuy.GroupBuyActivity;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.product.*;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.MerchantGroupPageRequest;
import com.zbkj.common.response.ProMerchantProductResponse;
import com.zbkj.common.response.ProductAttrValueResponse;
import com.zbkj.common.response.ProductDetailResponse;
import com.zbkj.common.response.ProductMerchantResponse;
import com.zbkj.common.response.groupbuy.*;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.groupby.GroupBuyActivitySkuDao;
import com.zbkj.service.service.*;
import com.zbkj.service.service.groupbuy.GroupBuyActivityService;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import com.zbkj.service.service.groupbuy.GroupBuyRecordService;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author dazongzi
* @description GroupBuyActivitySkuServiceImpl 接口实现
* @date 2024-08-13
*/
@Service
public class GroupBuyActivitySkuServiceImpl extends ServiceImpl<GroupBuyActivitySkuDao, GroupBuyActivitySku> implements GroupBuyActivitySkuService {
    private static final Logger logger = LoggerFactory.getLogger(GroupBuyActivitySkuServiceImpl.class);
    @Resource
    private GroupBuyActivitySkuDao dao;
    @Resource
    private ProductService productService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Resource
    private GroupBuyRecordService groupBuyRecordService;
    @Resource
    private GroupBuyActivityService groupBuyActivityService;
    @Autowired
    private ProductGuaranteeService productGuaranteeService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private GroupBuyUserService groupBuyUserService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private ProductDescriptionService productDescriptionService;
    @Autowired
    private ProductAttributeService productAttributeService;
    @Autowired
    private ProductAttributeOptionService productAttributeOptionService;

    /**
     * 根据拼团活动id获取拼团商品列表
     *
     * @param groupActivityId 拼团活动id
     * @return 拼团商品列表
     */
    @Override
    public List<GroupBuyActivitySku> getListByGroupActivityId(Integer groupActivityId) {
        LambdaQueryWrapper<GroupBuyActivitySku> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(GroupBuyActivitySku::getGroupActivityId, groupActivityId);
        return dao.selectList(queryWrapper);
    }

    /**
     * 审核拼团商品状态 同步写入
     *
     * @param id sku ID
     * @param op 审核状态
     * @return 执行结果
     */
    @Override
    public Boolean reviewGroupBuySkuStatus(Integer id, Boolean op) {
        // 控制状态 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过
        // 这里的状态只有在团购审核成功的时候同步，其他状态sku表中 仅仅是 0
        Integer groupStatus;
        if (op) {
            groupStatus = GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_PASS.getCode();
        }else{
            groupStatus = GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_INIT.getCode();
        }
        LambdaUpdateWrapper<GroupBuyActivitySku> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(GroupBuyActivitySku::getGroupActivityId, id)
                .set(GroupBuyActivitySku::getActivityStatus, op)
                .set(GroupBuyActivitySku::getGroupStatus, groupStatus);
        return update(updateWrapper);
    }


    ///////////////////////////////////////////////////////// 以下为移动端方法 //////////////////////////////////////////////////////////


    /**
     * 获取拼团商品详情
     *
     * @param productId 商品id 注意这里的商品id 是 拼团活动表中的商品id
     * @return ProductDetailResponse
     */
    @Override
    public ProductDetailResponse getGroupBuyDetail(Integer productId, Integer groupActivityId, Integer groupRecordId) {
        List<GroupBuyActivitySku> currentSkuList = getGroupBuySkusListForProductIdAndGroupActivityId(productId, groupActivityId);
        if (currentSkuList.isEmpty()) {
            throw new CrmebException("拼团商品不存在");
        }
        // 主商品的价格数据拿第一个sku的数据
        GroupBuyActivitySku currentActivitySku = currentSkuList.stream().min(Comparator.comparing(GroupBuyActivitySku::getActivePrice)).orElse(null);
        // 将团购商品属性和普通商品属性合并
        Product product = productService.getById(currentActivitySku.getProductId());
        // 排除审核失败的商品
        if(product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) ||
                product.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) throw new CrmebException("当前商品为审核或者审核拒绝");

        ProductDescription sd = productDescriptionService.getByProductIdAndType(product.getId(), product.getType(), product.getMarketingType());
        if (ObjectUtil.isNotNull(sd)) {
            product.setContent(StrUtil.isBlank(sd.getDescription()) ? "" : sd.getDescription());
        }
        // 获取用户
        Integer userId = userService.getUserId();
        // 查询普通商品
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        product.setMarketingType(ProductConstants.PRODUCT_MARKETING_TYPE_PINGTUAN);
        productDetailResponse.setProductInfo(product);
        if (StrUtil.isNotBlank(product.getGuaranteeIds())) {
            productDetailResponse.setGuaranteeList(productGuaranteeService.findByIdList(CrmebUtil.stringToArray(product.getGuaranteeIds())));
        }

        // 组装拼团信息
        GroupBuyActivity groupBuyActivity = groupBuyActivityService.getById(groupActivityId);
        GroupBuyActivityResponse groupBuyActivityResponse = new GroupBuyActivityResponse();
        // 凑团时候显示
        if(1 == groupBuyActivity.getShowGroup()){
            // 获取历史已经正在开启拼团记录
            groupBuyActivityResponse.setTotalAllOrderDone(groupBuyUserService.getGroupBuyUserDoneForActivityId(groupBuyActivity.getId()).size());

            // 当前拼团活动 对应的已经发起的拼团多条列表信息
            List<GroupBuyActivityRecordOnProcessItem> processItems = new ArrayList<>();
            List<GroupBuyRecord> lastGroupBuyRecord = groupBuyRecordService.getGroupRecordListForNotExist(groupBuyActivity.getId(), userId, product.getId(), 5);
            for (GroupBuyRecord groupBuyRecord : lastGroupBuyRecord) {
                // 获取拼团成功(非购买后)的最后5个用户头像
                List<GroupBuyUser> lastGroupBuyUserList = groupBuyUserService.getLastGroupBuyUserList(groupBuyRecord.getGroupBuyingId(), 5);

                GroupBuyActivityRecordOnProcessItem item = new GroupBuyActivityRecordOnProcessItem();
                item.setOrderDoneUserImages(lastGroupBuyUserList);
                item.setRecordEndTime(groupBuyRecord.getEndTime());
                item.setNeedSomeTogetherCount(groupBuyRecord.getBuyingCountNum() - groupBuyRecord.getYetBuyingNum());
                // 根据记录查询对应拼团信息的标识
                item.setProductGroupId(groupBuyRecord.getProductGroupId());
                processItems.add(item);
            }

            groupBuyActivityResponse.setProcessItem(processItems);

        }

        BeanUtils.copyProperties(groupBuyActivity, groupBuyActivityResponse);
        groupBuyActivityResponse.setSales(groupBuyUserService.getGroupBuySalesForAfterPay(groupBuyActivity.getId()));

        // 查找当前拼团活动历史拼团记录成功的 最多返回10条
        List<GroupBuyRecord> groupBuyUserActivityDoneList = groupBuyRecordService.getGroupBuyUserActivityDoneList(groupActivityId, userId, productId,10);
        List<GroupBuyUserInfo> groupBuyUserInfos = new ArrayList<>();
        for (GroupBuyRecord groupBuyRecord : groupBuyUserActivityDoneList) {
            GroupBuyUserInfo groupBuyUserInfo = new GroupBuyUserInfo();
            groupBuyUserInfo.setGroupNickname(groupBuyRecord.getGroupLeaderNickname());
            groupBuyUserInfo.setGroupAvatar(userService.getById(groupBuyRecord.getGroupLeaderUid()).getAvatar());
            groupBuyUserInfo.setId(groupBuyRecord.getGroupLeaderUid());
            groupBuyUserInfos.add(groupBuyUserInfo);
        }
        groupBuyActivityResponse.setGroupBuyUserActivityDoneList(!groupBuyUserInfos.isEmpty() ? groupBuyUserInfos : null);

        // 当前拼团商品的 拼团价格
        groupBuyActivityResponse.setActivePrice(currentActivitySku.getActivePrice());
        // 当前用户 在当前活动 还可以购买 的数量
        if(userId > 0){
                    List<GroupBuyUser> currentGroupBuyActivityRecords =
                    groupBuyUserService.getCurrentGroupBuyActivityRecordList(currentActivitySku.getGroupActivityId(), userId, 0);
            // 拼团活动限制处理
            groupBuyActivityResponse.setBuyLimitCount(groupBuyActivity.getAllQuota());
            Integer buyedNum = 0;
            if(!currentGroupBuyActivityRecords.isEmpty()){
                buyedNum = currentGroupBuyActivityRecords.stream().map(GroupBuyUser::getPayNum).reduce(0, Integer::sum);
            }
            Integer waitePayNum = orderDetailService.getByGroupBuyActivityId(groupActivityId, productId, userId);

            // 0 = 不限购
            if(groupBuyActivity.getAllQuota() == 0){
                groupBuyActivityResponse.setBuyLimitCount(-1); // 用于前端判断
            }else if(!currentGroupBuyActivityRecords.isEmpty()){
                groupBuyActivityResponse.setBuyLimitCount(groupBuyActivity.getAllQuota() - (buyedNum + waitePayNum));
            }
            if(groupBuyActivity.getAllQuota() > 0){
                groupBuyActivityResponse.setBuyLimitCount(groupBuyActivity.getAllQuota() - (buyedNum + waitePayNum));
            }
            // 安全判断
            if(groupBuyActivity.getAllQuota() > 0 && groupBuyActivityResponse.getBuyLimitCount() <0) groupBuyActivityResponse.setBuyLimitCount(0);

        }
        // 拼团成功份数
        groupBuyActivityResponse.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(productId, groupActivityId));
        productDetailResponse.setGroupBuyActivityResponse(groupBuyActivityResponse);

        // 获取商品规格
        List<ProductAttribute> attributeList = productAttributeService.findListByProductId(product.getId());
        attributeList.forEach(attr -> {
            List<ProductAttributeOption> optionList = productAttributeOptionService.findListByAttrId(attr.getId());
            attr.setOptionList(optionList);
        });

        // 根据制式设置attr属性
        productDetailResponse.setProductAttr(attributeList);
        // 根据制式设置sku属性
        LinkedHashMap<String, ProductAttrValueResponse> skuMap = new LinkedHashMap<>();
        List<ProductAttrValue> productAttrValueList = productAttrValueService.getListByProductIdAndType(product.getId(), product.getType(), ProductConstants.PRODUCT_MARKETING_TYPE_BASE, true);
        // 判断拼团产品是否被编辑过 - skuID 对应不上的情况
        boolean matchResult = productAttrValueList.stream()
                .allMatch(item -> currentSkuList.stream().anyMatch(csku ->
                        item.getId().equals(csku.getSkuId())));
        logger.info("对比拼团对应的基础商品是否被编辑过: ${}", productAttrValueList.size());
        if(!matchResult){
            throw new CrmebException("拼团商品规格被编辑过，请重新编辑拼团活动");
        }
        for (ProductAttrValue productAttrValue : productAttrValueList) {
            ProductAttrValueResponse atr = new ProductAttrValueResponse();
            BeanUtils.copyProperties(productAttrValue, atr);
            atr.setStock(productAttrValue.getStock());

            List<GroupBuyActivitySku> currentGroupBuySkuList = currentSkuList.stream().filter(item -> item.getSkuId().equals(productAttrValue.getId())).collect(Collectors.toList());
            if(!currentGroupBuySkuList.isEmpty()){
                GroupBuyActivitySku groupBuyActivitySku = currentGroupBuySkuList.get(0);
                atr.setGroupPrice(groupBuyActivitySku.getActivePrice());
                product.setGroupPrice(groupBuyActivitySku.getActivePrice());
                atr.setGroupStock(groupBuyActivitySku.getQuota());
            }
            skuMap.put(atr.getSku(), atr);

            if(productAttrValue.getId().equals(currentActivitySku.getSkuId())){
                productDetailResponse.getProductInfo().setPrice(productAttrValue.getPrice());
            }
        }
        productDetailResponse.setProductInfo(product);
        productDetailResponse.setProductValue(skuMap);

        // 获取商户信息
        Merchant merchant = merchantService.getById(product.getMerId());
        ProductMerchantResponse merchantResponse = new ProductMerchantResponse();
        BeanUtils.copyProperties(merchant, merchantResponse);
        merchantResponse.setCollectNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        // 获取商户推荐商品
        List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 6);
        merchantResponse.setProList(merchantProductResponseList);


        productDetailResponse.setUserCollect(false);
        if (userId > 0) {
            merchantResponse.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
            // 查询用户是否收藏收藏
            productDetailResponse.setUserCollect(productRelationService.existCollectByUser(userId, product.getId()));
        }
        productDetailResponse.setMerchantInfo(merchantResponse);

        // 异步调用进行数据统计
        asyncService.productDetailStatistics(product.getId(), userId);

        return productDetailResponse;
    }

    /**
     * 根据拼团活动id 和 商品 id 获取拼团商品列表
     * @param productId       商品id
     * @param groupActivityId 拼团活动id
     * @return List<GroupBuyActivitySku>
     */
    private List<GroupBuyActivitySku> getGroupBuySkusListForProductIdAndGroupActivityId(Integer productId, Integer groupActivityId) {
        LambdaQueryWrapper<GroupBuyActivitySku> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyActivitySku::getGroupActivityId, groupActivityId)
                .eq(GroupBuyActivitySku::getProductId, productId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据商品id获取拼团商品列表
     *
     * @param paramRequest 分页参数
     * @return List<GroupBuyActivitySku>
     */
    @Override
    public List<GroupBuyActivityProductListForSale> getListByGroupProductIdByList(PageParamRequest paramRequest, Integer showgroup) {

        Integer offset = (paramRequest.getPage() - 1) * paramRequest.getLimit();
        List<GroupBuyActivityProductListForSale> listByGroupProductIdByList = dao.getListByGroupProductIdByList(offset, paramRequest.getLimit(), showgroup);
        for (GroupBuyActivityProductListForSale groupBuyActivityProductListForSale : listByGroupProductIdByList) {
            // 根据拼团主id 查询 对应拼团sku 信息和商品信息
            List<GroupBuyActivitySku> skus = getActivitySkusListByActivityIdAndProductId(groupBuyActivityProductListForSale.getGroupActivityId(), groupBuyActivityProductListForSale.getProductId());


            // 根据拼团商品价格排序规则
            GroupBuyActivitySku currentActivitySku = skus.stream().min(Comparator.comparing(GroupBuyActivitySku::getActivePrice)).orElse(null);
            ProductAttrValue productAttrValue = productAttrValueService.getById(currentActivitySku.getSkuId());
            groupBuyActivityProductListForSale.setPrice(productAttrValue.getPrice());
            groupBuyActivityProductListForSale.setActivePrice(currentActivitySku.getActivePrice());
            groupBuyActivityProductListForSale.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(groupBuyActivityProductListForSale.getProductId(),
                    groupBuyActivityProductListForSale.getGroupActivityId()));
        }
        return listByGroupProductIdByList;
    }

    public List<GroupBuyActivitySku> getActivitySkusListByActivityIdAndProductId(Integer groupActivityId, Integer productId) {
        LambdaQueryWrapper<GroupBuyActivitySku> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyActivitySku::getGroupActivityId, groupActivityId);
        lambdaQueryWrapper.eq(GroupBuyActivitySku::getProductId, productId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取拼团商品列表
     *
     * @param paramRequest 分页参数
     */
    private List<GroupBuyActivitySku> getGroupBuySkusForFrontByList(Integer merId, PageParamRequest paramRequest) {
        PageHelper.startPage(paramRequest.getPage(), paramRequest.getLimit());
        return dao.findGroupBuySkuListByFront(merId);
    }



    /**
     * 根据 attrId 和 活动id 获取拼团商品
     *
     * @param activityId 拼团活动id
     * @param attrId     sku id
     * @return 拼团活动数据
     */
    @Override
    public GroupBuyActivitySku getByAttrId(Integer activityId, Integer attrId) {
        LambdaQueryWrapper<GroupBuyActivitySku> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyActivitySku::getGroupActivityId, activityId)
                .eq(GroupBuyActivitySku::getSkuId, attrId);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 减少拼团商品库存
     *
     * @param groupBuyActivityId 拼团活动id
     * @param attrValueId        skuId
     * @param payNum             购买数量
     */
    @Override
    public void subSKUStock(Integer groupBuyActivityId, Integer attrValueId, Integer payNum) {
        LambdaQueryWrapper<GroupBuyActivitySku> currentSku = Wrappers.lambdaQuery();
        currentSku.eq(GroupBuyActivitySku::getGroupActivityId, groupBuyActivityId)
                .eq(GroupBuyActivitySku::getSkuId, attrValueId);
        GroupBuyActivitySku currentGroupBuyActivitySku = dao.selectOne(currentSku);

        Integer quota = currentGroupBuyActivitySku.getQuota() - payNum;
        LambdaUpdateWrapper<GroupBuyActivitySku> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(GroupBuyActivitySku::getQuota, quota)
                .eq(GroupBuyActivitySku::getGroupActivityId, groupBuyActivityId)
                .eq(GroupBuyActivitySku::getSkuId, attrValueId);
        update(updateWrapper);
    }

    /**
     * 回滚库存
     *
     * @param groupBuyActivityId 拼团活动id
     * @param skuId        skuId
     * @param payNum             购买数量
     */
    @Override
    public void rollBackSKUStock(Order order,Integer groupBuyActivityId, Integer skuId, Integer payNum) {
        LambdaQueryWrapper<GroupBuyActivitySku> currentSku = Wrappers.lambdaQuery();
        currentSku.eq(GroupBuyActivitySku::getGroupActivityId, groupBuyActivityId)
                .eq(GroupBuyActivitySku::getSkuId, skuId);
        GroupBuyActivitySku currentGroupBuyActivitySku = dao.selectOne(currentSku);

        Integer quota = currentGroupBuyActivitySku.getQuota() + payNum;
        logger.info("拼团退款 操作 当前sku = {}", quota);
        LambdaUpdateWrapper<GroupBuyActivitySku> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(GroupBuyActivitySku::getQuota, quota)
                .eq(GroupBuyActivitySku::getGroupActivityId, groupBuyActivityId)
                .eq(GroupBuyActivitySku::getSkuId, skuId);

        GroupBuyUser currentGroupBuyUser = groupBuyUserService.getByOrderNo(order.getOrderNo());
        currentGroupBuyUser.setRecordStatus(OrderConstants.ORDER_REFUND_STATUS_ALL);
        groupBuyUserService.updateById(currentGroupBuyUser);
        logger.info("拼团退款 操作 当前用户 = {}", JSONObject.toJSONString(currentGroupBuyUser));
        update(updateWrapper);
    }

    /**
     * 拼团订单中查看拼团活动
     * 1. 根据订单号查询用户拼团订单中的购买商品的记录 查询到当前拼团订单的商品信息
     * 2. 根据当前拼团记录id 查询有多少人正在购买
     * @param orderNo 订单编号
     * @return 查询到的拼团活动信息
     */
    @Override
    public GroupBuyActivityFrontOrderViewResponse getGroupBuyActivityFrontForShare(String orderNo){
        GroupBuyActivityFrontOrderViewResponse response = new GroupBuyActivityFrontOrderViewResponse();
        response.setOrderNo(orderNo);
        Order order = null;
        if(orderNo.startsWith(OrderConstants.ORDER_PREFIX_PLATFORM)){
            order = orderService.getMerchantOrderNoByPtOrderNoForGroupBuy(orderNo);
        }else{
            order = orderService.getByOrderNo(orderNo);
        }

        GroupBuyUser groupBuyUser = groupBuyUserService.getByOrderNo(order.getOrderNo());
        if(ObjectUtil.isNull(groupBuyUser)){
            throw new CrmebException("未找到当前拼团订单:"+order.getOrderNo());
        }
        checkGroupActiviyStatusOn(groupBuyUser.getGroupActivityId());
        GroupBuyRecord currentRecord = groupBuyRecordService.getById(groupBuyUser.getGroupRecordId());

        // 拼团商品详情
        ProductDetailResponse groupBuyDetail = getGroupBuyDetail(currentRecord.getProductGroupId(), currentRecord.getGroupActivityId(), currentRecord.getGroupBuyingId());

        // 分享所需参数
        GroupBuyActivityRecordForFrontShareUse groupBuyActivityRecord = new GroupBuyActivityRecordForFrontShareUse();
        BeanUtils.copyProperties(currentRecord, groupBuyActivityRecord);
        groupBuyActivityRecord.setGroupActivityId(groupBuyUser.getGroupActivityId());
        groupBuyActivityRecord.setProductId(groupBuyUser.getProductGroupId());
        groupBuyActivityRecord.setGroupRecordId(groupBuyUser.getGroupRecordId());
        groupBuyActivityRecord.setFictiStatus(currentRecord.getFictiStatus());
        groupBuyActivityRecord.setGroupLeaderNickname(currentRecord.getGroupLeaderNickname());
        groupBuyActivityRecord.setGroupLeaderAvatar(groupBuyUser.getGroupAvatar());
        GroupBuyActivitySku currentGroupBuyActivitySku =
                getByAttrId(currentRecord.getGroupActivityId(), currentRecord.getSkuid());
        groupBuyActivityRecord.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(groupBuyUser.getProductGroupId(),
                groupBuyUser.getGroupActivityId()));
        if(ObjectUtil.isNotNull(currentGroupBuyActivitySku)){
            groupBuyActivityRecord.setActivePrice(currentGroupBuyActivitySku.getActivePrice());
        }

        // 这里获取购买过的拼团信息对应的商品sku属性
        ProductAttrValue currentAttrVale = productAttrValueService.getById(currentRecord.getSkuid());
        groupBuyDetail.getProductInfo().setPrice(currentAttrVale.getPrice());

        // 当前拼团还差多少人拼团成功 这里根据当前拼团的最大人数给一个定长的集合，以30 为界 <= 则集合长度以实际拼团人数为准，> 30 的直接固定30
        // 定长的集合 查询当前购买的人数来填充数据，以名称和对象以及团长身份为item 前端有数据的直接显示头像，没数据的显示问号 1个问号=差几个人
        Integer limit = 30;
        if(currentRecord.getBuyingCountNum() <= limit){
            limit = currentRecord.getBuyingCountNum();
        }
        List<GroupBuyUser> groupBuyUserDoneForRecordId = Collections.emptyList();
        // 拼团中的查询
        if(currentRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode())){
            groupBuyUserDoneForRecordId = groupBuyUserService.getGroupBuyUserAfterPayForRecordId(groupBuyUser.getGroupRecordId(), limit);
            if(groupBuyUserDoneForRecordId.size() < limit){
                for(int i = groupBuyUserDoneForRecordId.size(); i < limit; i++){
                    GroupBuyUser groupBuyUserTemp = new GroupBuyUser();
                    groupBuyUserTemp.setGroupNickname("？");
                    groupBuyUserTemp.setGroupAvatar("需要拼团补位");
                    groupBuyUserDoneForRecordId.add(groupBuyUserTemp);
                }
            }
        }
        // 假团拼团成功了 查看拼团详情时还需要将补位的人数虚拟出来
        if(currentRecord.getFictiStatus().equals(1) && currentRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode())){
            groupBuyUserDoneForRecordId = groupBuyUserService.getGroupBuyUserAfterOderDoneForRecordId(groupBuyUser.getGroupRecordId(), limit);
            if(groupBuyUserDoneForRecordId.size() < limit){
                limit = limit - groupBuyUserDoneForRecordId.size();
                for (int i = 0; i < limit; i++) {
                    GroupBuyUser groupBuyUserTemp = new GroupBuyUser();
                    groupBuyUserTemp.setGroupNickname("？");
                    groupBuyUserTemp.setGroupAvatar("https");
                    groupBuyUserDoneForRecordId.add(groupBuyUserTemp);
                }
            }

        }

        // 真团拼团成功的查询 虚拟成团状态0.未开启，1开启
        if(currentRecord.getFictiStatus().equals(0) && currentRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode())){
            groupBuyUserDoneForRecordId = groupBuyUserService.getGroupBuyUserAfterOderDoneForRecordId(groupBuyUser.getGroupRecordId(), limit);

        }else if (currentRecord.getFictiStatus().equals(0) && currentRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_FAIL.getCode())){
            groupBuyUserDoneForRecordId = groupBuyUserService.getListByGroupRecordId(groupBuyUser.getGroupRecordId());
        }


        response.setGroupBuyUserList(groupBuyUserDoneForRecordId);

        Integer userId = userService.getUserId();

        // 查找当前拼团活动历史拼团记录成功的 最多返回10条
        List<GroupBuyRecord> groupBuyUserActivityDoneList = groupBuyRecordService.getGroupBuyUserActivityDoneList(currentRecord.getGroupActivityId(), userId, currentRecord.getProductGroupId(), 10);
        List<GroupBuyUserInfo> groupBuyUserInfos = new ArrayList<>();
        for (GroupBuyRecord groupBuyRecord : groupBuyUserActivityDoneList) {
            GroupBuyUserInfo groupBuyUserInfo = new GroupBuyUserInfo();
            groupBuyUserInfo.setGroupNickname(groupBuyRecord.getGroupLeaderNickname());
            groupBuyUserInfo.setGroupAvatar(userService.getById(groupBuyRecord.getGroupLeaderUid()).getAvatar());
            groupBuyUserInfo.setId(groupBuyRecord.getGroupLeaderUid());
            groupBuyUserInfos.add(groupBuyUserInfo);
        }
        response.setGroupBuyUserActivityDoneList(!groupBuyUserInfos.isEmpty() ? groupBuyUserInfos : null);



        response.setProductDetailResponse(groupBuyDetail);
        response.setRecordForShare(groupBuyActivityRecord);
        return response;
    }


    @Override
    public GroupBuyActivityFrontOrderViewResponse getGroupBuyActivityFrontForReadShare(Integer groupActivityId, Integer productId, Integer recordId){

        GroupBuyActivityFrontOrderViewResponse response = new GroupBuyActivityFrontOrderViewResponse();

        GroupBuyRecord currentRecord = groupBuyRecordService.getById(recordId);

        checkGroupActiviyStatusOn(currentRecord.getGroupActivityId());
        // 拼团商品详情
        ProductDetailResponse groupBuyDetail = getGroupBuyDetail(currentRecord.getProductGroupId(), currentRecord.getGroupActivityId(), currentRecord.getGroupBuyingId());

        // 分享所需参数
        GroupBuyActivityRecordForFrontShareUse groupBuyActivityRecord = new GroupBuyActivityRecordForFrontShareUse();
        GroupBuyRecord currentGroupBuyRecord = groupBuyRecordService.getById(recordId);
        BeanUtils.copyProperties(currentGroupBuyRecord, groupBuyActivityRecord);
        // 判断扫码后的拼团是否过期
        if(currentRecord.getEndTime().getTime() <= System.currentTimeMillis()){
            groupBuyActivityRecord.setRecordStatus(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_FAIL.getCode());
        }
        groupBuyActivityRecord.setGroupActivityId(groupActivityId);
        groupBuyActivityRecord.setProductId(productId);
        groupBuyActivityRecord.setGroupRecordId(recordId);
        GroupBuyActivitySku currentGroupBuyActivitySku =
                getByAttrId(currentGroupBuyRecord.getGroupActivityId(), currentGroupBuyRecord.getSkuid());
        if(ObjectUtil.isNotNull(currentGroupBuyActivitySku)){
            groupBuyActivityRecord.setActivePrice(currentGroupBuyActivitySku.getActivePrice());
        }
        // 查询当前用户是否可购买标识 0=可购买(没买过) 1=不可购买(买过)
        Integer currentUserId = userService.getUserId();
        if(currentUserId > 0){
            List<GroupBuyUser> groupBuyUsers = groupBuyUserService.getCurrentGroupBuyActivityRecordListByUserId(currentUserId, groupActivityId, recordId, GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode());
            groupBuyActivityRecord.setCanBuyFlag(groupBuyUsers.isEmpty() ? 0 : 1);
            if(!groupBuyUsers.isEmpty()){
                response.setOrderNo(groupBuyUsers.get(0).getOrderId());

            }
        }
        groupBuyActivityRecord.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(productId, groupActivityId));



        // 当前拼团还差多少人拼团成功 这里根据当前拼团的最大人数给一个定长的集合，以30 为界 <= 则集合长度以实际拼团人数为准，> 30 的直接固定30
        // 定长的集合 查询当前购买的人数来填充数据，以名称和对象以及团长身份为item 前端有数据的直接显示头像，没数据的显示问号 1个问号=差几个人

        Integer limit = 30;
        if(currentRecord.getBuyingCountNum() <= limit){
            limit = currentRecord.getBuyingCountNum();
        }
        List<GroupBuyUser> groupBuyUserDoneForRecordId = Collections.emptyList();
        // 拼团中的查询
        if(currentRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_INIT.getCode())){
            groupBuyUserDoneForRecordId = groupBuyUserService.getGroupBuyUserAfterPayForRecordId(recordId, limit);
            if(groupBuyUserDoneForRecordId.size() < limit){
                for(int i = groupBuyUserDoneForRecordId.size(); i < limit; i++){
                    GroupBuyUser groupBuyUserTemp = new GroupBuyUser();
                    groupBuyUserTemp.setGroupNickname("？");
                    groupBuyUserTemp.setGroupAvatar("需要拼团补位");
                    groupBuyUserDoneForRecordId.add(groupBuyUserTemp);
                }
            }
        }

        // 拼团成功的查询
        if(currentRecord.getRecordStatus().equals(GroupBuyRecordEnum.GROUP_BUY_RECORD_ENUM_STATUS_SUCCESS.getCode())){
            groupBuyUserDoneForRecordId = groupBuyUserService.getGroupBuyUserAfterOderDoneForRecordId(recordId, limit);

        }

        response.setGroupBuyUserList(groupBuyUserDoneForRecordId);

        response.setProductDetailResponse(groupBuyDetail);
        response.setRecordForShare(groupBuyActivityRecord);
        return response;
    }

    /**
     * 根据 skuIds 删除对应的拼团商品属性
     * @param finalSkuIdList 拼团商品属性id集合
     */
    @Override
   public void deleteSkusIdsForProductTurnOff(List<Integer> finalSkuIdList){
        LambdaQueryWrapper<GroupBuyActivitySku> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(GroupBuyActivitySku::getSkuId, finalSkuIdList);
        List<GroupBuyActivitySku> skus = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(skus)) {
            return;
        }
       List<Integer> ids = skus.stream().map(GroupBuyActivitySku::getId).collect(Collectors.toList());
       dao.deleteBatchIds(ids);
       logger.info("有商品操作下架，删除关联拼团商品属性: {}", JSONObject.toJSONString(finalSkuIdList));
   }

    /**
     * 根据拼团活动id获取移动端拼团商品sku列表
     * @param groupActivityId 拼团活动id
     * @return 拼团商品列表
     */
    @Override
    public GroupBuyActivitySku getFrontMinActivePriceByGroupActivityId(Integer groupActivityId) {
        return dao.getFrontMinActivePriceByGroupActivityId(groupActivityId);
    }

    /**
     * 商户拼团商品分页列表
     */
    @Override
    public PageInfo<GroupBuyActivityProductListForSale> findMerchantProductPageByFront(MerchantGroupPageRequest request) {
        Page<GroupBuyActivitySku> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<GroupBuyActivityProductListForSale> productList = dao.findGroupMerchantProductListByFront(request.getMerId());
        for (GroupBuyActivityProductListForSale groupBuyActivityProductListForSale : productList) {
            // 根据拼团主id 查询 对应拼团sku 信息和商品信息
            List<GroupBuyActivitySku> skus = getActivitySkusListByActivityIdAndProductId(groupBuyActivityProductListForSale.getGroupActivityId(), groupBuyActivityProductListForSale.getProductId());
            // 根据拼团商品价格排序规则
            GroupBuyActivitySku currentActivitySku = skus.stream().min(Comparator.comparing(GroupBuyActivitySku::getActivePrice)).orElse(null);
            ProductAttrValue productAttrValue = productAttrValueService.getById(currentActivitySku.getSkuId());
            groupBuyActivityProductListForSale.setPrice(productAttrValue.getPrice());
            groupBuyActivityProductListForSale.setActivePrice(currentActivitySku.getActivePrice());
            groupBuyActivityProductListForSale.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(groupBuyActivityProductListForSale.getProductId(),
                    groupBuyActivityProductListForSale.getGroupActivityId()));
        }
        return CommonPage.copyPageInfo(page, productList);
    }


    ////////////////////////////////////////

    private void checkGroupActiviyStatusOn(Integer activityId) {
        GroupBuyActivity currentActivity = groupBuyActivityService.getById(activityId);
        if(currentActivity.getIsDel().equals(1)
                || currentActivity.getActivityStatus().equals(0)
                || currentActivity.getGroupStatus().equals(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_AUDIT.getCode())
                || currentActivity.getGroupStatus().equals(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_REFUSE.getCode())){
            throw new CrmebException("当前活动已失效");
        }
    }
}

