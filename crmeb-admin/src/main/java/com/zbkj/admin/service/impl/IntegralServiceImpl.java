package com.zbkj.admin.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.IntegralService;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.request.IntegralPageSearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.IntegralConfigResponse;
import com.zbkj.common.response.IntegralRecordPageResponse;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.service.UserIntegralRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 积分服务实现类
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
public class IntegralServiceImpl implements IntegralService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;

    /**
     * 获取积分配置
     *
     * @return IntegralConfigResponse
     */
    @Override
    public IntegralConfigResponse getConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH);
        keyList.add(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY);
        keyList.add(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY);
        keyList.add(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO);
        keyList.add(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE);
        keyList.add(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME);
        keyList.add(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE);
        MyRecord record = systemConfigService.getValuesByKeyList(keyList);
        IntegralConfigResponse configResponse = new IntegralConfigResponse();
        configResponse.setIntegralDeductionSwitch(Boolean.valueOf(record.getStr(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH)));
        configResponse.setIntegralDeductionStartMoney(Integer.valueOf(record.getStr(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY)));
        configResponse.setIntegralDeductionMoney(new BigDecimal(record.getStr(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY)));
        configResponse.setIntegralDeductionRatio(Integer.valueOf(record.getStr(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO)));
        configResponse.setOrderGiveIntegral(Integer.valueOf(record.getStr(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE)));
        configResponse.setFreezeIntegralDay(Integer.valueOf(record.getStr(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME)));
        configResponse.setIntegralFreezeNode(record.getStr(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE));
        return configResponse;
    }

    /**
     * 编辑积分配置
     *
     * @param request 积分配置请求对象
     * @return Boolean
     */
    @Override
    public Boolean updateConfig(IntegralConfigResponse request) {
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_SWITCH,
                    request.getIntegralDeductionSwitch().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_START_MONEY,
                    request.getIntegralDeductionStartMoney().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_MONEY,
                    request.getIntegralDeductionMoney().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_DEDUCTION_RATIO,
                    request.getIntegralDeductionRatio().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_INTEGRAL_RATE_ORDER_GIVE,
                    request.getOrderGiveIntegral().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME,
                    request.getFreezeIntegralDay().toString());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_KEY_STORE_INTEGRAL_FREEZE_NODE,
                    request.getIntegralFreezeNode());
            return Boolean.TRUE;
        });
    }

    /**
     * 积分记录分页列表
     *
     * @param request     搜索参数
     */
    @Override
    public PageInfo<IntegralRecordPageResponse> findRecordPageList(IntegralPageSearchRequest request) {
        return userIntegralRecordService.findRecordPageListByPlat(request);
    }
}
