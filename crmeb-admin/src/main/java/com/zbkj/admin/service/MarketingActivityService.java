package com.zbkj.admin.service;

import com.zbkj.common.request.BirthdayPresentRequest;
import com.zbkj.common.request.NewPeoplePresentRequest;
import com.zbkj.common.response.BirthdayPresentResponse;
import com.zbkj.common.response.NewPeoplePresentResponse;

/**
 * 营销活动service
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
public interface MarketingActivityService {

    /**
     * 获取生日有礼配置
     */
    BirthdayPresentResponse getBirthdayPresentConfig();

    /**
     * 编辑生日有礼配置
     * @param request 请求参数
     */
    Boolean editBirthdayPresent(BirthdayPresentRequest request);

    /**
     * 获取新人礼配置
     */
    NewPeoplePresentResponse getNewPeopleConfig();

    /**
     * 编辑新人礼配置
     * @param request 请求参数
     */
    Boolean editNewPeopleConfig(NewPeoplePresentRequest request);

    /**
     * 发送生日有礼
     */
    void sendBirthdayPresent();
}
