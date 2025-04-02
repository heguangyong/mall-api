package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.UserMerchantCollect;
import com.zbkj.common.request.CancelCollectRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.MerchantCollectResponse;

/**
*  UserMerchantCollectService 接口
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
public interface UserMerchantCollectService extends IService<UserMerchantCollect> {

    /**
     * 是否收藏
     * @param userId 用户uid
     * @param merId 商户id
     * @return Boolean
     */
    Boolean isCollect(Integer userId, Integer merId);

    /**
     * 店铺关注人数
     * @param merId 商户id
     * @return Integer
     */
    Integer getCountByMerId(Integer merId);

    /**
     * 用户收藏店铺
     * @param merId 商户id
     * @return Boolean
     */
    Boolean userCollect(Integer merId);

    /**
     * 用户取消收藏店铺
     * @param merId 商户id
     * @return Boolean
     */
    Boolean userCancelCollect(Integer merId);

    /**
     * 店铺收藏列表
     * @param pageParamRequest 分页参数
     * @return List
     */
    PageInfo<MerchantCollectResponse> findList(PageParamRequest pageParamRequest);

    /**
     * 通过用户id删除
     * @param uid 用户ID
     */
    Boolean deleteByUid(Integer uid);

    /**
     * 批量取消收藏店铺
     */
    Boolean userBatchCancelCollect(CancelCollectRequest request);
}
