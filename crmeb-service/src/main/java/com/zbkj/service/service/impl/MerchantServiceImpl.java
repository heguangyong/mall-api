package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.*;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.bill.MerchantDailyStatement;
import com.zbkj.common.model.bill.MerchantMonthStatement;
import com.zbkj.common.model.express.ShippingTemplates;
import com.zbkj.common.model.merchant.*;
import com.zbkj.common.model.page.PageDiy;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.system.SystemNotification;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.*;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.common.vo.MerchantConfigInfoVo;
import com.zbkj.common.vo.MerchantSettlementInfoVo;
import com.zbkj.service.dao.MerchantDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StoreServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantDao, Merchant> implements MerchantService {

    @Resource
    private MerchantDao dao;

    private final Logger logger = LoggerFactory.getLogger(MerchantServiceImpl.class);

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SystemAdminService adminService;
    @Autowired
    private ProductService productService;
    @Autowired
    private MerchantInfoService merchantInfoService;
    @Autowired
    private MerchantCategoryService merchantCategoryService;
    @Autowired
    private MerchantTypeService merchantTypeService;
    @Autowired
    private MerchantDailyStatementService merchantDailyStatementService;
    @Autowired
    private MerchantMonthStatementService merchantMonthStatementService;
    @Autowired
    private MerchantApplyService merchantApplyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMerchantCollectService userMerchantCollectService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private ShippingTemplatesService shippingTemplatesService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private SystemNotificationService systemNotificationService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private SeckillProductService seckillProductService;
    @Autowired
    private PageDiyService pageDiyService;
    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 商户分页列表
     *
     * @param searchRequest    搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    @Override
    public PageInfo<MerchantPageResponse> getAdminPage(MerchantSearchRequest searchRequest, PageParamRequest pageParamRequest) {
        Page<Merchant> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Merchant> lqw = getAdminPageWrapperBySearch(searchRequest);
        lqw.orderByDesc(Merchant::getSort);
        List<Merchant> merchantList = dao.selectList(lqw);
        if (CollUtil.isEmpty(merchantList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<MerchantPageResponse> responseList = merchantList.stream().map(e -> {
            MerchantPageResponse response = new MerchantPageResponse();
            BeanUtils.copyProperties(e, response);
            SystemAdmin systemAdmin = adminService.getById(e.getCreateId());
            response.setCreateName(systemAdmin.getRealName());
            if (crmebConfig.getPhoneMaskSwitch()) {
                response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    private LambdaQueryWrapper<Merchant> getAdminPageWrapperBySearch(MerchantSearchRequest searchRequest) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(searchRequest.getCategoryId())) {
            lqw.eq(Merchant::getCategoryId, searchRequest.getCategoryId());
        }
        if (ObjectUtil.isNotNull(searchRequest.getTypeId())) {
            lqw.eq(Merchant::getTypeId, searchRequest.getTypeId());
        }
        if (ObjectUtil.isNotNull(searchRequest.getIsSelf())) {
            lqw.eq(Merchant::getIsSelf, searchRequest.getIsSelf());
        }
        if (ObjectUtil.isNotNull(searchRequest.getIsSwitch())) {
            lqw.eq(Merchant::getIsSwitch, searchRequest.getIsSwitch());
        }
        if (StrUtil.isNotBlank(searchRequest.getKeywords())) {
            String keywords = URLUtil.decode(searchRequest.getKeywords());
            lqw.and(i -> i.like(Merchant::getName, keywords)
//                    .or().apply(StrUtil.format(" find_in_set('{}', keywords)", keywords)));
                    .or().apply(" find_in_set({0}, keywords)", keywords));
        }
        if (StrUtil.isNotBlank(searchRequest.getDateLimit())) {
            DateLimitUtilVo dateLimitUtilVo = CrmebDateUtil.getDateLimit(searchRequest.getDateLimit());
            lqw.between(Merchant::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        lqw.eq(Merchant::getIsDel, false);
        return lqw;
    }

    /**
     * 商户分页列表表头数量
     *
     * @return MerchantHeaderNumResponse
     */
    @Override
    public MerchantHeaderNumResponse getListHeaderNum(MerchantSearchRequest searchRequest) {
        Integer openNum = getOpenNum(searchRequest);
        // 关闭的商户数
        Integer closeNum = getCloseNum(searchRequest);
        return new MerchantHeaderNumResponse(openNum, closeNum);
    }

    private Integer getOpenNum(MerchantSearchRequest searchRequest) {
        LambdaQueryWrapper<Merchant> lqw = getAdminPageWrapperBySearch(searchRequest);
        lqw.eq(Merchant::getIsSwitch, 1);
        return dao.selectCount(lqw);
    }

    private Integer getCloseNum(MerchantSearchRequest searchRequest) {
        LambdaQueryWrapper<Merchant> lqw = getAdminPageWrapperBySearch(searchRequest);
        lqw.eq(Merchant::getIsSwitch, 0);
        return dao.selectCount(lqw);
    }

    /**
     * 获取所有商户数量
     *
     * @return Integer
     */
    @Override
    public Integer getAllCount() {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(Merchant::getIsDel, false);
        return dao.selectCount(lqw);
    }

    /**
     * 获取商户客服信息
     */
    @Override
    public MerchantServiceInfoResponse getCustomerServiceInfo(Integer id) {
        getByIdException(id);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(id);
        MerchantServiceInfoResponse response = new MerchantServiceInfoResponse();
        BeanUtils.copyProperties(merchantInfo, response);
        return response;
    }

    /**
     * 添加商户
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean add(MerchantAddRequest request) {
        // 检查商户名or商户账号or商户手机号是否重复
        if (checkMerchantName(request.getName()) || merchantApplyService.checkMerchantName(request.getName())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户名已存在");
        }
        if (checkMerchantPhone(request.getPhone()) || merchantApplyService.checkMerchantPhone(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户手机号已存在");
        }
        if (adminService.checkAccount(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户手机号已存在");
        }
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();

        Boolean execute = transactionTemplate.execute(e -> {
            Boolean aBoolean = commonAdd(request, MerchantConstants.CREATE_TYPE_ADMIN, loginUserVo.getUser().getId());
            if (!aBoolean) {
                logger.error("后台新增商户事务失败！准备回滚");
                e.setRollbackOnly();
            }
            return Boolean.TRUE;
        });
        if (execute) {
            SystemNotification payNotification = systemNotificationService.getByMark(NotifyConstants.AUDIT_SUCCESS_MARK);
            // 发送短信
            if (StrUtil.isNotBlank(request.getPhone()) && payNotification.getIsSms().equals(1)) {
                String merSiteUrl = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_MERCHANT_SITE_URL);
                if (StrUtil.isBlank(merSiteUrl)) {
                    merSiteUrl = "";
                }
                try {
                    smsService.sendMerchantAuditSuccessNotice(request.getPhone(), DateUtil.date().toString(),
                            request.getName(), request.getPhone(),
                            "000000",
                            merSiteUrl);
                } catch (Exception e) {
                    logger.error("商户创建成功短信发送异常，{}", e.getMessage());
                }
            }
        }
        return execute;
    }

    /**
     * 公共商户添加
     *
     * @param request    商户参数
     * @param createType 创建类型：后台创建、审核
     * @param createId   创建人/审核人ID
     */
    private Boolean commonAdd(MerchantAddRequest request, String createType, Integer createId) {
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(request, merchant);
        if (StrUtil.isNotBlank(merchant.getQualificationPicture())) {
            merchant.setQualificationPicture(systemAttachmentService.clearPrefix(merchant.getQualificationPicture()));
        }
        merchant.setCreateType(createType);
        merchant.setCreateId(createId);

        // 初始化商户信息表
        MerchantInfo merchantInfo = new MerchantInfo();
        // 初始化日/月帐单
        MerchantDailyStatement dailyStatement = new MerchantDailyStatement();
        MerchantMonthStatement monthStatement = new MerchantMonthStatement();
        // 初始化一条全国包邮的运费模板
        ShippingTemplates shippingTemplates = new ShippingTemplates();
        shippingTemplates.setName(ShippingTemplatesConstants.DEFAULT_NAME);
        shippingTemplates.setType(ShippingTemplatesConstants.CHARGE_MODE_TYPE_UNKNOWN);
        shippingTemplates.setAppoint(ShippingTemplatesConstants.APPOINT_TYPE_ALL);
        shippingTemplates.setSort(999);

        // 初始化管理账号
        boolean save;
        save = save(merchant);
        if (!save) return Boolean.FALSE;
        SystemAdmin merchantAdmin = initMerchantAdmin(request.getPhone(), request.getName());
        merchantAdmin.setMerId(merchant.getId());
        save = adminService.save(merchantAdmin);
        if (!save) return Boolean.FALSE;
        merchant.setAdminId(merchantAdmin.getId());
        dao.updateById(merchant);

        merchantInfo.setMerId(merchant.getId());
        save = merchantInfoService.save(merchantInfo);
        if (!save) return Boolean.FALSE;

        dailyStatement.setMerId(merchant.getId()).setDataDate(DateUtil.date().toDateStr());
        monthStatement.setMerId(merchant.getId()).setDataDate(DateUtil.date().toString(DateConstants.DATE_FORMAT_MONTH));
        save = merchantDailyStatementService.save(dailyStatement);
        if (!save) return Boolean.FALSE;
        save = merchantMonthStatementService.save(monthStatement);
        if (!save) return Boolean.FALSE;
        shippingTemplates.setMerId(merchant.getId());
        save = shippingTemplatesService.save(shippingTemplates);

        DateTime date = DateUtil.date();
        PageDiy pageDiy = pageDiyService.getMerchantDefDiy();
        pageDiy.setId(null);
        pageDiy.setMerId(merchant.getId());
        pageDiy.setAddTime(date);
        pageDiy.setUpdateTime(date);
        pageDiyService.save(pageDiy);
        return save;
    }

    /**
     * 初始化店长管理员账号
     *
     * @param account 账号
     * @param name    名称
     * @return SystemAdmin
     */
    private SystemAdmin initMerchantAdmin(String account, String name) {
        SystemAdmin systemAdmin = new SystemAdmin();
        systemAdmin.setAccount(account);
        systemAdmin.setPwd(CrmebUtil.merchantInitPassword(account));
        systemAdmin.setRealName(name);
        systemAdmin.setStatus(true);
        systemAdmin.setRoles(RoleEnum.SUPER_MERCHANT.getValue().toString());
        systemAdmin.setType(RoleEnum.SUPER_MERCHANT.getValue());
        systemAdmin.setPhone(account);
        return systemAdmin;
    }

    /**
     * 编辑商户
     *
     * @param request 请求参数
     * @return Boolean
     */
    @Override
    public Boolean edit(MerchantUpdateRequest request) {
        Merchant merchant = getByIdException(request.getId());
        if (!request.getName().equals(merchant.getName())) {
            if (checkMerchantName(request.getName(), request.getId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户名称已存在");
            }
        }
        Merchant tempMerchant = new Merchant();
        BeanUtils.copyProperties(request, tempMerchant);
        if (StrUtil.isNotBlank(tempMerchant.getQualificationPicture())) {
            tempMerchant.setQualificationPicture(systemAttachmentService.clearPrefix(tempMerchant.getQualificationPicture()));
        }
        return dao.updateById(tempMerchant) > 0;
    }

    /**
     * 重置商户密码
     */
    @Override
    public Boolean resetPassword(Integer id) {
        Merchant merchant = getByIdException(id);
        SystemAdmin systemAdmin = adminService.getDetail(merchant.getAdminId());
        systemAdmin.setPwd(CrmebUtil.merchantInitPassword(systemAdmin.getAccount()));
        return adminService.updateById(systemAdmin);
    }

    /**
     * 修改复制商品数量
     *
     * @param request 请求对象
     * @return Boolean
     */
    @Override
    public Boolean updateCopyProductNum(MerchantUpdateProductNumRequest request) {
        Merchant merchant = getByIdException(request.getId());
        if (request.getType().equals(Constants.OPERATION_TYPE_SUBTRACT) && merchant.getCopyProductNum() - request.getNum() < 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "扣减后的数量不能小于0");
        }
        UpdateWrapper<Merchant> updateWrapper = new UpdateWrapper<>();
        if (request.getType().equals(Constants.OPERATION_TYPE_ADD)) {
            updateWrapper.setSql(StrUtil.format("copy_product_num = copy_product_num + {}", request.getNum()));
        }
        if (request.getType().equals(Constants.OPERATION_TYPE_SUBTRACT)) {
            updateWrapper.setSql(StrUtil.format("copy_product_num = copy_product_num - {}", request.getNum()));
            updateWrapper.last(StrUtil.format("and (copy_product_num - {} >= 0)", request.getNum()));
        }
        updateWrapper.eq("id", request.getId());
        return update(updateWrapper);
    }

    /**
     * 平台端商户详情
     *
     * @param id 商户ID
     * @return MerchantPlatformDetailResponse
     */
    @Override
    public MerchantPlatformDetailResponse getPlatformDetail(Integer id) {
        Merchant merchant = getByIdException(id);
        MerchantPlatformDetailResponse response = new MerchantPlatformDetailResponse();
        BeanUtils.copyProperties(merchant, response);
        response.setAccount(merchant.getPhone());
        if (crmebConfig.getPhoneMaskSwitch()) {
            response.setAccount(CrmebUtil.maskMobile(response.getAccount()));
            response.setPhone(CrmebUtil.maskMobile(response.getPhone()));
        }
        return response;
    }

    /**
     * 商户推荐开关
     *
     * @param id 商户ID
     * @return Boolean
     */
    @Override
    public Boolean recommendSwitch(Integer id) {
        Merchant merchant = getByIdException(id);
        merchant.setIsRecommend(!merchant.getIsRecommend());
        return dao.updateById(merchant) > 0;
    }

    /**
     * 关闭商户
     *
     * @param id 商户ID
     * @return Boolean
     */
    @Override
    public Boolean close(Integer id) {
        Merchant merchant = getByIdException(id);
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_CLOSE);
        }
        // 1.修改商户状态，2.强制下架商户所有商品,3.手动关闭的商户，商品审核状态自动切换为需要审核状态
        merchant.setIsSwitch(false);
        merchant.setProductSwitch(true);
        merchant.setIsForceShutdown(true);
        return transactionTemplate.execute(e -> {
            dao.updateById(merchant);
            productService.forcedRemovalAll(merchant.getId());
            // 4.强制下架商户所有商品对应的营销商品
            seckillProductService.downByMerId(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 开启商户
     *
     * @param id 商户ID
     * @return Boolean
     */
    @Override
    public Boolean open(Integer id) {
        Merchant merchant = getByIdException(id);
        if (merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_OPEN);
        }
        openMerchantValidator(merchant);
        merchant.setIsSwitch(true);
        merchant.setIsForceShutdown(false);
        return dao.updateById(merchant) > 0;
    }

    /**
     * 入驻审核成功，初始化商户
     *
     * @param request   商户添加参数
     * @param auditorId 审核员id
     */
    @Override
    public Boolean auditSuccess(MerchantAddRequest request, Integer auditorId) {
        // 检查商户名or商户账号or商户手机号是否重复
        if (checkMerchantName(request.getName())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户名已存在");
        }
        if (checkMerchantPhone(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户手机号已存在");
        }
        if (adminService.checkAccount(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户手机号已存在");
        }
        return commonAdd(request, MerchantConstants.CREATE_TYPE_APPLY, auditorId);
    }

    /**
     * 获取商户详情
     *
     * @param id 商户ID
     * @return Merchant
     */
    @Override
    public Merchant getByIdException(Integer id) {
        Merchant merchant = getById(id);
        if (ObjectUtil.isNull(merchant) || merchant.getIsDel()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_NOT_EXIST);
        }
        return merchant;
    }

    /**
     * 扣减商户复制商品数量
     *
     * @param id 商户id
     * @return Boolean
     */
    @Override
    public Boolean subCopyProductNum(Integer id) {
        UpdateWrapper<Merchant> wrapper = Wrappers.update();
        wrapper.setSql(" copy_product_num = copy_product_num -1 ");
        wrapper.eq("id", id);
        wrapper.ge("copy_product_num", 1);
        return update(wrapper);
    }

    /**
     * 操作商户余额
     *
     * @param merId 商户id
     * @param price 金额
     * @param type  操作类型
     * @return Boolean
     */
    @Override
    public Boolean operationBalance(Integer merId, BigDecimal price, String type) {
        UpdateWrapper<Merchant> wrapper = Wrappers.update();
        if (type.equals(Constants.OPERATION_TYPE_ADD)) {
            wrapper.setSql(" balance = balance + " + price);
        } else {
            wrapper.setSql(" balance = balance - " + price);
//            wrapper.last(StrUtil.format("balance - {} >= 0", price));
        }
        wrapper.eq("id", merId);
        return update(wrapper);
    }

    /**
     * 根据商户id集合获取商户信息
     * @param merIdList 商户id集合
     * @return 查询到的商户信息
     */
    public List<Merchant> getListByIdList(List<Integer> merIdList) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.in(Merchant::getId, merIdList);
        return dao.selectList(lqw);
    }

    /**
     * 获取商户Id组成的Map
     *
     * @param merIdList 商户ID列表
     * @return Map
     */
    @Override
    public Map<Integer, Merchant> getMerIdMapByIdList(List<Integer> merIdList) {
        List<Merchant> merchantList = getListByIdList(merIdList);
        Map<Integer, Merchant> merchantMap = new HashMap<>();
        merchantList.forEach(merchant -> {
            merchantMap.put(merchant.getId(), merchant);
        });
        return merchantMap;
    }

    /**
     * 商户端商户基础信息
     *
     * @return MerchantBaseInfoResponse
     */
    @Override
    public MerchantBaseInfoResponse getBaseInfo() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = getByIdException(systemAdmin.getMerId());
        MerchantBaseInfoResponse baseInfo = new MerchantBaseInfoResponse();
        MerchantCategory merchantCategory = merchantCategoryService.getById(merchant.getCategoryId());
        MerchantType merchantType = merchantTypeService.getById(merchant.getTypeId());
        BeanUtils.copyProperties(merchant, baseInfo);
        baseInfo.setMerCategory(merchantCategory.getName());
        baseInfo.setMerType(merchantType.getName());
        baseInfo.setReceiptPrintingSwitch(merchant.getReceiptPrintingSwitch());
        return baseInfo;
    }

    /**
     * 商户端商户配置信息
     *
     * @return MerchantConfigInfoResponse
     */
    @Override
    public MerchantConfigInfoVo getConfigInfo() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = getByIdException(systemAdmin.getMerId());
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(merchant.getId());
        MerchantConfigInfoVo infoResponse = new MerchantConfigInfoVo();
        BeanUtils.copyProperties(merchant, infoResponse);
        BeanUtils.copyProperties(merchantInfo, infoResponse);
        infoResponse.setTxMapKey(systemConfigService.getValueByKey(SysConfigConstants.CONFIG_SITE_TENG_XUN_MAP_KEY));
        return infoResponse;
    }

    /**
     * 商户端商户结算信息
     *
     * @return MerchantTransferInfoResponse
     */
    @Override
    public MerchantSettlementInfoVo getSettlementInfo() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(systemAdmin.getMerId());
        if (ObjectUtil.isNull(merchantInfo)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_NOT_EXIST);
        }
        MerchantSettlementInfoVo response = new MerchantSettlementInfoVo();
        BeanUtils.copyProperties(merchantInfo, response);
        return response;
    }

    /**
     * 商户端商户配置信息编辑
     *
     * @param request 编辑参数
     * @return Boolean
     */
    @Override
    public Boolean configInfoEdit(MerchantConfigInfoVo request) {
        serviceTypeCheck(request);
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant tempMerchant = getByIdException(systemAdmin.getMerId());
        MerchantInfo tempMerchantInfo = merchantInfoService.getByMerId(tempMerchant.getId());
        Merchant merchant = new Merchant();
        MerchantInfo merchantInfo = new MerchantInfo();
        BeanUtils.copyProperties(request, merchant);
        BeanUtils.copyProperties(request, merchantInfo);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        merchant.setBackImage(systemAttachmentService.clearPrefix(request.getBackImage(), cdnUrl));
        merchant.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar(), cdnUrl));
        merchant.setRectangleLogo(systemAttachmentService.clearPrefix(request.getRectangleLogo(), cdnUrl));
        merchant.setCoverImage(systemAttachmentService.clearPrefix(request.getCoverImage(), cdnUrl));
        merchant.setStreetBackImage(systemAttachmentService.clearPrefix(request.getStreetBackImage(), cdnUrl));
        merchant.setId(tempMerchant.getId());
        if (StrUtil.isNotBlank(merchant.getPcBanner())) {
            merchant.setPcBanner(systemAttachmentService.clearPrefix(merchant.getPcBanner(), cdnUrl));
        }
        if (StrUtil.isNotBlank(merchant.getPcBackImage())) {
            merchant.setPcBackImage(systemAttachmentService.clearPrefix(merchant.getPcBackImage(), cdnUrl));
        }
        merchant.setReceiptPrintingSwitch(request.getReceiptPrintingSwitch());
        merchant.setElectrPrintingSwitch(request.getElectrPrintingSwitch());
        merchantInfo.setId(tempMerchantInfo.getId());
        return transactionTemplate.execute(e -> {
            updateById(merchant);
            merchantInfoService.updateById(merchantInfo);
            return Boolean.TRUE;
        });
    }

    /**
     * 商户端商户结算信息编辑
     *
     * @param request 编辑参数
     * @return Boolean
     */
    @Override
    public Boolean settlementInfoEdit(MerchantSettlementInfoVo request) {
        settlementInfoCheck(request);
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        MerchantInfo tempMerchantInfo = merchantInfoService.getByMerId(systemAdmin.getMerId());
        if (ObjectUtil.isNull(tempMerchantInfo)) {
            throw new CrmebException(MerchantResultCode.MERCHANT_NOT_EXIST);
        }
        MerchantInfo merchantInfo = new MerchantInfo();
        BeanUtils.copyProperties(request, merchantInfo);
        merchantInfo.setId(tempMerchantInfo.getId());

        if (!request.getSettlementType().equals(MerchantConstants.MERCHANT_SETTLEMENT_TYPE_BANK)) {
            String cdnUrl = systemAttachmentService.getCdnUrl();
            if (StrUtil.isNotBlank(request.getWechatQrcodeUrl())) {
                merchantInfo.setWechatQrcodeUrl(systemAttachmentService.clearPrefix(request.getWechatQrcodeUrl(), cdnUrl));
            }
            if (StrUtil.isNotBlank(request.getAlipayQrcodeUrl())) {
                merchantInfo.setAlipayQrcodeUrl(systemAttachmentService.clearPrefix(request.getAlipayQrcodeUrl(), cdnUrl));
            }
        }
        return merchantInfoService.updateById(merchantInfo);
    }

    /**
     * 结算信息校验
     */
    private void settlementInfoCheck(MerchantSettlementInfoVo request) {
        if (request.getSettlementType().equals(MerchantConstants.MERCHANT_SETTLEMENT_TYPE_BANK)) {
            if (StrUtil.isBlank(request.getBankUserName())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "持卡人姓名不能为空");
            }
            if (StrUtil.isBlank(request.getBankName())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "银行名称不能为空");
            }
            if (StrUtil.isBlank(request.getBankCard())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "银行卡号不能为空");
            }
            if (StrUtil.isBlank(request.getBankAddress())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开户地址不能为空");
            }
        }
        if (request.getSettlementType().equals(MerchantConstants.MERCHANT_SETTLEMENT_TYPE_WECHAT)) {
            if (StrUtil.isBlank(request.getWechatCode())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "微信号不能为空");
            }
            if (StrUtil.isBlank(request.getWechatQrcodeUrl())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "微信收款二维码不能为空");
            }
            if (StrUtil.isBlank(request.getRealName())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "真实姓名不能为空");
            }
        }
        if (request.getSettlementType().equals(MerchantConstants.MERCHANT_SETTLEMENT_TYPE_ALIPAY)) {
            if (StrUtil.isBlank(request.getAlipayCode())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "支付宝账号不能为空");
            }
            if (StrUtil.isBlank(request.getAlipayQrcodeUrl())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "支付宝收款二维码不能为空");
            }
        }
    }

    /**
     * 商户端商户开关
     *
     * @return Boolean
     */
    @Override
    public Boolean updateSwitch() {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = getByIdException(systemAdmin.getMerId());
        if (merchant.getIsSwitch()) {
            merchant.setIsSwitch(false);
            productService.downByMerId(merchant.getId());
            return updateById(merchant);
        }
        openMerchantValidator(merchant);
        if (merchant.getIsForceShutdown()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "平台强制关闭，请联系平台管理员");
        }
        merchant.setIsSwitch(true);
        return updateById(merchant);
    }

    /**
     * 开店校验
     *
     * @param merchant 商户信息
     */
    private void openMerchantValidator(Merchant merchant) {
        if (StrUtil.isBlank(merchant.getAvatar()) || StrUtil.isBlank(merchant.getBackImage()) || StrUtil.isBlank(merchant.getStreetBackImage())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先进行商户头像、背景图配置");
        }
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(merchant.getId());
        if (StrUtil.isBlank(merchantInfo.getServiceLink()) && StrUtil.isBlank(merchantInfo.getServicePhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先进行客服信息配置");
        }
    }

    /**
     * 获取所有的商户id
     *
     * @return List
     */
    @Override
    public List<Integer> getAllId() {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getIsDel, false);
        List<Merchant> merchantList = dao.selectList(lqw);
        return merchantList.stream().map(Merchant::getId).collect(Collectors.toList());
    }

    /**
     * 商户入驻申请
     *
     * @param request 申请参数
     * @return Boolean
     */
    @Override
    public Boolean settledApply(MerchantSettledApplyRequest request) {
        // 检查商户名or手机号是否重复
        if (checkMerchantName(request.getName())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户名称已存在");
        }
        if (checkMerchantPhone(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号已存在");
        }
        //检测验证码
        smsService.checkValidateCode(SmsConstants.VERIFICATION_CODE_SCENARIO_SETTLED, request.getPhone(), request.getCaptcha());

        MerchantCategory merchantCategory = merchantCategoryService.getByIdException(request.getCategoryId());
        request.setHandlingFee(merchantCategory.getHandlingFee());
        return merchantApplyService.settledApply(request);
    }

    /**
     * 商户入驻申请记录
     *
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<MerchantSettledResponse> findSettledRecord(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        PageInfo<MerchantApply> pageInfo = merchantApplyService.findSettledRecord(uid, pageParamRequest);
        List<MerchantApply> merchantApplyList = pageInfo.getList();
        if (CollUtil.isEmpty(merchantApplyList)) {
            return CommonPage.copyPageInfo(pageInfo, CollUtil.newArrayList());
        }
        List<MerchantSettledResponse> responseList = merchantApplyList.stream().map(apply -> {
            MerchantSettledResponse response = new MerchantSettledResponse();
            BeanUtils.copyProperties(apply, response);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(pageInfo, responseList);
    }

    /**
     * 商户搜索列表
     *
     * @param request          搜索参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<MerchantSearchResponse> findSearchList(MerchantMoveSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Merchant> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(request.getCategoryIds())) {
            List<Integer> categoryIdList = CrmebUtil.stringToArray(request.getCategoryIds());
            lqw.in(Merchant::getCategoryId, categoryIdList);
        }
        if (StrUtil.isNotBlank(request.getTypeIds())) {
            List<Integer> typeIdList = CrmebUtil.stringToArray(request.getTypeIds());
            lqw.in(Merchant::getTypeId, typeIdList);
        }
        if (ObjectUtil.isNotNull(request.getIsSelf())) {
            lqw.eq(Merchant::getIsSelf, request.getIsSelf());
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String keywords = URLUtil.decode(request.getKeywords());
            lqw.and(i -> i.like(Merchant::getName, keywords)
                    .or().apply(" find_in_set({0}, keywords)", keywords));
        }
        lqw.eq(Merchant::getIsSwitch, true);
        lqw.eq(Merchant::getIsDel, false);
        lqw.orderByDesc(Merchant::getStarLevel, Merchant::getIsRecommend, Merchant::getSort, Merchant::getId);
        List<Merchant> merchantList = dao.selectList(lqw);
        if (CollUtil.isEmpty(merchantList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        Integer userId = userService.getUserId();
        List<MerchantSearchResponse> responseList = merchantList.stream().map(merchant -> {
            MerchantSearchResponse response = new MerchantSearchResponse();
            BeanUtils.copyProperties(merchant, response);
            // 获取商户推荐商品
            List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 3);
            response.setProList(merchantProductResponseList);
            // 店铺关注人数
            Integer followerNum = userMerchantCollectService.getCountByMerId(merchant.getId());
            response.setFollowerNum(followerNum);
            if (userId > 0) {
                response.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
            }
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 店铺街
     *
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<MerchantSearchResponse> getStreet(PageParamRequest pageParamRequest) {
        Page<Merchant> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(Merchant::getIsSwitch, true);
        lqw.eq(Merchant::getIsDel, false);
        lqw.orderByDesc(Merchant::getIsRecommend, Merchant::getSort, Merchant::getId);
        List<Merchant> merchantList = dao.selectList(lqw);
        if (CollUtil.isEmpty(merchantList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<MerchantSearchResponse> responseList = merchantList.stream().map(merchant -> {
            MerchantSearchResponse response = new MerchantSearchResponse();
            BeanUtils.copyProperties(merchant, response);
            // 获取商户推荐商品
            List<ProMerchantProductResponse> merchantProductResponseList = productService.getRecommendedProductsByMerId(merchant.getId(), 3);
            response.setProList(merchantProductResponseList);
            // 店铺关注人数
            Integer followerNum = userMerchantCollectService.getCountByMerId(merchant.getId());
            response.setFollowerNum(followerNum);
            response.setPcLogo(merchant.getPcLogo());
            response.setPcGoodStoreCoverImage(merchant.getPcGoodStoreCoverImage());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 店铺首页信息
     *
     * @param id 商户id
     * @return MerchantIndexInfoResponse
     */
    @Override
    public MerchantIndexInfoResponse getIndexInfo(Integer id) {
        Integer userId = userService.getUserId();
        Merchant merchant = getByIdException(id);
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_CLOSE);
        }
        MerchantIndexInfoResponse response = new MerchantIndexInfoResponse();
        BeanUtils.copyProperties(merchant, response);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(merchant.getId());
        BeanUtils.copyProperties(merchantInfo, response);
        response.setIsCollect(false);
        if (userId > 0) {
            response.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
            // 商户访客量统计
            String dateStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
            redisUtil.incrAndCreate(StrUtil.format(RedisConstants.MERCHANT_VISITORS_KEY, dateStr, merchant.getId()));
        }
        return response;
    }

    /**
     * 店铺详细信息
     *
     * @param id 商户id
     * @return MerchantDetailResponse
     */
    @Override
    public MerchantDetailResponse getDetail(Integer id) {
        Merchant merchant = getByIdException(id);
        if (!merchant.getIsSwitch()) {
            throw new CrmebException("The store is not open");
        }
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(merchant.getId());
        MerchantDetailResponse response = new MerchantDetailResponse();
        BeanUtils.copyProperties(merchant, response);
        BeanUtils.copyProperties(merchantInfo, response);
        response.setFollowerNum(userMerchantCollectService.getCountByMerId(merchant.getId()));
        Integer userId = userService.getUserId();
        if (userId > 0) {
            response.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
        } else {
            response.setIsCollect(false);
        }
        return response;
    }

    /**
     * 商户是否使用商户分类
     *
     * @param cid 分类id
     * @return Boolean
     */
    @Override
    public Boolean isExistCategory(Integer cid) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getIsDel, false);
        lqw.eq(Merchant::getCategoryId, cid);
        lqw.last(" limit 1");
        Merchant merchant = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchant);
    }

    /**
     * 商户是否使用商户类型
     *
     * @param tid 类型id
     * @return Boolean
     */
    @Override
    public Boolean isExistType(Integer tid) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getIsDel, false);
        lqw.eq(Merchant::getTypeId, tid);
        lqw.last(" limit 1");
        Merchant merchant = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchant);
    }

    /**
     * 客服类型校验
     */
    private void serviceTypeCheck(MerchantConfigInfoVo request) {
        if (request.getServiceType().equals(MerchantConstants.MERCHANT_SERVICE_TYPE_H5)) {
            if (StrUtil.isBlank(request.getServiceLink())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "客服H5链接不能为空");
            }
            if (!ReUtil.isMatch(RegularConstants.URL, request.getServiceLink())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "客服H5链接格式不正确");
            }
        }
        if (request.getServiceType().equals(MerchantConstants.MERCHANT_SERVICE_TYPE_PHONE)) {
            if (StrUtil.isBlank(request.getServicePhone())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "客服电话不能为空");
            }
        }
    }

    /**
     * 检查商户手机号是否重复
     *
     * @param phone 商户手机号
     * @return Boolean
     */
    private Boolean checkMerchantPhone(String phone) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getPhone, phone);
        lqw.eq(Merchant::getIsDel, false);
        lqw.last(" limit 1");
        Merchant merchant = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchant);
    }

    /**
     * 修改商户手机号
     *
     * @param request 修改请求对象
     * @return Boolean
     */
    @Override
    public Boolean updatePhone(MerchantUpdatePhoneRequest request) {
        Merchant merchant = getByIdException(request.getId());
        if (request.getPhone().equals(merchant.getPhone())) {
            return Boolean.TRUE;
        }
        if (checkMerchantPhone(request.getPhone(), request.getId()) || merchantApplyService.checkMerchantPhone(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号已存在");
        }
        if (adminService.checkAccount(request.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "手机号已存在");
        }
        merchant.setPhone(request.getPhone());
        SystemAdmin systemAdmin = adminService.getDetail(merchant.getAdminId());
        String pwd = "";
        try {
            pwd = CrmebUtil.decryptPassowrd(systemAdmin.getPwd(), systemAdmin.getAccount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        systemAdmin.setAccount(request.getPhone());
        systemAdmin.setPhone(request.getPhone());
        systemAdmin.setPwd(CrmebUtil.encryptPassword(pwd, request.getPhone()));
        return transactionTemplate.execute(e -> {
            updateById(merchant);
            adminService.updateById(systemAdmin);
            return Boolean.TRUE;
        });
    }

    /**
     * 首页商户列表
     */
    @Override
    public List<IndexMerchantResponse> findIndexList(Integer recomdProdsNum) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.eq(Merchant::getIsSwitch, true);
        lqw.eq(Merchant::getIsDel, false);
        lqw.orderByDesc(Merchant::getIsRecommend, Merchant::getSort, Merchant::getId);
        lqw.last(" limit " + recomdProdsNum);
        List<Merchant> merchantList = dao.selectList(lqw);
        return merchantList.stream().map(mer -> {
            IndexMerchantResponse response = new IndexMerchantResponse();
            BeanUtils.copyProperties(mer, response);
            // 根据商户再获取三条商户对应的3条推荐商品 适用于DIY样式
            response.setProList(productService.getRecommendedProductsByMerId(mer.getId(), 3));
            // 店铺关注人数
            response.setFollowerNum(userMerchantCollectService.getCountByMerId(mer.getId()));

            response.setPcLogo(mer.getPcLogo());
            response.setPcGoodStoreCoverImage(mer.getPcGoodStoreCoverImage());
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * 通过商户id列表获取商户名map
     *
     * @param merIdList 商户id列表
     * @return Map
     */
    @Override
    public Map<Integer, Merchant> getMapByIdList(List<Integer> merIdList) {
        Map<Integer, Merchant> merchantMap = CollUtil.newHashMap();
        if (CollUtil.isEmpty(merIdList)) {
            return merchantMap;
        }
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId, Merchant::getName, Merchant::getIsSelf, Merchant::getTypeId,
                Merchant::getCategoryId, Merchant::getAvatar, Merchant::getProductSwitch);
        lqw.in(Merchant::getId, merIdList);
        List<Merchant> merchantList = dao.selectList(lqw);
        merchantList.forEach(merchant -> {
            merchantMap.put(merchant.getId(), merchant);
        });
        return merchantMap;
    }

    /**
     * 获取商户自提信息
     */
    @Override
    public MerchantTakeTheirResponse getTakeTheir(Integer id) {
        Merchant merchant = getByIdException(id);
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_CLOSE);
        }
        if (!merchant.getIsTakeTheir()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_NOT_TAKE_THEIR);
        }
        MerchantTakeTheirResponse response = new MerchantTakeTheirResponse();
        BeanUtils.copyProperties(merchant, response);
        return response;
    }

    /**
     * 发送入驻申请短信验证码
     *
     * @param phone 手机号
     * @return Boolean
     */
    @Override
    public Boolean sendSettledCode(String phone) {
        return smsService.sendCommonCode(phone, SmsConstants.VERIFICATION_CODE_SCENARIO_SETTLED);
    }

    /**
     * 获取日期新增商户数
     *
     * @param date 日期 yyyy-MM-dd
     * @return 新增商户数
     */
    @Override
    public Integer getNewNumByDate(String date) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.apply("date_format(create_time, '%Y-%m-%d') = {0}", date);
        return dao.selectCount(lqw);
    }

    /**
     * 获取所有的商户
     */
    @Override
    public List<Merchant> all() {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId, Merchant::getName);
        lqw.eq(Merchant::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 可用分类商户列表
     */
    @Override
    public List<CategoryMerchantResponse> getUseCategoryList() {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId, Merchant::getName, Merchant::getCategoryId);
        lqw.eq(Merchant::getIsSwitch, 1);
        lqw.eq(Merchant::getIsDel, 0);
        List<Merchant> merchantList = dao.selectList(lqw);
        List<CategoryMerchantResponse> responseList = CollUtil.newArrayList();
        if (CollUtil.isEmpty(merchantList)) {
            return responseList;
        }
        Map<Integer, MerchantCategory> categoryMap = merchantCategoryService.allMap();
        merchantList.forEach(m -> {
            if (responseList.stream().anyMatch(e -> e.getId().equals(m.getCategoryId()))) {
                responseList.forEach(response -> {
                    if (response.getId().equals(m.getCategoryId())) {
                        response.getMerchantList().add(m);
                    }
                });
            } else {
                CategoryMerchantResponse response = new CategoryMerchantResponse();
                MerchantCategory merchantCategory = categoryMap.get(m.getCategoryId());
                response.setId(merchantCategory.getId());
                response.setName(merchantCategory.getName());
                response.getMerchantList().add(m);
                responseList.add(response);
            }
        });
        return responseList;
    }

    /**
     * 获取PC商城商户首页信息
     *
     * @param merId 商户ID
     */
    @Override
    public MerchantPcIndexResponse getPcIndexByMerId(Integer merId) {
        Merchant merchant = getByIdException(merId);
        if (!merchant.getIsSwitch()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_SWITCH_CLOSE);
        }
        MerchantPcIndexResponse response = new MerchantPcIndexResponse();
        BeanUtils.copyProperties(merchant, response);
        MerchantInfo merchantInfo = merchantInfoService.getByMerId(merchant.getId());
        response.setServiceType(merchantInfo.getServiceType());
        response.setServiceLink(merchantInfo.getServiceLink());
        response.setServicePhone(merchantInfo.getServicePhone());

        List<GroupConfig> bannerConfigList = groupConfigService.findByTag(GroupConfigConstants.TAG_MERCHANT_PC_BANNER, merchant.getId(), Constants.SORT_ASC, null);
        if (CollUtil.isNotEmpty(bannerConfigList)) {
            List<GroupConfig> bannerList = bannerConfigList.stream().filter(e -> e.getStatus().equals(true)).collect(Collectors.toList());
            response.setBannerList(bannerList);
        }

        response.setIsCollect(false);
        Integer userId = userService.getUserId();
        if (userId > 0) {
            response.setIsCollect(userMerchantCollectService.isCollect(userId, merchant.getId()));
            // 商户访客量统计
            String dateStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
            redisUtil.incrAndCreate(StrUtil.format(RedisConstants.MERCHANT_VISITORS_KEY, dateStr, merchant.getId()));
        }
        return response;
    }

    /**
     * 获取商户地址信息
     *
     * @param id 商户ID
     */
    @Override
    public MerchantAddressInfoResponse getAddressInfo(Integer id) {
        Merchant merchant = getByIdException(id);
        MerchantAddressInfoResponse response = new MerchantAddressInfoResponse();
        BeanUtils.copyProperties(merchant, response);
        return response;
    }

    /**
     * 获取商户商品审核开关信息
     */
    @Override
    public ProductAuditSwitchInfoResponse getProductAuditSwitchInfo() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        Merchant merchant = getByIdException(admin.getMerId());

        ProductAuditSwitchInfoResponse response = new ProductAuditSwitchInfoResponse();
        response.setProductSwitch(merchant.getProductSwitch());
        response.setIsSwitch(merchant.getIsSwitch());
        return response;
    }

    /**
     * 根据商户分类获取商户列表
     *
     * @param CategoryId 商户分类
     * @return 对应的商户列表
     */
    @Override
    public List<Merchant> getMerchantListByType(Integer CategoryId) {
        if (ObjectUtil.isNotNull(CategoryId)) {
            LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
            lqw.select(Merchant::getId, Merchant::getName);
            lqw.eq(Merchant::getIsSwitch, true);
            lqw.eq(Merchant::getIsDel, false);
            lqw.eq(Merchant::getCategoryId, CategoryId);
            return dao.selectList(lqw);
        }
        return Collections.emptyList();
    }

    /**
     * 检查商户手机号是否重复
     *
     * @param phone 商户手机号
     * @param id    商户ID
     * @return Boolean
     */
    private Boolean checkMerchantPhone(String phone, Integer id) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getPhone, phone);
        lqw.ne(Merchant::getId, id);
        lqw.eq(Merchant::getIsDel, false);
        lqw.last(" limit 1");
        Merchant merchant = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchant);
    }

    /**
     * 检查商户名是否重复
     *
     * @param name 商户名称
     */
    private Boolean checkMerchantName(String name) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getName, name);
        lqw.eq(Merchant::getIsDel, false);
        lqw.last(" limit 1");
        Merchant merchant = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchant);
    }

    /**
     * 检查商户名是否重复
     *
     * @param name 商户名称
     * @param id   商户ID
     */
    private Boolean checkMerchantName(String name, Integer id) {
        LambdaQueryWrapper<Merchant> lqw = Wrappers.lambdaQuery();
        lqw.select(Merchant::getId);
        lqw.eq(Merchant::getName, name);
        lqw.ne(Merchant::getId, id);
        lqw.eq(Merchant::getIsDel, false);
        lqw.last(" limit 1");
        Merchant merchant = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(merchant);
    }

}

