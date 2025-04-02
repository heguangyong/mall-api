package com.zbkj.front.service;

import com.zbkj.common.request.PaySvipOrderRequest;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.common.response.SvipBenefitsExplainResponse;
import com.zbkj.common.response.SvipInfoResponse;
import com.zbkj.common.response.SvipOrderRecordResponse;

import java.util.List;

/**
 * 会员付费类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/5/14
 */
public interface MemberService {

    /**
     * 获取svip会员中心信息
     */
    SvipInfoResponse getSvipInfo();

    /**
     * 获取svip会员权益列表
     */
    List<SvipBenefitsExplainResponse> getSvipBenefitsList();

    /**
     * 生成购买svip会员订单
     */
    OrderPayResultResponse paySvipOrderCreate(PaySvipOrderRequest request);

    /**
     * svip会员订单购买记录
     */
    List<SvipOrderRecordResponse> getSvipOrderRecord();
}
