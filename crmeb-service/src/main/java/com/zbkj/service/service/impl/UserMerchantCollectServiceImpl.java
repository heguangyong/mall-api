package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.user.UserMerchantCollect;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CancelCollectRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.MerchantCollectResponse;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.service.dao.UserMerchantCollectDao;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.UserMerchantCollectService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
*  UserMerchantCollectServiceImpl 接口实现
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
@Service
public class UserMerchantCollectServiceImpl extends ServiceImpl<UserMerchantCollectDao, UserMerchantCollect> implements UserMerchantCollectService {

    @Resource
    private UserMerchantCollectDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;

    /**
     * 是否收藏
     * @param userId 用户uid
     * @param merId 商户id
     * @return Boolean
     */
    @Override
    public Boolean isCollect(Integer userId, Integer merId) {
        LambdaQueryWrapper<UserMerchantCollect> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserMerchantCollect::getUid, userId);
        lqw.eq(UserMerchantCollect::getMerId, merId);
        lqw.last(" limit 1");
        UserMerchantCollect collect = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(collect);
    }

    /**
     * 店铺关注人数
     * @param merId 商户id
     * @return Integer
     */
    @Override
    public Integer getCountByMerId(Integer merId) {
        LambdaQueryWrapper<UserMerchantCollect> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserMerchantCollect::getMerId, merId);
        return dao.selectCount(lqw);
    }

    /**
     * 删除收藏
     * @param uid 用户id
     * @param merId 商户id
     * @return Boolean
     */
    private Boolean delete(Integer uid, Integer merId) {
        LambdaUpdateWrapper<UserMerchantCollect> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(UserMerchantCollect::getUid, uid);
        wrapper.eq(UserMerchantCollect::getMerId, merId);
        return remove(wrapper);
    }

    /**
     * 用户收藏店铺
     * @param merId 商户id
     * @return Boolean
     */
    @Override
    public Boolean userCollect(Integer merId) {
        Integer uid = userService.getUserIdException();
        if (isCollect(uid, merId)) {
            return Boolean.TRUE;
        }
        UserMerchantCollect userMerchantCollect = new UserMerchantCollect();
        userMerchantCollect.setUid(uid);
        userMerchantCollect.setMerId(merId);
        return save(userMerchantCollect);
    }

    /**
     * 用户取消收藏店铺
     * @param merId 商户id
     * @return Boolean
     */
    @Override
    public Boolean userCancelCollect(Integer merId) {
        Integer uid = userService.getUserIdException();
        if (!isCollect(uid, merId)) {
            return Boolean.TRUE;
        }
        if (merchantEmployeeService.isExist(merId, uid)) {
            throw new CrmebException("店铺管理员不可取关店铺");
        }
        return delete(uid, merId);
    }

    /**
     * 店铺收藏列表
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public PageInfo<MerchantCollectResponse> findList(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        LambdaQueryWrapper<UserMerchantCollect> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserMerchantCollect::getUid, uid);
        lqw.orderByDesc(UserMerchantCollect::getCreateTime);
        Page<Object> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserMerchantCollect> collectList = dao.selectList(lqw);
        if (CollUtil.isEmpty(collectList)) {
            return CommonPage.copyPageInfo(page, CollUtil.newArrayList());
        }
        List<MerchantCollectResponse> responseList = collectList.stream().map(collect -> {
            MerchantCollectResponse response = new MerchantCollectResponse();
            BeanUtils.copyProperties(collect, response);
            Merchant merchant = merchantService.getById(collect.getMerId());
            response.setMerName(merchant.getName());
            response.setMerAvatar(merchant.getAvatar());
            response.setIsSelf(merchant.getIsSelf());
            response.setCollectNum(getCountByMerId(merchant.getId()));
            response.setPcGoodStoreCoverImage(merchant.getPcGoodStoreCoverImage());
            response.setPcLogo(merchant.getPcLogo());
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 通过用户id删除
     * @param uid 用户ID
     */
    @Override
    public Boolean deleteByUid(Integer uid) {
        LambdaUpdateWrapper<UserMerchantCollect> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(UserMerchantCollect::getUid, uid);
        return remove(wrapper);
    }

    /**
     * 批量取消收藏店铺
     */
    @Override
    public Boolean userBatchCancelCollect(CancelCollectRequest request) {
        Integer userId = userService.getUserIdException();
        LambdaUpdateWrapper<UserMerchantCollect> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(UserMerchantCollect::getUid, userId);
        wrapper.eq(UserMerchantCollect::getMerId, CrmebUtil.stringToArray(request.getIds()));
        return remove(wrapper);
    }
}

