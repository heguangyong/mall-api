package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.wechat.WechatUrlLinkRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CommonSearchRequest;
import com.zbkj.common.request.WechatGenerateUrlLinkRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.WechatUrlLinkRecordDao;
import com.zbkj.service.service.WechatService;
import com.zbkj.service.service.WechatUrlLinkRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WechatPayInfoServiceImpl 接口实现
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
public class WechatUrlLinkRecordServiceImpl extends ServiceImpl<WechatUrlLinkRecordDao, WechatUrlLinkRecord> implements WechatUrlLinkRecordService {

    private static final Logger logger = LoggerFactory.getLogger(WechatUrlLinkRecordServiceImpl.class);

    @Resource
    private WechatUrlLinkRecordDao dao;

    @Autowired
    private WechatService wechatService;

    /**
     * 小程序生成url链接
     *
     * @param request 请求参数
     * @return 生成的url链接地址
     */
    @Override
    public String miniGenerateUrlLink(WechatGenerateUrlLinkRequest request) {
        String name = request.getName();
        if (isExistName(request.getName())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "链接名称重复,请重新填写");
        }
        String originalPath = request.getOriginalPath();
        Integer expireInterval = request.getExpireInterval();
        String path;
        String query = "";
        if (originalPath.contains("?")) {
            String[] split = originalPath.split("\\?", 2);
            path = split[0];
            query = split[1];
        } else {
            path = originalPath;
        }

        String urlLink = wechatService.generateMiniUrlLink(path, query, expireInterval, "release");

        WechatUrlLinkRecord urlLinkRecord = new WechatUrlLinkRecord();
        urlLinkRecord.setName(name);
        urlLinkRecord.setOriginalPath(request.getOriginalPath());
        urlLinkRecord.setPath(path);
        urlLinkRecord.setQuery(query);
        urlLinkRecord.setExpireInterval(expireInterval);
        urlLinkRecord.setUrlLink(urlLink);
        urlLinkRecord.setCreateId(SecurityUtil.getLoginUserVo().getUser().getId());
        boolean save = save(urlLinkRecord);
        if (!save) {
            logger.error("微信小程序生成加密URLLink，保存记录异常");
        }
        return urlLink;
    }

    private Boolean isExistName(String name) {
        LambdaQueryWrapper<WechatUrlLinkRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(WechatUrlLinkRecord::getName, name);
        Integer count = dao.selectCount(lqw);
        return count >= 1;
    }

    /**
     * 微信小程序url链接分页列表
     */
    @Override
    public PageInfo<WechatUrlLinkRecord> findMiniRecordPageList(CommonSearchRequest request) {
        Page<WechatUrlLinkRecord> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<WechatUrlLinkRecord> lqw = Wrappers.lambdaQuery();
        lqw.select(WechatUrlLinkRecord::getId, WechatUrlLinkRecord::getName, WechatUrlLinkRecord::getOriginalPath,
                WechatUrlLinkRecord::getUrlLink, WechatUrlLinkRecord::getExpireInterval, WechatUrlLinkRecord::getCreateTime);
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.like(WechatUrlLinkRecord::getName, URLUtil.decode(request.getKeywords()));
        }
        lqw.eq(WechatUrlLinkRecord::getIsDel, 0);
        lqw.orderByDesc(WechatUrlLinkRecord::getId);
        List<WechatUrlLinkRecord> recordList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, recordList);
    }

    /**
     * 删除微信小程序加密URLLink记录
     */
    @Override
    public Boolean deleteMiniUrlLinkRecord(Integer id) {
        WechatUrlLinkRecord record = getById(id);
        if (record.getIsDel()) {
            return Boolean.TRUE;
        }
        record.setIsDel(true);
        record.setUpdateTime(DateUtil.date());
        return updateById(record);
    }
}

