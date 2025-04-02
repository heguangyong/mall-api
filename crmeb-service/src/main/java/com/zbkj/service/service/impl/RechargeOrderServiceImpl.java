package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.bill.Bill;
import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.RechargeOrderSearchRequest;
import com.zbkj.common.request.UserRechargeRequest;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.RechargePackageResponse;
import com.zbkj.common.response.UserRechargeItemResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.*;
import com.zbkj.common.vo.*;
import com.zbkj.service.dao.RechargeOrderDao;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RechargeOrderServiceImpl 接口实现
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
public class RechargeOrderServiceImpl extends ServiceImpl<RechargeOrderDao, RechargeOrder> implements RechargeOrderService {

    @Resource
    private RechargeOrderDao dao;

    private static final Logger logger = LoggerFactory.getLogger(RechargeOrderServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;
    @Autowired
    private BillService billService;
    @Autowired
    private AsyncService asyncService;
    @Autowired
    private AliPayService aliPayService;

    /**
     * 列表
     *
     * @param request          请求参数
     * @return List<RechargeOrder>
     */
    @Override
    public PageInfo<RechargeOrder> getAdminPage(RechargeOrderSearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
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
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
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
        if (StrUtil.isNotBlank(request.getKeywords())) {
            String orderNo = URLUtil.decode(request.getKeywords());
            map.put("orderNo", orderNo);
        }

        Page<RechargeOrder> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<RechargeOrder> rechargeOrderList = dao.getAdminPage(map);
        return CommonPage.copyPageInfo(page, rechargeOrderList);
    }

    /**
     * 充值额度选择
     *
     * @return UserRechargeResponse
     */
    @Override
    public RechargePackageResponse getRechargePackage() {
        RechargePackageResponse userRechargeResponse = new RechargePackageResponse();
        userRechargeResponse.setPackageList(systemGroupDataService.getListByGid(GroupDataConstants.GROUP_DATA_ID_RECHARGE_PACKAGE, UserRechargeItemResponse.class));
        String rechargeAttention = systemConfigService.getValueByKey(SysConfigConstants.CONFIG_RECHARGE_ATTENTION);
        List<String> rechargeAttentionList = new ArrayList<>();
        if (StrUtil.isNotBlank(rechargeAttention)) {
            rechargeAttentionList = CrmebUtil.stringToArrayStrRegex(rechargeAttention, "\n");
        }
        userRechargeResponse.setNoticeList(rechargeAttentionList);
        return userRechargeResponse;
    }

    /**
     * 创建用户充值订单
     *
     * @param request 用户下单参数
     */
    @Override
    public OrderPayResultResponse userRechargeOrderCreate(UserRechargeRequest request) {
        User user = userService.getInfo();
        if (ObjectUtil.isNull(request.getPrice()) && ObjectUtil.isNull(request.getGroupDataId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择充值套餐或填写自定义充值金额");
        }
        BigDecimal rechargePrice = BigDecimal.ZERO;
        BigDecimal gainPrice = BigDecimal.ZERO;
        String rechargeMinAmountStr = systemConfigService.getValueByKey(SysConfigConstants.USER_RECHARGE_MIN_AMOUNT);
        BigDecimal rechargeMinAmount = StrUtil.isBlank(rechargeMinAmountStr) ? BigDecimal.ZERO : new BigDecimal(rechargeMinAmountStr);
        if (ObjectUtil.isNotNull(request.getPrice())) {
            if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "充值金额必须大于0");
            }
            rechargePrice = request.getPrice();
        } else {
            UserRechargeItemResponse rechargePackage = systemGroupDataService.getNormalInfo(request.getGroupDataId(), UserRechargeItemResponse.class);
            if (ObjectUtil.isNull(rechargePackage)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "您选择的充值方式已下架");
            }
            //售价和赠送
            rechargePrice = new BigDecimal(rechargePackage.getPrice());
            gainPrice = new BigDecimal(rechargePackage.getGiveMoney());
        }
        if (rechargePrice.compareTo(rechargeMinAmount) < 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "充值金额小于最低充值金额");
        }
        String rechargeNo = CrmebUtil.getOrderNo(OrderConstants.RECHARGE_ORDER_PREFIX);
        OrderPayResultResponse response = new OrderPayResultResponse();
        RechargeOrder rechargeOrder = new RechargeOrder();
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            MyRecord record = wechatPayment(rechargePrice, request.getPayChannel(), user.getId());
            WxPayJsResultVo vo = record.get("vo");
            String outTradeNo = record.getStr("outTradeNo");
            response.setJsConfig(vo);
            rechargeOrder.setOutTradeNo(outTradeNo);
        }
        if (request.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            String result = aliPayService.pay(rechargeNo, rechargePrice, "recharge", request.getPayChannel(), "");
            response.setAlipayRequest(result);
            rechargeOrder.setOutTradeNo(rechargeNo);
        }
        rechargeOrder.setUid(user.getId());
        rechargeOrder.setOrderNo(rechargeNo);
        rechargeOrder.setPrice(rechargePrice);
        rechargeOrder.setGivePrice(gainPrice);
        rechargeOrder.setPayType(request.getPayType());
        rechargeOrder.setPayChannel(request.getPayChannel());
        boolean save = save(rechargeOrder);
        if (!save) {
            throw new CrmebException("生成充值订单失败!");
        }
        return response;
    }

    /**
     * 微信支付
     *
     * @param rechargePrice 充值金额
     * @param payChannel    支付渠道
     * @param uid           用户id
     * @return
     */
    private MyRecord wechatPayment(BigDecimal rechargePrice, String payChannel, Integer uid) {
        // 预下单
        Map<String, String> unifiedorder = wechatUnifiedorder(rechargePrice, payChannel, uid);
        WxPayJsResultVo vo = new WxPayJsResultVo();
        vo.setAppId(unifiedorder.get("appId"));
        vo.setNonceStr(unifiedorder.get("nonceStr"));
        vo.setPackages(unifiedorder.get("package"));
        vo.setSignType(unifiedorder.get("signType"));
        vo.setTimeStamp(unifiedorder.get("timeStamp"));
        vo.setPaySign(unifiedorder.get("paySign"));
        if (payChannel.equals(PayConstants.PAY_CHANNEL_H5)) {
            vo.setMwebUrl(unifiedorder.get("mweb_url"));
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_IOS) ||
                payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID)) {// App
            vo.setPartnerid(unifiedorder.get("partnerid"));
        }
        MyRecord record = new MyRecord();
        record.set("vo", vo);
        record.set("outTradeNo", unifiedorder.get("outTradeNo"));
        return record;
    }

    /**
     * 微信预下单
     *
     * @param rechargePrice 充值金额
     * @param payChannel    支付渠道
     * @return 预下单返回对象
     */
    private Map<String, String> wechatUnifiedorder(BigDecimal rechargePrice, String payChannel, Integer uid) {
        // 获取用户openId
        // 根据订单支付类型来判断获取公众号openId还是小程序openId
        UserToken userToken = new UserToken();
        userToken.setToken("");
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_PUBLIC)) {// 公众号
            userToken = userTokenService.getTokenByUserId(uid, UserConstants.USER_TOKEN_TYPE_WECHAT);
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_MINI)) {// 小程序
            userToken = userTokenService.getTokenByUserId(uid, UserConstants.USER_TOKEN_TYPE_ROUTINE);
        }

        // 获取appid、mch_id、微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        String tempPayChannel = payChannel;
        if (tempPayChannel.equals(PayConstants.PAY_CHANNEL_H5) || tempPayChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_NATIVE)) {
            String source = systemConfigService.getValueByKey(SysConfigConstants.WECHAT_PAY_SOURCE_H5_PC);
            if (StrUtil.isNotBlank(source) && source.equals(PayConstants.WECHAT_PAY_SOURCE_MINI)) {
                tempPayChannel = PayConstants.PAY_CHANNEL_WECHAT_MINI;
            } else {
                tempPayChannel = PayConstants.PAY_CHANNEL_WECHAT_PUBLIC;
            }
        }
        switch (tempPayChannel) {
            case PayConstants.PAY_CHANNEL_WECHAT_PUBLIC:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PUBLIC_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_PUBLIC_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_MINI:
            case PayConstants.PAY_CHANNEL_WECHAT_MINI_VIDEO:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_MINI_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_MINI_KEY);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                appId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_APP_APPID);
                mchId = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_MCHID);
                signKey = systemConfigService.getValueByKeyException(WeChatConstants.WECHAT_PAY_APP_KEY);
                break;
        }
        // 获取微信预下单对象
        CreateOrderRequestVo unifiedorderVo = getUnifiedorderVo(rechargePrice, uid, userToken.getToken(), appId, mchId, signKey, payChannel);
        // 预下单
        CreateOrderResponseVo responseVo = wechatService.payUnifiedorder(unifiedorderVo);
        // 组装前端预下单参数
        Map<String, String> map = new HashMap<>();
        map.put("appId", unifiedorderVo.getAppid());
        map.put("nonceStr", unifiedorderVo.getNonce_str());
        map.put("package", "prepay_id=".concat(responseVo.getPrepayId()));
        map.put("signType", unifiedorderVo.getSign_type());
        Long currentTimestamp = WxPayUtil.getCurrentTimestamp();
        map.put("timeStamp", Long.toString(currentTimestamp));
        String paySign = WxPayUtil.getSign(map, signKey);
        map.put("paySign", paySign);
        map.put("prepayId", responseVo.getPrepayId());
        map.put("prepayTime", CrmebDateUtil.nowDateTimeStr());
        map.put("outTradeNo", unifiedorderVo.getOut_trade_no());
        if (payChannel.equals(PayConstants.PAY_CHANNEL_H5)) {
            map.put("mweb_url", responseVo.getMWebUrl());
        }
        if (payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_IOS) ||
                payChannel.equals(PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID)) {// App
            map.put("partnerid", mchId);
            map.put("package", responseVo.getPrepayId());
            Map<String, Object> appMap = new HashMap<>();
            appMap.put("appid", unifiedorderVo.getAppid());
            appMap.put("partnerid", mchId);
            appMap.put("prepayid", responseVo.getPrepayId());
            appMap.put("package", "Sign=WXPay");
            appMap.put("noncestr", unifiedorderVo.getNonce_str());
            appMap.put("timestamp", currentTimestamp);
            String sign = WxPayUtil.getSignObject(appMap, signKey);
            map.put("paySign", sign);
        }
        return map;
    }

    /**
     * 获取微信预下单对象
     */
    private CreateOrderRequestVo getUnifiedorderVo(BigDecimal rechargePrice, Integer uid, String openid, String appId, String mchId, String signKey, String payChannel) {
        // 获取域名
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(PayConstants.PAY_SERVICE_TYPE_RECHARGE, uid);

        CreateOrderRequestVo vo = new CreateOrderRequestVo();
        vo.setAppid(appId);
        vo.setMch_id(mchId);
        vo.setNonce_str(WxPayUtil.getNonceStr());
        vo.setSign_type(PayConstants.WX_PAY_SIGN_TYPE_MD5);
        String siteName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME);
        // 因商品名称在微信侧超长更换为网站名称
        vo.setBody(siteName);
        vo.setAttach(JSONObject.toJSONString(attachVo));
        vo.setOut_trade_no(CrmebUtil.getOrderNo(OrderConstants.ORDER_PREFIX_WECHAT));
        // 订单中使用的是BigDecimal,这里要转为Integer类型
        vo.setTotal_fee(rechargePrice.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        vo.setSpbill_create_ip(RequestUtil.getClientIp());
        vo.setNotify_url(apiDomain + PayConstants.WX_PAY_NOTIFY_API_URI);
        switch (payChannel) {
            case PayConstants.PAY_CHANNEL_H5:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_H5);
                vo.setOpenid(null);
                break;
            case PayConstants.PAY_CHANNEL_WECHAT_APP_IOS:
            case PayConstants.PAY_CHANNEL_WECHAT_APP_ANDROID:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_APP);
                vo.setOpenid(null);
                break;
            default:
                vo.setTrade_type(PayConstants.WX_PAY_TRADE_TYPE_JS);
                vo.setOpenid(openid);
        }
        CreateOrderH5SceneInfoVo createOrderH5SceneInfoVo = new CreateOrderH5SceneInfoVo(
                new CreateOrderH5SceneInfoDetailVo(
                        domain,
                        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_NAME)
                )
        );
        vo.setScene_info(JSONObject.toJSONString(createOrderH5SceneInfoVo));
        String sign = WxPayUtil.getSign(vo, signKey);
        vo.setSign(sign);
        return vo;
    }

    /**
     * 获取订单
     *
     * @param outTradeNo 商户系统内部的订单号
     */
    @Override
    public RechargeOrder getByOutTradeNo(String outTradeNo) {
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getOutTradeNo, outTradeNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 支付成功后置处理
     *
     * @param rechargeOrder 支付订单
     */
    @Override
    public Boolean paySuccessAfter(RechargeOrder rechargeOrder) {
        User user = userService.getById(rechargeOrder.getUid());

        BigDecimal addPrice = rechargeOrder.getPrice().add(rechargeOrder.getGivePrice());
        BigDecimal balance = user.getNowMoney().add(addPrice);
        // 余额变动对象
        UserBalanceRecord record = new UserBalanceRecord();
        record.setUid(rechargeOrder.getUid());
        record.setLinkId(rechargeOrder.getOrderNo());
        record.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_RECHARGE);
        record.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_ADD);
        record.setAmount(addPrice);
        record.setBalance(balance);
        record.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_RECHARGE, addPrice));

        Bill bill = new Bill();
        bill.setOrderNo(rechargeOrder.getOrderNo());
        bill.setUid(rechargeOrder.getUid());
        bill.setPm(BillConstants.BILL_PM_ADD);
        bill.setAmount(rechargeOrder.getPrice());
        bill.setType(BillConstants.BILL_TYPE_RECHARGE_USER);
        bill.setMark(StrUtil.format("充值订单，用户充值金额{}元", rechargeOrder.getPrice()));
        Boolean execute = transactionTemplate.execute(e -> {
            // 订单变动
            boolean updatePaid = updatePaid(rechargeOrder.getId(), rechargeOrder.getOrderNo());
            if (!updatePaid) {
                logger.error("充值订单更新支付状态失败，orderNo = {}", rechargeOrder.getOrderNo());
                e.setRollbackOnly();
                return false;
            }
            // 余额变动
            userService.updateNowMoney(user.getId(), addPrice, Constants.OPERATION_TYPE_ADD);
            // 创建记录
            userBalanceRecordService.save(record);
            billService.save(bill);
            return Boolean.TRUE;
        });
        if (execute) {
            // 发送充值成功通知
            asyncService.sendRechargeSuccessNotification(rechargeOrder, user);
        }
        return execute;
    }

    /**
     * 支付完成变动
     */
    private boolean updatePaid(Integer id, String orderNo) {
        LambdaUpdateWrapper<RechargeOrder> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(RechargeOrder::getPaid, true);
        wrapper.set(RechargeOrder::getPayTime, CrmebDateUtil.nowDateTime());
        wrapper.eq(RechargeOrder::getId, id);
        wrapper.eq(RechargeOrder::getOrderNo, orderNo);
        wrapper.eq(RechargeOrder::getPaid, false);
        return update(wrapper);
    }

    /**
     * 获取某一天的充值记录
     *
     * @param date 日期 yyyy-MM-dd
     * @return 充值记录
     */
    @Override
    public List<RechargeOrder> findByDate(String date) {
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getPaid, true);
        lqw.apply("date_format(pay_time, '%Y-%m-%d') = {0}", date);
        return dao.selectList(lqw);
    }

    /**
     * 获取充值订单
     * @param orderNo 充值单号
     * @return 充值订单
     */
    @Override
    public RechargeOrder getByOrderNo(String orderNo) {
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getOrderNo, orderNo);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取待上传微信发货管理订单
     */
    @Override
    public List<RechargeOrder> findAwaitUploadWechatList() {
        DateTime date = DateUtil.date();
        DateTime offsetMinute = DateUtil.offsetMinute(date, -10);
        LambdaQueryWrapper<RechargeOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(RechargeOrder::getPaid, 1);
        lqw.eq(RechargeOrder::getIsWechatShipping, 0);
        lqw.eq(RechargeOrder::getPayType, PayConstants.PAY_TYPE_WE_CHAT);
        lqw.eq(RechargeOrder::getPayChannel, PayConstants.PAY_CHANNEL_WECHAT_MINI);
        lqw.le(RechargeOrder::getPayTime, offsetMinute);
        return dao.selectList(lqw);
    }
}

