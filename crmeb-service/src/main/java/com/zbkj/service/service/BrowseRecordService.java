package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.record.BrowseRecord;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
 * BrowseRecordService 接口
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
public interface BrowseRecordService extends IService<BrowseRecord> {

    /**
     * 根据用户id和商品id获取
     * @param uid 用户id
     * @param proId 商品id
     */
    BrowseRecord getByUidAndProId(Integer uid, Integer proId);

    /**
     * 获取用户足迹数量
     * @param uid 用户ID
     * @return 用户足迹数量
     */
    Integer getCountByUid(Integer uid);

    /**
     * 获取用户所有足迹
     * @param uid 用户Id
     * @return 用户足迹
     */
    List<BrowseRecord> findAllByUid(Integer uid);

    /**
     * 更新足迹数据
     */
    Boolean myUpdate(BrowseRecord browseRecord);

    /**
     * 用户足迹分月列表
     * @param userId 用户Id
     * @param pageParamRequest 分页参数
     */
    PageInfo<BrowseRecord> findPageByUserId(Integer userId, PageParamRequest pageParamRequest);
}