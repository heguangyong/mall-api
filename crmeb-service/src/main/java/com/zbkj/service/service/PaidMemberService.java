package com.zbkj.service.service;

import com.zbkj.common.request.PaidMemberBenefitsStatementRequest;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.common.vo.PaidMemberConfigVo;

import java.util.List;

/**
* PaidMemberService 接口
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
public interface PaidMemberService {

    /**
     * 付费会员配置信息获取
     */
    PaidMemberConfigVo getBaseConfig();

    /**
     * 编辑付费会员基础配置
     */
    Boolean editBaseConfig(PaidMemberConfigVo voRequest);

    /**
     * 获取付费会员会员权益
     */
    List<PaidMemberBenefitsVo> getBenefitsList();

    /**
     * 编辑付费会员会员权益
     */
    Boolean editBenefits(PaidMemberBenefitsVo voRequest);

    /**
     * 付费会员会员权益开关
     */
    Boolean editBenefitsSwitch(Integer id);

    /**
     * 编辑付费会员会员权益说明
     */
    Boolean editBenefitsStatement(PaidMemberBenefitsStatementRequest request);

    /**
     * 会员过期处理
     */
    void memberExpirationProcessing();
}