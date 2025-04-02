package com.zbkj.admin.service.impl;

import com.zbkj.admin.service.CommunityService;
import com.zbkj.common.constants.CommunityConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.CommunityConfigVo;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @ClassName CommunityServiceImpl
 * @Description 社区服务实现类
 * @Author HZW
 * @Date 2023/3/9 14:39
 * @Version 1.0
 */
@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 获取社区配置
     */
    @Override
    public CommunityConfigVo getConfig() {
        CommunityConfigVo configVo = new CommunityConfigVo();
        configVo.setCommunityImageTextAuditSwitch(systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_IMAGE_TEXT_AUDIT_SWITCH));
        configVo.setCommunityShortVideoAuditSwitch(systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_SHORT_VIDEO_AUDIT_SWITCH));
        configVo.setCommunityReplySwitch(systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_REPLY_SWITCH));
        configVo.setCommunityReplyAuditSwitch(systemConfigService.getValueByKeyException(CommunityConstants.COMMUNITY_REPLY_AUDIT_SWITCH));
        return configVo;
    }

    /***
     * 更新社区配置
     * @Author HZW
     * @Date 2023/3/9 14:40
     */
    @Override
    public void updateConfig(CommunityConfigVo configVo) {
        Boolean execute = transactionTemplate.execute(e -> {
            systemConfigService.updateOrSaveValueByName(CommunityConstants.COMMUNITY_IMAGE_TEXT_AUDIT_SWITCH, configVo.getCommunityImageTextAuditSwitch());
            systemConfigService.updateOrSaveValueByName(CommunityConstants.COMMUNITY_SHORT_VIDEO_AUDIT_SWITCH, configVo.getCommunityShortVideoAuditSwitch());
            systemConfigService.updateOrSaveValueByName(CommunityConstants.COMMUNITY_REPLY_AUDIT_SWITCH, configVo.getCommunityReplyAuditSwitch());
            systemConfigService.updateOrSaveValueByName(CommunityConstants.COMMUNITY_REPLY_SWITCH, configVo.getCommunityReplySwitch());
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException(CommonResultCode.ERROR.setMessage("更新社区配置失败"));
        }
    }
}
