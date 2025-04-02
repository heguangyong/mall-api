package com.zbkj.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.*;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.member.PaidMemberCard;
import com.zbkj.common.model.member.PaidMemberOrder;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBalanceRecord;
import com.zbkj.common.model.user.UserToken;
import com.zbkj.common.request.PaySvipOrderRequest;
import com.zbkj.common.response.*;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MemberResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RequestUtil;
import com.zbkj.common.utils.WxPayUtil;
import com.zbkj.common.vo.*;
import com.zbkj.front.service.MemberService;
import com.zbkj.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 会员服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/14
 */
@Service
public class MemberServiceImpl implements MemberService {

    private Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private UserService userService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private PaidMemberCardService paidMemberCardService;
    @Autowired
    private PaidMemberOrderService paidMemberOrderService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private UserTokenService userTokenService;
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private UserBalanceRecordService userBalanceRecordService;

    /**
     * 获取svip会员中心信息
     */
    @Override
    public SvipInfoResponse getSvipInfo() {
        SvipInfoResponse response = new SvipInfoResponse();
        boolean isPermanentPaidMember = false;
        Integer uid = userService.getUserId();
        if (uid > 0) {
            User user = userService.getById(uid);
            isPermanentPaidMember = user.getIsPermanentPaidMember();
            response.setNickname(user.getNickname());
            response.setAvatar(user.getAvatar());
            response.setPhone(CrmebUtil.maskMobile(user.getPhone()));
            response.setIsPaidMember(user.getIsPaidMember());
            response.setIsPermanentPaidMember(user.getIsPermanentPaidMember());
            response.setPaidMemberExpirationTime(user.getPaidMemberExpirationTime());
        }
        response.setBenefitsList(getSvipInfoBenefitsList());
        if (isPermanentPaidMember) {
            response.setCardList(new ArrayList<>());
        } else {
            response.setCardList(getSvipCardList(uid));
        }
        return response;
    }

    /**
     * 获取svip会员权益列表
     */
    @Override
    public List<SvipBenefitsExplainResponse> getSvipBenefitsList() {
        List<GroupConfig> configList = getBaseSvipBenefitsList();
        List<SvipBenefitsExplainResponse> benefitsResponseList = new ArrayList<>();
        if (CollUtil.isNotEmpty(configList)) {
            Iterator<GroupConfig> iterator = configList.iterator();
            while (iterator.hasNext()) {
                GroupConfig config = iterator.next();
                SvipBenefitsExplainResponse benefitsResponse = new SvipBenefitsExplainResponse();
                benefitsResponse.setValue(config.getValue());
                benefitsResponse.setImageUrl(config.getImageUrl());
                benefitsResponse.setExpand(config.getExpand());
                benefitsResponseList.add(benefitsResponse);
            }
        }
        return benefitsResponseList;
    }

    private List<SvipCardResponse> getSvipCardList(Integer uid) {
        Boolean isPayTrial = paidMemberOrderService.userIsPayTrial(uid);
        return paidMemberCardService.findFrontList(isPayTrial);
    }

    private List<SvipBenefitsResponse> getSvipInfoBenefitsList() {
        List<GroupConfig> configList = getBaseSvipBenefitsList();
        List<SvipBenefitsResponse> benefitsResponseList = new ArrayList<>();
        if (CollUtil.isNotEmpty(configList)) {
            Iterator<GroupConfig> iterator = configList.iterator();
            while (iterator.hasNext()) {
                GroupConfig config = iterator.next();
                SvipBenefitsResponse benefitsResponse = new SvipBenefitsResponse();
                benefitsResponse.setValue(config.getValue());
                benefitsResponse.setMessage(config.getMessage());
                benefitsResponse.setImageUrl(config.getImageUrl());
                benefitsResponseList.add(benefitsResponse);
            }
        }
        return benefitsResponseList;
    }

    private List<GroupConfig> getBaseSvipBenefitsList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_PAID_MEMBER_BENEFITS, Constants.SORT_DESC, null);
        for (int i = 0; i < configList.size(); ) {
            GroupConfig groupConfig = configList.get(i);
            if (!groupConfig.getStatus()) {
                configList.remove(i);
                continue;
            }
            i++;
        }
        return configList;
    }

    /**
     * 生成购买svip会员订单
     *
     */
    @Override
    public OrderPayResultResponse paySvipOrderCreate(PaySvipOrderRequest request) {
        User user = userService.getInfo();
        if (user.getIsPermanentPaidMember()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户已经是永久会员");
        }
        PaidMemberCard card = paidMemberCardService.getByIdException(request.getCardId());
        if (!card.getStatus()) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_CARD_NOT_EXIST);
        }
        // 试用会员卡只能购买一次
        if (card.getType().equals(0) && paidMemberOrderService.userIsPayTrial(user.getId())) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_CARD_NOT_EXIST);
        }
        PaidMemberOrder order = new PaidMemberOrder();
        order.setUid(user.getId());
        order.setOrderNo(CrmebUtil.getOrderNo(OrderConstants.PAID_MEMBER_ORDER_PREFIX));
        order.setCardId(card.getId());
        order.setCardName(card.getName());
        order.setType(card.getType());
        order.setDeadlineDay(card.getDeadlineDay());
        order.setOriginalPrice(card.getOriginalPrice());
        order.setPrice(card.getPrice());
        order.setGiftBalance(BigDecimal.ZERO);
        if (card.getGiftBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (card.getIsFirstChargeGive()) {
                    if (!paidMemberOrderService.userIsFirstPay(user.getId(), card.getId())) {
                        order.setGiftBalance(card.getGiftBalance());
                    }
            } else {
                order.setGiftBalance(card.getGiftBalance());
            }
        }
        order.setPayType(request.getPayType());
        order.setPayChannel(request.getPayChannel());
        order.setPaid(false);
        OrderPayResultResponse response = new OrderPayResultResponse();
        response.setPayChannel(request.getPayChannel());
        response.setPayType(request.getPayType());
        response.setOrderNo(order.getOrderNo());
        if (order.getPayType().equals(PayConstants.PAY_TYPE_YUE)) {
            if (user.getNowMoney().compareTo(order.getPrice()) < 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "用户余额不足");
            }
            boolean save = paidMemberOrderService.save(order);
            if (!save) {
                throw new CrmebException("生成svip订单失败!");
            }
            userYuePay(order, user.getId(), user.getNowMoney());
            Boolean yueBoolean = paidMemberOrderService.paySuccessAfter(order);
            if (!yueBoolean) {
                userService.updateNowMoney(user.getId(), order.getPrice(), Constants.OPERATION_TYPE_ADD);
            }
            response.setStatus(yueBoolean);
            return response;
        }
        if (request.getPayType().equals(PayConstants.PAY_TYPE_WE_CHAT)) {
            MyRecord record = wechatPayment(order.getPrice(), request.getPayChannel(), user.getId());
            WxPayJsResultVo vo = record.get("vo");
            String outTradeNo = record.getStr("outTradeNo");
            response.setJsConfig(vo);
            order.setOutTradeNo(outTradeNo);
        }
        if (request.getPayType().equals(PayConstants.PAY_TYPE_ALI_PAY)) {
            String result = aliPayService.pay(order.getOrderNo(), order.getPrice(), "svip", request.getPayChannel(), "");
            response.setAlipayRequest(result);
            order.setOutTradeNo(order.getOrderNo());
        }
        boolean save = paidMemberOrderService.save(order);
        if (!save) {
            throw new CrmebException("生成svip订单失败!");
        }
        return response;
    }

    private void userYuePay(PaidMemberOrder svipOrder, Integer userId, BigDecimal nowMoney) {
        Boolean yueBoolean = userService.updateNowMoney(userId, svipOrder.getPrice(), Constants.OPERATION_TYPE_SUBTRACT);
        if (!yueBoolean) {
            throw new CrmebException("用户余额扣除数据操作异常");
        }
        // 创建记录
        userBalanceRecordService.save(createBalanceRecord(svipOrder, nowMoney));
    }

    private UserBalanceRecord createBalanceRecord(PaidMemberOrder svipOrder, BigDecimal nowMoney) {
        UserBalanceRecord record = new UserBalanceRecord();
        record.setUid(svipOrder.getUid());
        record.setLinkId(svipOrder.getOrderNo());
        record.setLinkType(BalanceRecordConstants.BALANCE_RECORD_LINK_TYPE_SVIP);
        record.setType(BalanceRecordConstants.BALANCE_RECORD_TYPE_SUB);
        record.setAmount(svipOrder.getPrice());
        record.setBalance(nowMoney.subtract(svipOrder.getPrice()));
        record.setRemark(StrUtil.format(BalanceRecordConstants.BALANCE_RECORD_REMARK_SVIP_ORDER, svipOrder.getPrice()));
        return record;
    }

    /**
     * svip会员订单购买记录
     */
    @Override
    public List<SvipOrderRecordResponse> getSvipOrderRecord() {
        Integer userId = userService.getUserIdException();
        List<PaidMemberOrder> recordList = paidMemberOrderService.findUserSvipOrderRecord(userId);
        List<SvipOrderRecordResponse> responseList = new ArrayList<>();
        for (PaidMemberOrder record : recordList) {
            SvipOrderRecordResponse response = new SvipOrderRecordResponse();
            BeanUtils.copyProperties(record, response);
            responseList.add(response);
        }
        return responseList;
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
    private CreateOrderRequestVo getUnifiedorderVo(BigDecimal price, Integer uid, String openid, String appId, String mchId, String signKey, String payChannel) {
        // 获取域名
        String domain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_SITE_URL);
        String apiDomain = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_KEY_API_URL);

        AttachVo attachVo = new AttachVo(PayConstants.PAY_SERVICE_TYPE_SVIP, uid);

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
        vo.setTotal_fee(price.multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
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
}
