package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.seckill.SeckillActivity;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.SeckillActivitySearchRequest;
import com.zbkj.service.dao.SeckillActivityDao;
import com.zbkj.service.service.SeckillActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  SeckillActivityServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityDao, SeckillActivity> implements SeckillActivityService {

    @Resource
    private SeckillActivityDao dao;

    /**
     * 秒杀活动分页列表
     * @param request 查询参数
     * @param isMerchant 是否商户
     */
    @Override
    public PageInfo<SeckillActivity> getActivityPage(SeckillActivitySearchRequest request, Boolean isMerchant) {
        Page<SeckillActivity> page = PageHelper.startPage(request.getPage(), request.getLimit());
        LambdaQueryWrapper<SeckillActivity> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(request.getName())) {
            lqw.like(SeckillActivity::getName, URLUtil.decode(request.getName()));
        }
        if (StrUtil.isNotBlank(request.getDate())) {
            lqw.le(SeckillActivity::getStartDate, request.getDate());
            lqw.ge(SeckillActivity::getEndDate, request.getDate());
        }
        if (isMerchant) {
//            lqw.eq(SeckillActivity::getIsOpen, 1);
        }
        lqw.eq(SeckillActivity::getIsDel, 0);
        lqw.orderByDesc(SeckillActivity::getId);
        List<SeckillActivity> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 获取秒杀活动
     * @param acvitityIdList 秒杀活动ID列表
     * @param isOpen 是否开启
     */
    @Override
    public List<SeckillActivity> findByIdListAndOpen(List<Integer> acvitityIdList, Boolean isOpen) {
        LambdaQueryWrapper<SeckillActivity> lqw = Wrappers.lambdaQuery();
        lqw.in(SeckillActivity::getId, acvitityIdList);
        if (ObjectUtil.isNotNull(isOpen)) {
            lqw.eq(SeckillActivity::getIsOpen, isOpen);
        }
        return dao.selectList(lqw);
    }
}

