package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.request.PaidMemberBenefitsStatementRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.PaidMemberBenefitsVo;
import com.zbkj.common.vo.PaidMemberConfigVo;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CdkeyLibraryServiceImpl 接口实现
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
public class PaidMemberServiceImpl implements PaidMemberService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private UserService userService;

    /**
     * 付费会员配置信息获取
     */
    @Override
    public PaidMemberConfigVo getBaseConfig() {
        List<String> keyList = new ArrayList<>();
        keyList.add(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE);
        keyList.add(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY);
        keyList.add(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH);
        MyRecord myRecord = systemConfigService.getValuesByKeyList(keyList);
        PaidMemberConfigVo vo = new PaidMemberConfigVo();
        vo.setPaidMemberPaidEntrance(myRecord.getStr(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE));
        vo.setPaidMemberPriceDisplay(myRecord.getStr(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY));
        vo.setPaidMemberProductSwitch(myRecord.getStr(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH));
        return vo;
    }

    /**
     * 编辑付费会员基础配置
     */
    @Override
    public Boolean editBaseConfig(PaidMemberConfigVo voRequest) {
        return transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_PAID_MEMBER_PAID_ENTRANCE, voRequest.getPaidMemberPaidEntrance());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_PAID_MEMBER_PRICE_DISPLAY, voRequest.getPaidMemberPriceDisplay());
            systemConfigService.updateOrSaveValueByName(SysConfigConstants.CONFIG_PAID_MEMBER_PRODUCT_SWITCH, voRequest.getPaidMemberProductSwitch());
            return Boolean.TRUE;
        });
    }

    /**
     * 获取付费会员会员权益
     */
    @Override
    public List<PaidMemberBenefitsVo> getBenefitsList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_PAID_MEMBER_BENEFITS, Constants.SORT_DESC, null);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        Iterator<GroupConfig> iterator = configList.iterator();
        List<PaidMemberBenefitsVo> voList = new ArrayList<>();
        while (iterator.hasNext()) {
            GroupConfig config = iterator.next();
            PaidMemberBenefitsVo vo = new PaidMemberBenefitsVo();
            BeanUtils.copyProperties(config, vo);
            int multiple = 1;
            String channelStr = "";
            if (StrUtil.isNotBlank(config.getLinkUrl())) {
                StrUtil.split(config.getLinkUrl(), ":");
                String[] split = config.getLinkUrl().split(":");
                String multipleStr = split[0];
                multiple = Integer.parseInt(multipleStr);
                channelStr = split[1];
                if (channelStr.equals("0")) {
                    channelStr = "";
                }
            }
            vo.setMultiple(multiple);
            vo.setChannelStr(channelStr);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 编辑付费会员会员权益
     */
    @Override
    public Boolean editBenefits(PaidMemberBenefitsVo voRequest) {
        GroupConfig groupConfig = getBenefitsGroupConfigById(voRequest.getId());
        if (groupConfig.getName().equals("integralDoubling") || groupConfig.getName().equals("experienceDoubling")) {
            if (ObjectUtil.isNull(voRequest.getMultiple()) || voRequest.getMultiple() < 1 || voRequest.getMultiple() > 99) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "倍数范围为：1~99");
            }
            String channelStr = StrUtil.isBlank(voRequest.getChannelStr()) ? "0" : voRequest.getChannelStr();
            String linkUrl = voRequest.getMultiple() + ":" + channelStr;
            groupConfig.setLinkUrl(linkUrl);
        }
        groupConfig.setImageUrl(systemAttachmentService.clearPrefix(voRequest.getImageUrl()));
        groupConfig.setValue(voRequest.getValue());
        groupConfig.setMessage(voRequest.getMessage());
        groupConfig.setStatus(voRequest.getStatus());
        groupConfig.setSort(voRequest.getSort());
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * 付费会员会员权益开关
     */
    @Override
    public Boolean editBenefitsSwitch(Integer id) {
        GroupConfig groupConfig = getBenefitsGroupConfigById(id);
        groupConfig.setStatus(!groupConfig.getStatus());
        return groupConfigService.updateById(groupConfig);
    }

    /**
     * 编辑付费会员会员权益说明
     */
    @Override
    public Boolean editBenefitsStatement(PaidMemberBenefitsStatementRequest request) {
        GroupConfig groupConfig = getBenefitsGroupConfigById(request.getId());
        String expand = StrUtil.isNotBlank(request.getExpand()) ? request.getExpand() : "";
        groupConfig.setExpand(systemAttachmentService.clearPrefix(expand));
        return groupConfigService.updateById(groupConfig);
    }

    private GroupConfig getBenefitsGroupConfigById(Integer id) {
        GroupConfig groupConfig = groupConfigService.getByIdException(id);
        if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_PAID_MEMBER_BENEFITS)) {
            throw new CrmebException("数据不存在");
        }
        if (!groupConfig.getName().equals("memberExclusivePrice")
                && !groupConfig.getName().equals("integralDoubling")
                && !groupConfig.getName().equals("experienceDoubling")
                && !groupConfig.getName().equals("exclusiveCustomer")) {
            throw new CrmebException("数据不存在");
        }
        return groupConfig;
    }


    /**
     * 会员过期处理
     */
    @Override
    public void memberExpirationProcessing() {
        Boolean update = userService.memberExpirationProcessing();
        if (!update) {
            throw new CrmebException("会员过期处理失败");
        }
    }
}


