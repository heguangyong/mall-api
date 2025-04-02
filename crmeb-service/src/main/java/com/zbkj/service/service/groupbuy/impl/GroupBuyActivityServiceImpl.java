package com.zbkj.service.service.groupbuy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.enums.GroupBuyGroupStatusEnum;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.groupbuy.GroupBuyActivity;
import com.zbkj.common.model.groupbuy.GroupBuyActivitySku;
import com.zbkj.common.model.groupbuy.GroupBuyUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivityRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivitySearchRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivitySkuRequest;
import com.zbkj.common.request.groupbuy.PatGroupBuyActivitySearchRequest;
import com.zbkj.common.response.AttrValueResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.groupbuy.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.groupby.GroupBuyActivityDao;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.ProductAttrValueService;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.groupbuy.GroupBuyActivityService;
import com.zbkj.service.service.groupbuy.GroupBuyActivitySkuService;
import com.zbkj.service.service.groupbuy.GroupBuyUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dazongzi
 * @description GroupBuyActivityServiceImpl 接口实现
 * @date 2024-08-13
 */
@Service
public class GroupBuyActivityServiceImpl extends ServiceImpl<GroupBuyActivityDao, GroupBuyActivity> implements GroupBuyActivityService {

    @Resource
    private GroupBuyActivityDao dao;

    @Resource
    private GroupBuyActivitySkuService groupBuyActivitySkuService;

    @Resource
    private ProductService productService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Resource
    private MerchantService merchantService;

    @Resource
    private GroupBuyUserService groupBuyUserService;

    /**
     * 此方法商户和平台公用，用平台的参数包含了商户的参数，在controller 层面做了参数区分
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<GroupBuyActivity>
     * @author dazongzi
     * @since 2024-08-13
     */
    @Override
    public PageInfo<GroupBuyActivityResponse> getList(PatGroupBuyActivitySearchRequest request, PageParamRequest pageParamRequest) {

        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        //带 GroupBuyActivity 类的多条件查询
        LambdaQueryWrapper<GroupBuyActivity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(GroupBuyActivity::getIsDel, 0);
        // 根据权限判断查询自己所属商户的活动拼团列表
        if (admin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()) || admin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            lambdaQueryWrapper.eq(GroupBuyActivity::getMerId, admin.getMerId());
        }
        if (ObjectUtil.isNotEmpty(request.getGroupName().trim())) {
            lambdaQueryWrapper.like(GroupBuyActivity::getGroupName, URLUtil.decode(request.getGroupName().trim()));
        }
        // 活动进程 活动进程  0=未开始 1=进行中 2=已结束
        if (ObjectUtil.isNotEmpty(request.getGroupProcess())) {
            switch (request.getGroupProcess()) {
                case 0:
                    lambdaQueryWrapper.ge(GroupBuyActivity::getStartTime, CrmebDateUtil.nowDateTime());
                    break;
                case 1:
                    lambdaQueryWrapper.le(GroupBuyActivity::getStartTime, CrmebDateUtil.nowDateTime())
                            .gt(GroupBuyActivity::getEndTime, CrmebDateUtil.nowDateTime());
                    break;
                case 2:
                    lambdaQueryWrapper.lt(GroupBuyActivity::getEndTime, CrmebDateUtil.nowDateTime());
                    break;
            }
        }

        // 活动日期
        if (ObjectUtil.isNotEmpty(request.getStartTime())) {
            Date date = CrmebDateUtil.strToDate(request.getStartTime(), DateConstants.DATE_FORMAT_DATE);
            String startTime = CrmebDateUtil.dateToStr(date, DateConstants.DATE_FORMAT_START);
            String endTime = CrmebDateUtil.dateToStr(date, DateConstants.DATE_FORMAT_END);
            lambdaQueryWrapper.le(GroupBuyActivity::getStartTime, startTime)
                    .ge(GroupBuyActivity::getEndTime, endTime);
        }

        // 活动状态 开启或者关闭
        if (ObjectUtil.isNotEmpty(request.getGroupStatus())) {
            lambdaQueryWrapper.eq(GroupBuyActivity::getGroupStatus, request.getGroupStatus());
        }

        // 给商户查询做准备
        // 商户分类 根据商户分类找到对应商户再触发商户查询
        if (ObjectUtil.isNotEmpty(request.getCategoryId())) {
            List<Merchant> merchantList = merchantService.getMerchantListByType(request.getCategoryId());
            List<Integer> merIds = new ArrayList<>();
            if (merchantList.isEmpty()) {
                merIds.add(0);
            } else {
                merIds = merchantList.stream().map(Merchant::getId).collect(Collectors.toList());
            }
            lambdaQueryWrapper.in(GroupBuyActivity::getMerId, merIds);
        }

        // 指定商户查询
        if (StrUtil.isNotBlank(request.getMerName())) {
            lambdaQueryWrapper.like(GroupBuyActivity::getMerName, URLUtil.decode(request.getMerName()));
        }

        // 活动状态 关闭/开启
        if (ObjectUtil.isNotEmpty(request.getActivityStatus())) {
            lambdaQueryWrapper.eq(GroupBuyActivity::getActivityStatus, request.getActivityStatus());
        }

        lambdaQueryWrapper.orderByDesc(GroupBuyActivity::getCreateTime);
        Page<GroupBuyActivity> activityGroupPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<GroupBuyActivity> groupBuyActivities = dao.selectList(lambdaQueryWrapper);

        List<GroupBuyActivityResponse> groupBuyActivityResponses = new ArrayList<>();
        groupBuyActivities.stream().map(groupBuyActivity -> {
            GroupBuyActivityResponse groupBuyActivityResponse = new GroupBuyActivityResponse();
            BeanUtils.copyProperties(groupBuyActivity, groupBuyActivityResponse);
//            groupBuyActivityResponse.setGroupBuyActivitySkuResponses(groupBuyActivitySkuService.getListByGroupActivityId(groupBuyActivity.getId()));
            groupBuyActivityResponses.add(groupBuyActivityResponse);
            return null;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(activityGroupPage, groupBuyActivityResponses);
    }

    /**
     * 获取拼团头部 数据
     *
     * @return 对应状态的数量
     */
    @Override
    public List<GroupBuyActivityListHeaderCount> getListHeaderCount(GroupBuyActivitySearchRequest request, SystemAdmin systemAdmin) {
        // 0=初始化 1=已拒绝 2=已撤销 3=待审核 4=已通过
        List<GroupBuyActivityListHeaderCount> headerCountList = new ArrayList<>();
        if (systemAdmin.getMerId() == 0) {
            GroupBuyActivityListHeaderCount headerCount1 = new GroupBuyActivityListHeaderCount(1, 0);
            GroupBuyActivityListHeaderCount headerCount3 = new GroupBuyActivityListHeaderCount(3, 0);
            GroupBuyActivityListHeaderCount headerCount4 = new GroupBuyActivityListHeaderCount(4, 0);
            headerCountList.add(headerCount1);
            headerCountList.add(headerCount3);
            headerCountList.add(headerCount4);
        } else {
            GroupBuyActivityListHeaderCount headerCount1 = new GroupBuyActivityListHeaderCount(1, 0);
            GroupBuyActivityListHeaderCount headerCount2 = new GroupBuyActivityListHeaderCount(2, 0);
            GroupBuyActivityListHeaderCount headerCount3 = new GroupBuyActivityListHeaderCount(3, 0);
            GroupBuyActivityListHeaderCount headerCount4 = new GroupBuyActivityListHeaderCount(4, 0);
            headerCountList.add(headerCount1);
            headerCountList.add(headerCount2);
            headerCountList.add(headerCount3);
            headerCountList.add(headerCount4);
        }
        LambdaQueryWrapper<GroupBuyActivity> queryWrapper = Wrappers.lambdaQuery();
        for (GroupBuyActivityListHeaderCount headerCount : headerCountList) {
            queryWrapper.clear();
            queryWrapper.eq(GroupBuyActivity::getIsDel, 0);
            // 指定商户查询
            if (StrUtil.isNotBlank(request.getMerName())) {
                queryWrapper.like(GroupBuyActivity::getMerName, URLUtil.decode(request.getMerName()));
            }
            if (StrUtil.isNotBlank(request.getGroupName().trim())) {
                String groupNameDecode = URLUtil.decode(request.getGroupName().trim());
                queryWrapper.like(GroupBuyActivity::getGroupName, groupNameDecode);
            }
            // 活动进程 活动进程  0=未开始 1=进行中 2=已结束
            if (ObjectUtil.isNotEmpty(request.getGroupProcess())) {
                switch (request.getGroupProcess()) {
                    case 0:
                        queryWrapper.ge(GroupBuyActivity::getStartTime, CrmebDateUtil.nowDateTime());
                        break;
                    case 1:
                        queryWrapper.le(GroupBuyActivity::getStartTime, CrmebDateUtil.nowDateTime())
                                .gt(GroupBuyActivity::getEndTime, CrmebDateUtil.nowDateTime());
                        break;
                    case 2:
                        queryWrapper.lt(GroupBuyActivity::getEndTime, CrmebDateUtil.nowDateTime());
                        break;
                }
            }
            // 活动日期
            if (ObjectUtil.isNotEmpty(request.getStartTime())) {
                Date date = CrmebDateUtil.strToDate(request.getStartTime(), DateConstants.DATE_FORMAT_DATE);
                String startTime = CrmebDateUtil.dateToStr(date, DateConstants.DATE_FORMAT_START);
                String endTime = CrmebDateUtil.dateToStr(date, DateConstants.DATE_FORMAT_END);
                queryWrapper.le(GroupBuyActivity::getStartTime, startTime)
                        .ge(GroupBuyActivity::getEndTime, endTime);
            }
            // 商户分类 根据商户分类找到对应商户再触发商户查询
            if (ObjectUtil.isNotEmpty(request.getCategoryId())) {
                List<Merchant> merchantList = merchantService.getMerchantListByType(request.getCategoryId());
                List<Integer> merIdList = new ArrayList<>();
                if (merchantList.isEmpty()) {
                    merIdList.add(0);
                } else {
                    merIdList = merchantList.stream().map(Merchant::getId).collect(Collectors.toList());
                }
                queryWrapper.in(GroupBuyActivity::getMerId, merIdList);
            }
            // 活动状态 关闭/开启
            if (ObjectUtil.isNotEmpty(request.getActivityStatus())) {
                queryWrapper.eq(GroupBuyActivity::getActivityStatus, request.getActivityStatus());
            }
            // 根据权限判断商户还是平台
            if (systemAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue()) ||
                    systemAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
                queryWrapper.eq(GroupBuyActivity::getMerId, systemAdmin.getMerId());
            }
            // 控制状态
            queryWrapper.eq(GroupBuyActivity::getGroupStatus, headerCount.getGroupStatus());
            headerCount.setCount(dao.selectCount(queryWrapper));
        }

        return headerCountList;
    }

    /**
     * 新增拼团
     *
     * @param request 拼团原始对象
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean addGroupBuyActivity(GroupBuyActivityRequest request) {
        // 获取拼团基础活动数据
        GroupBuyActivity groupBuyActivity = new GroupBuyActivity();
        BeanUtils.copyProperties(request, groupBuyActivity);
        // 获取拼团商品数据
        List<GroupBuyActivitySku> groupBuySkuList = new ArrayList<>();
        List<GroupBuyActivitySkuRequest> groupBuySkuRequestList = request.getGroupBuySkuRequest();
        for (GroupBuyActivitySkuRequest groupSkuRequest : groupBuySkuRequestList) {
            GroupBuyActivitySku groupSku = new GroupBuyActivitySku();
            BeanUtils.copyProperties(groupSkuRequest, groupSku);
            groupSku.setQuota(groupSkuRequest.getQuotaShow());
            groupBuySkuList.add(groupSku);
        }
        // 安全校验 拼团商品售卖的库存不得大于原始商品库存
        validGroupProduct(groupBuySkuList);

        // 设置当前拼团活动状态 有初始化状态 但是这里默认指定为 待审核
        groupBuyActivity.setGroupStatus(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_AUDIT.getCode());
        // 设置当前登录的商户
        SystemAdmin currentMerchantAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant currentMerchant = merchantService.getByIdException(currentMerchantAdmin.getMerId());
        groupBuyActivity.setMerId(currentMerchantAdmin.getMerId());
        groupBuyActivity.setMerName(currentMerchant.getName());

        // 保存到拼团活动表和对应sku
        dao.insert(groupBuyActivity);
        groupBuySkuList = groupBuySkuList.stream().map(sku -> sku.setGroupActivityId(groupBuyActivity.getId())).collect(Collectors.toList());
        groupBuyActivitySkuService.saveBatch(groupBuySkuList);
        return Boolean.TRUE;
    }


    /**
     * 修改拼团
     *
     * @param request 拼团待修改对象
     * @return Boolean
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Boolean updateGroupBuyActivity(GroupBuyActivityRequest request) {
        // 获取拼团基础活动数据
        GroupBuyActivity groupBuyActivity = new GroupBuyActivity();
        BeanUtils.copyProperties(request, groupBuyActivity);
        groupBuyActivity.setGroupName(request.getGroupName());

        // 获取拼团商品数据
        List<GroupBuyActivitySku> groupBuySkuList = new ArrayList<>();
        List<GroupBuyActivitySkuRequest> groupBuySkuRequestList = request.getGroupBuySkuRequest();
        for (GroupBuyActivitySkuRequest groupSkuRequest : groupBuySkuRequestList) {
            GroupBuyActivitySku groupSku = new GroupBuyActivitySku();
            BeanUtils.copyProperties(groupSkuRequest, groupSku);
            groupSku.setQuota(groupSkuRequest.getQuotaShow());
            groupBuySkuList.add(groupSku);
        }

        // 安全校验 拼团商品售卖的库存不得大于原始商品库存
        validGroupProduct(groupBuySkuList);

        // 设置当前拼团活动状态 修改后默认为待审核状态
        groupBuyActivity.setGroupStatus(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_AUDIT.getCode());
        // 保存到拼团活动表和对应sku
        dao.updateById(groupBuyActivity);
        // 删除历史SKU 数据后添加新的数据 不更新
        List<GroupBuyActivitySku> listByGroupActivityId = groupBuyActivitySkuService.getListByGroupActivityId(request.getId());
        if (ObjectUtil.isNotEmpty(listByGroupActivityId)) {
            List<Integer> skuIds = listByGroupActivityId.stream().map(GroupBuyActivitySku::getId).collect(Collectors.toList());
            groupBuyActivitySkuService.removeByIds(skuIds);
        }
        groupBuyActivitySkuService.saveBatch(groupBuySkuList);
        return Boolean.TRUE;
    }

    /**
     * 获取拼团详情
     *
     * @param id 拼团活动id
     * @return 拼团活动详情
     */
    @Override
    public GroupBuyActivityResponse getGroupBuyActivity(Integer id) {
        GroupBuyActivity groupBuyActivity = dao.selectById(id);
        if (ObjectUtil.isEmpty(groupBuyActivity)) {
            return null;
        }
        List<GroupBuyActivityProductResponse> groupBuyActivityProductResponseList = new ArrayList<>();
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();

        // set 拼团活动数据
        GroupBuyActivityResponse groupBuyActivityResponse = new GroupBuyActivityResponse();
        BeanUtils.copyProperties(groupBuyActivity, groupBuyActivityResponse);

        // 获取当前拼团下所有的sku列表
        List<GroupBuyActivitySku> groupBuyActivitySkuList = groupBuyActivitySkuService.getListByGroupActivityId(id);

        Map<Integer, List<GroupBuyActivitySku>> skuListGroupByProductIdMap =
                groupBuyActivitySkuList.stream().collect(Collectors.groupingBy(GroupBuyActivitySku::getProductId));

        for (Map.Entry<Integer, List<GroupBuyActivitySku>> productIdEntry : skuListGroupByProductIdMap.entrySet()) {
            Integer productId = productIdEntry.getKey();
            List<GroupBuyActivitySku> groupBuyActivitySkuListByProductId = productIdEntry.getValue();
            ProductInfoResponse productInfoResponse = productService.getInfo(productId, systemAdmin);
            if (productInfoResponse.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_WAIT) ||
                    productInfoResponse.getAuditStatus().equals(ProductConstants.AUDIT_STATUS_FAIL)) continue;
            List<GroupBuyActivitySkuResponse> groupBuyActivitySkuResponseList = new ArrayList<>();

            for (GroupBuyActivitySku groupBuyActivitySku : groupBuyActivitySkuListByProductId) {

                // 确定是否有相同的商品id
                GroupBuyActivitySkuResponse skuResponse = new GroupBuyActivitySkuResponse();

                skuResponse.setId(groupBuyActivitySku.getId());
                skuResponse.setGroupActivityId(groupBuyActivitySku.getGroupActivityId());
                skuResponse.setProductId(productId);
                skuResponse.setActivePrice(groupBuyActivitySku.getActivePrice());
                skuResponse.setQuotaShow(groupBuyActivitySku.getQuotaShow());
                skuResponse.setQuota(groupBuyActivitySku.getQuota());
                skuResponse.setProductName(productInfoResponse.getName());
                skuResponse.setSkuId(groupBuyActivitySku.getSkuId());

                // 找到商品匹配的SKU 数据
                List<AttrValueResponse> seamSkuList =
                        productInfoResponse.getAttrValueList().stream().filter(attrValue -> attrValue.getId().equals(groupBuyActivitySku.getSkuId())).collect(Collectors.toList());
                if (!seamSkuList.isEmpty()) {
                    skuResponse.setAttrValue(seamSkuList);
                    groupBuyActivitySkuResponseList.add(skuResponse);
                }
            }

            GroupBuyActivityProductResponse groupBuyActivityProductResponse = new GroupBuyActivityProductResponse();
            groupBuyActivityProductResponse.setProductId(productId);
            groupBuyActivityProductResponse.setProductName(productInfoResponse.getName());
            groupBuyActivityProductResponse.setImage(productInfoResponse.getImage());
            groupBuyActivityProductResponse.setGroupBuyActivitySkuResponses(groupBuyActivitySkuResponseList);
            groupBuyActivityProductResponseList.add(groupBuyActivityProductResponse);
        }

        groupBuyActivityResponse.setGroupBuyActivityProductResponseList(groupBuyActivityProductResponseList);

        return groupBuyActivityResponse;
    }

    /**
     * 拼团活动详情 针对移动端
     *
     * @param id 拼团活动id
     * @return 拼团活动详情
     */
    @Override
    public GroupBuyActivityResponse getGroupBuyActivityForFront(Integer id) {
        GroupBuyActivity groupBuyActivity = dao.selectById(id);
        if (ObjectUtil.isEmpty(groupBuyActivity)) {
            return null;
        }
        List<GroupBuyActivityProductResponse> groupBuyActivityProductResponseList = new ArrayList<>();

        // set 拼团活动数据
        GroupBuyActivityResponse groupBuyActivityResponse = new GroupBuyActivityResponse();
        BeanUtils.copyProperties(groupBuyActivity, groupBuyActivityResponse);

        // 获取当前拼团下所有的sku列表
        List<GroupBuyActivitySku> groupBuyActivitySkuList = groupBuyActivitySkuService.getListByGroupActivityId(id);

        Map<Integer, List<GroupBuyActivitySku>> skuListGroupByProductIdMap =
                groupBuyActivitySkuList.stream().collect(Collectors.groupingBy(GroupBuyActivitySku::getProductId));

        for (Map.Entry<Integer, List<GroupBuyActivitySku>> productIdEntry : skuListGroupByProductIdMap.entrySet()) {
            Integer productId = productIdEntry.getKey();
            List<GroupBuyActivitySku> groupBuyActivitySkuListByProductId = productIdEntry.getValue();
            List<GroupBuyActivitySkuResponse> groupBuyActivitySkuResponseList = new ArrayList<>();

            for (GroupBuyActivitySku groupBuyActivitySku : groupBuyActivitySkuListByProductId) {

                GroupBuyActivitySkuResponse skuResponse = new GroupBuyActivitySkuResponse();

                skuResponse.setId(groupBuyActivitySku.getId());
                skuResponse.setGroupActivityId(groupBuyActivitySku.getGroupActivityId());
                skuResponse.setProductId(productId);
                skuResponse.setActivePrice(groupBuyActivitySku.getActivePrice());
                skuResponse.setQuotaShow(groupBuyActivitySku.getQuotaShow());
                skuResponse.setQuota(groupBuyActivitySku.getQuota());
                skuResponse.setSkuId(groupBuyActivitySku.getSkuId());

                groupBuyActivitySkuResponseList.add(skuResponse);
            }

            GroupBuyActivityProductResponse groupBuyActivityProductResponse = new GroupBuyActivityProductResponse();
            groupBuyActivityProductResponse.setProductId(productId);

            groupBuyActivityProductResponse.setGroupBuyActivitySkuResponses(groupBuyActivitySkuResponseList);
            groupBuyActivityProductResponseList.add(groupBuyActivityProductResponse);
        }

        groupBuyActivityResponse.setGroupBuyActivityProductResponseList(groupBuyActivityProductResponseList);

        return groupBuyActivityResponse;
    }

    /**
     * 拼团活动状态修改
     *
     * @param groupBuyActivityId 拼团活动id
     * @param status             活动状态
     * @return Boolean
     */
    @Override
    public Boolean groupBuyActivityStatusOnOrOff(Integer groupBuyActivityId, Integer status) {
        GroupBuyActivity groupBuyActivity = dao.selectById(groupBuyActivityId);
        if (ObjectUtil.isNull(groupBuyActivity)) {
            throw new CrmebException("活动不存在");
        }
        groupBuyActivity.setActivityStatus(status);
        return dao.updateById(groupBuyActivity) > 0;
    }

    /**
     * 拼团状态审核
     *
     * @param groupBuyActivityId 拼团活动id
     * @param groupStatus        拼团状态枚举值
     * @param reason             审核决绝原因
     * @return Boolean 结果
     */
    @Override
    public Boolean groupBuyGroupStatusProgress(Integer groupBuyActivityId, Integer groupStatus, String reason) {
        if (ObjectUtil.isNotNull(groupBuyActivityId) && ObjectUtil.isNotNull(groupStatus)) {
            GroupBuyActivity groupBuyActivity = dao.selectById(groupBuyActivityId);
            if (ObjectUtil.isNull(groupBuyActivity)) {
                throw new CrmebException("活动不存在");
            }
            // 如果是拒绝 则需要填写拒绝理由
            if (groupStatus.equals(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_REFUSE.getCode())
                    && ObjectUtil.isEmpty(reason)) {
                throw new CrmebException("请填写拒绝理由");
            } else {
                groupBuyActivity.setRefusal(reason);
            }
            groupBuyActivity.setGroupStatus(groupStatus);

            // 设置在团购商品中对应的SKU 状态
            boolean status = groupStatus.equals(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_PASS.getCode());
            groupBuyActivitySkuService.reviewGroupBuySkuStatus(groupBuyActivityId, status);
            return dao.updateById(groupBuyActivity) > 0;
        }
        return Boolean.FALSE;
    }

    /**
     * 平台强制关闭
     *
     * @param groupBuyActivityId 拼团活动id
     * @param reason             关闭原因
     * @return 关闭结果
     */
    @Override
    public Boolean groupBuyGroupStatusProgressClose(Integer groupBuyActivityId, String reason) {
        if (ObjectUtil.isNotNull(groupBuyActivityId)) {
            GroupBuyActivity groupBuyActivity = dao.selectById(groupBuyActivityId);
            if (ObjectUtil.isNull(groupBuyActivity)) {
                throw new CrmebException("活动不存在");
            }
            groupBuyActivity.setGroupStatus(GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_REFUSE.getCode());
            if (ObjectUtil.isNotEmpty(reason)) {
                groupBuyActivity.setRefusal(reason);
            }
            return dao.updateById(groupBuyActivity) > 0;
        }
        return null;
    }

    /**
     * 删除拼团活动
     *
     * @param id 活动id
     * @return 删除结果
     */
    @Override
    public Boolean deleteGroupBuyActivity(Integer id) {
        GroupBuyActivity activity = getById(id);
        GroupBuyActivityResponse groupBuyActivityResponse = new GroupBuyActivityResponse();
        BeanUtils.copyProperties(activity, groupBuyActivityResponse);
        if (groupBuyActivityResponse.getGroupProcess().equals(1)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "进行中的拼团活动不能删除");
        }
        GroupBuyActivity groupBuyActivity = new GroupBuyActivity();
        groupBuyActivity.setId(id);
        groupBuyActivity.setIsDel(1);
        return dao.updateById(groupBuyActivity) > 0;
    }

    /**
     * 获取正在拼团中的列表
     *
     * @param limit 限制数量
     * @return 拼团列表
     */
    private List<GroupBuyActivity> getListForShopActive(Integer limit) {
        LambdaQueryWrapper<GroupBuyActivity> queryWrapper = Wrappers.lambdaQuery();
        Date date = CrmebDateUtil.nowDateTime();
        queryWrapper.le(GroupBuyActivity::getStartTime, date)
                .gt(GroupBuyActivity::getEndTime, date)
                .eq(GroupBuyActivity::getIsDel, Boolean.FALSE)
                .eq(GroupBuyActivity::getGroupStatus, GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_PASS.getCode())
                .eq(GroupBuyActivity::getActivityStatus, Boolean.TRUE)
                .orderByDesc(GroupBuyActivity::getId)
                .last("limit " + limit);
        return dao.selectList(queryWrapper);
    }

    /**
     * 获取正在拼团中的列表
     *
     * @param limit 限制数量
     * @return 拼团列表
     */
    private List<GroupBuyActivity> getListForShopActive(Integer merId, Integer limit) {
        LambdaQueryWrapper<GroupBuyActivity> queryWrapper = Wrappers.lambdaQuery();
        Date date = CrmebDateUtil.nowDateTime();
        queryWrapper.le(GroupBuyActivity::getStartTime, date)
                .gt(GroupBuyActivity::getEndTime, date)
                .eq(GroupBuyActivity::getIsDel, Boolean.FALSE)
                .eq(GroupBuyActivity::getGroupStatus, GroupBuyGroupStatusEnum.GROUP_BUY_ENUM_ACTIVITY_STATUS_PASS.getCode())
                .eq(GroupBuyActivity::getActivityStatus, Boolean.TRUE)
                .eq(GroupBuyActivity::getMerId, merId)
                .orderByDesc(GroupBuyActivity::getId)
                .last("limit " + limit);
        return dao.selectList(queryWrapper);
    }

    //////////////////////////////////////////////////////////////////// 以下为移动端方法

    /**
     * 拼团活动首页数据 移动端商城首页
     *
     * @return GroupBuyActivityFrontResponse
     */
    @Override
    public GroupBuyActivityFrontResponse getGroupBuyActivityFrontIndex(Integer limit) {
        GroupBuyActivityFrontResponse response = new GroupBuyActivityFrontResponse();
        // 查询开启的拼团列表前3个，获取到每个拼团活动的第一个主图 和拼团基础信息
        List<GroupBuyActivity> groupBuyActivityList = getListForShopActive(limit);

        List<GroupBuyActivityFrontItemResponse> itemResponse = new ArrayList<>();
        for (GroupBuyActivity groupBuyActivity : groupBuyActivityList) {
            GroupBuyActivityFrontItemResponse item = new GroupBuyActivityFrontItemResponse();
            BeanUtils.copyProperties(groupBuyActivity, item);

            // 根据拼团主id 查询 对应拼团sku 信息和商品信息
            // 主商品的价格数据拿第一个拼团价最低sku的数据
            GroupBuyActivitySku groupBuyActivitySku = groupBuyActivitySkuService.getFrontMinActivePriceByGroupActivityId(groupBuyActivity.getId());
            GroupBuyActivityProductResponse productResponse = new GroupBuyActivityProductResponse();
            if (ObjectUtil.isNotNull(groupBuyActivitySku)) {
                GroupBuyActivitySkuResponse skuResponse = new GroupBuyActivitySkuResponse();
                List<GroupBuyActivitySkuResponse> skuResponses = new ArrayList<>();
                BeanUtils.copyProperties(groupBuyActivitySku, skuResponse);

                Product product = productService.getById(groupBuyActivitySku.getProductId());
                ProductAttrValue attrValue = productAttrValueService.getById(groupBuyActivitySku.getSkuId());

                AttrValueResponse attrValueResponse = new AttrValueResponse();
                BeanUtils.copyProperties(attrValue, attrValueResponse);

                // 商品基础的sku 信息
                skuResponse.setProductName(product.getName());
                List<AttrValueResponse> attrValueResponseList = new ArrayList<>();
                attrValueResponse.setPrice(attrValue.getPrice());
                attrValueResponseList.add(attrValueResponse);
                skuResponse.setAttrValue(attrValueResponseList);

                skuResponse.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(groupBuyActivitySku.getProductId(), groupBuyActivity.getId()));

                productResponse.setProductId(groupBuyActivitySku.getProductId());
                productResponse.setProductName(product.getName());
                productResponse.setImage(product.getImage());

                skuResponses.add(skuResponse);
                productResponse.setGroupBuyActivitySkuResponses(skuResponses);
                item.setGroupBuyActivityProductResponse(productResponse);
            }

            item.setGroupBuyActivityProductResponse(productResponse);
            itemResponse.add(item);
        }


        response.setItems(itemResponse);
        // 获取历史拼团成功的数量
        response.setTotalAllOrderDone(groupBuyUserService.getGroupBuyUserDoneTotalCount());


        // 获取拼团成功的最后5个用户头像
        List<GroupBuyUser> lastGroupBuyUserList = groupBuyUserService.getLastGroupBuyUserList(null, 5);
        List<String> groupDoneUserImages = Collections.emptyList();
        if (!lastGroupBuyUserList.isEmpty()) {
            groupDoneUserImages = lastGroupBuyUserList.stream().map(GroupBuyUser::getGroupAvatar).collect(Collectors.toList());
        }
        response.setOrderDoneUserImages(groupDoneUserImages);
        return response;
    }

    /**
     * 商户首页拼团卡片数据获取
     * @param merId 商户ID
     * @param limit 商品条数
     */
    @Override
    public GroupBuyActivityFrontResponse getGroupBuyActivityMerchantFrontIndex(Integer merId, Integer limit) {
        GroupBuyActivityFrontResponse response = new GroupBuyActivityFrontResponse();
        // 查询开启的拼团列表前3个，获取到每个拼团活动的第一个主图 和拼团基础信息
        List<GroupBuyActivity> groupBuyActivityList = getListForShopActive(merId, limit);

        List<GroupBuyActivityFrontItemResponse> itemResponse = new ArrayList<>();
        for (GroupBuyActivity groupBuyActivity : groupBuyActivityList) {
            GroupBuyActivityFrontItemResponse item = new GroupBuyActivityFrontItemResponse();
            BeanUtils.copyProperties(groupBuyActivity, item);

            // 根据拼团主id 查询 对应拼团sku 信息和商品信息
            // 主商品的价格数据拿第一个拼团价最低sku的数据
            GroupBuyActivitySku groupBuyActivitySku = groupBuyActivitySkuService.getFrontMinActivePriceByGroupActivityId(groupBuyActivity.getId());
            GroupBuyActivityProductResponse productResponse = new GroupBuyActivityProductResponse();
            if (ObjectUtil.isNotNull(groupBuyActivitySku)) {
                GroupBuyActivitySkuResponse skuResponse = new GroupBuyActivitySkuResponse();
                List<GroupBuyActivitySkuResponse> skuResponses = new ArrayList<>();
                BeanUtils.copyProperties(groupBuyActivitySku, skuResponse);

                Product product = productService.getById(groupBuyActivitySku.getProductId());
                ProductAttrValue attrValue = productAttrValueService.getById(groupBuyActivitySku.getSkuId());

                AttrValueResponse attrValueResponse = new AttrValueResponse();
                BeanUtils.copyProperties(attrValue, attrValueResponse);

                // 商品基础的sku 信息
                skuResponse.setProductName(product.getName());
                List<AttrValueResponse> attrValueResponseList = new ArrayList<>();
                attrValueResponse.setPrice(attrValue.getPrice());
                attrValueResponseList.add(attrValueResponse);
                skuResponse.setAttrValue(attrValueResponseList);

                skuResponse.setLatestBuyCount(groupBuyUserService.getOrderDoneCountByProductIdAndActivityId(groupBuyActivitySku.getProductId(), groupBuyActivity.getId()));

                productResponse.setProductId(groupBuyActivitySku.getProductId());
                productResponse.setProductName(product.getName());
                productResponse.setImage(product.getImage());

                skuResponses.add(skuResponse);
                productResponse.setGroupBuyActivitySkuResponses(skuResponses);
                item.setGroupBuyActivityProductResponse(productResponse);
            }

            item.setGroupBuyActivityProductResponse(productResponse);
            itemResponse.add(item);
        }


        response.setItems(itemResponse);
        // 获取历史拼团成功的数量
        response.setTotalAllOrderDone(groupBuyUserService.getGroupBuyUserDoneTotalCount(merId));


        // 获取拼团成功的最后5个用户头像
        List<GroupBuyUser> lastGroupBuyUserList = groupBuyUserService.getLastGroupBuyUserList(null, merId, 5);
        List<String> groupDoneUserImages = Collections.emptyList();
        if (!lastGroupBuyUserList.isEmpty()) {
            groupDoneUserImages = lastGroupBuyUserList.stream().map(GroupBuyUser::getGroupAvatar).collect(Collectors.toList());
        }
        response.setOrderDoneUserImages(groupDoneUserImages);
        return response;
    }

    //////////////////////////////////////////////////////////////////// 以下为工具方法

    /**
     * 拼团商品必要时的验证
     *
     * @param groupBuySkuList 拼团商品 SKU 列表
     */
    private void validGroupProduct(List<GroupBuyActivitySku> groupBuySkuList) {
        if (ObjectUtil.isNotEmpty(groupBuySkuList)) {
            for (GroupBuyActivitySku groupBuySku : groupBuySkuList) {
                ProductAttrValue sku = productAttrValueService.getById(groupBuySku.getSkuId());
                if (ObjectUtil.isNull(sku)) {
                    throw new CrmebException("商品不存在");
                }
                if (sku.getStock() < groupBuySku.getQuotaShow()) {
                    throw new CrmebException("商品库存不得大于原始商品库存");
                }
            }
        }
    }
}

