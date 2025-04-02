package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.BrowseRecordDao;
import com.zbkj.service.service.BrowseRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserVisitRecordServiceImpl 接口实现
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
public class BrowseRecordServiceImpl extends ServiceImpl<BrowseRecordDao, BrowseRecord> implements BrowseRecordService {

    @Resource
    private BrowseRecordDao dao;

    /**
     * 根据用户id和商品id获取
     * @param uid 用户id
     * @param proId 商品id
     */
    @Override
    public BrowseRecord getByUidAndProId(Integer uid, Integer proId) {
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, uid);
        lqw.eq(BrowseRecord::getProductId, proId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取用户足迹数量
     * @param uid 用户ID
     * @return 用户足迹数量
     */
    @Override
    public Integer getCountByUid(Integer uid) {
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, uid);
        return dao.selectCount(lqw);
    }

    /**
     * 获取用户所有足迹
     * @param uid 用户Id
     * @return 用户足迹
     */
    @Override
    public List<BrowseRecord> findAllByUid(Integer uid) {
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, uid);
        lqw.orderByDesc(BrowseRecord::getCreateTime);
        lqw.last(" limit 100");
        return dao.selectList(lqw);
    }

    /**
     * 更新足迹数据
     */
    @Override
    public Boolean myUpdate(BrowseRecord browseRecord) {
        return dao.myUpdate(browseRecord) > 0;
    }

    /**
     * 用户足迹分月列表
     * @param userId 用户Id
     * @param pageParamRequest 分页参数
     */
    @Override
    public PageInfo<BrowseRecord> findPageByUserId(Integer userId, PageParamRequest pageParamRequest) {
        Page<BrowseRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<BrowseRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(BrowseRecord::getUid, userId);
        lqw.orderByDesc(BrowseRecord::getCreateTime);
        List<BrowseRecord> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }
}

