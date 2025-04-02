package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.WechatUrlLinkRecord;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.WechatGenerateUrlLinkRequest;

/**
 * WechatUrlLinkRecordService 接口
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
public interface WechatUrlLinkRecordService extends IService<WechatUrlLinkRecord> {

    /**
     * 小程序生成url链接
     * @param request 请求参数
     * @return 生成的url链接地址
     */
    String miniGenerateUrlLink(WechatGenerateUrlLinkRequest request);

    /**
     * 微信小程序url链接分页列表
     */
    PageInfo<WechatUrlLinkRecord> findMiniRecordPageList(CommonSearchRequest request);

    /**
     * 删除微信小程序加密URLLink记录
     */
    Boolean deleteMiniUrlLinkRecord(Integer id);
}
