package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantDeliveryPersonnel;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.MerchantDeliveryPersonnelSaveRequest;
import com.zbkj.common.request.MerchantDeliveryPersonnelSearchRequest;
import com.zbkj.common.response.MerchantDeliveryPersonnelPageResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.MerchantDeliveryPersonnelDao;
import com.zbkj.service.service.MerchantDeliveryPersonnelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HZW
 * @description MerchantDeliveryPersonnelServiceImpl 接口实现
 * @date 2024-11-04
 */
@Service
public class MerchantDeliveryPersonnelServiceImpl extends ServiceImpl<MerchantDeliveryPersonnelDao, MerchantDeliveryPersonnel> implements MerchantDeliveryPersonnelService {

    @Resource
    private MerchantDeliveryPersonnelDao dao;

    /**
     * 商户配送人员分页列表
     */
    @Override
    public PageInfo<MerchantDeliveryPersonnelPageResponse> findPage(MerchantDeliveryPersonnelSearchRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<MerchantDeliveryPersonnel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDeliveryPersonnel::getMerId, admin.getMerId());
        if (StrUtil.isNotBlank(request.getPersonnelName())) {
            lqw.like(MerchantDeliveryPersonnel::getPersonnelName, URLUtil.decode(request.getPersonnelName()));
        }
        if (StrUtil.isNotBlank(request.getPersonnelPhone())) {
            lqw.like(MerchantDeliveryPersonnel::getPersonnelPhone, request.getPersonnelPhone());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(MerchantDeliveryPersonnel::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.eq(MerchantDeliveryPersonnel::getIsDelete, 0);
        lqw.eq(MerchantDeliveryPersonnel::getStatus, 1);
        lqw.orderByDesc(MerchantDeliveryPersonnel::getSort, MerchantDeliveryPersonnel::getId);
        Page<MerchantDeliveryPersonnel> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<MerchantDeliveryPersonnel> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<MerchantDeliveryPersonnelPageResponse> responseList = list.stream().map(mdp -> {
            MerchantDeliveryPersonnelPageResponse response = new MerchantDeliveryPersonnelPageResponse();
            BeanUtils.copyProperties(mdp, response);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 商户配送人员分页列表
     */
    @Override
    public PageInfo<MerchantDeliveryPersonnelPageResponse> findPageByEmployee(MerchantDeliveryPersonnelSearchRequest request, Integer merId) {
        LambdaQueryWrapper<MerchantDeliveryPersonnel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDeliveryPersonnel::getMerId, merId);
        if (StrUtil.isNotBlank(request.getPersonnelName())) {
            lqw.like(MerchantDeliveryPersonnel::getPersonnelName, URLUtil.decode(request.getPersonnelName()));
        }
        if (StrUtil.isNotBlank(request.getPersonnelPhone())) {
            lqw.like(MerchantDeliveryPersonnel::getPersonnelPhone, request.getPersonnelPhone());
        }
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            lqw.between(MerchantDeliveryPersonnel::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lqw.eq(MerchantDeliveryPersonnel::getIsDelete, 0);
        lqw.eq(MerchantDeliveryPersonnel::getStatus, 1);
        lqw.orderByDesc(MerchantDeliveryPersonnel::getSort, MerchantDeliveryPersonnel::getId);
        Page<MerchantDeliveryPersonnel> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<MerchantDeliveryPersonnel> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<MerchantDeliveryPersonnelPageResponse> responseList = list.stream().map(mdp -> {
            MerchantDeliveryPersonnelPageResponse response = new MerchantDeliveryPersonnelPageResponse();
            BeanUtils.copyProperties(mdp, response);
            return response;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 新增商户配送人员
     */
    @Override
    public Boolean add(MerchantDeliveryPersonnelSaveRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        // 重复校校验
        if (isExistName(request.getPersonnelName(), 0, admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员名称重复");
        }
        if (isExistPhone(request.getPersonnelPhone(), 0, admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员手机号重复");
        }
        MerchantDeliveryPersonnel personnel = new MerchantDeliveryPersonnel();
        BeanUtils.copyProperties(request, personnel);
        personnel.setId(null);
        personnel.setMerId(admin.getMerId());
        return save(personnel);
    }

    /**
     * 删除商户配送人员
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        getByIdException(id, admin.getMerId());
        LambdaUpdateWrapper<MerchantDeliveryPersonnel> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantDeliveryPersonnel::getIsDelete, 1);
        wrapper.eq(MerchantDeliveryPersonnel::getId, id);
        return update(wrapper);
    }

    /**
     * 编辑商户配送人员
     */
    @Override
    public Boolean edit(MerchantDeliveryPersonnelSaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择配送人员");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        getByIdException(request.getId(), admin.getMerId());
        if (isExistName(request.getPersonnelName(), request.getId(), admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员名称重复");
        }
        if (isExistPhone(request.getPersonnelPhone(), request.getId(), admin.getMerId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员手机号重复");
        }
        MerchantDeliveryPersonnel personnel = new MerchantDeliveryPersonnel();
        personnel.setId(request.getId());
        personnel.setPersonnelName(request.getPersonnelName());
        personnel.setPersonnelPhone(request.getPersonnelPhone());
        personnel.setSort(request.getSort());
        personnel.setUpdateTime(new Date());
        return updateById(personnel);
    }

    private MerchantDeliveryPersonnel getByIdException(Integer id, Integer merId) {
        MerchantDeliveryPersonnel personnel = getById(id);
        if (ObjectUtil.isNull(personnel) || personnel.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员不存在");
        }
        if (!personnel.getMerId().equals(merId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员不存在");
        }
        return personnel;
    }

    private MerchantDeliveryPersonnel getByIdException(Integer id) {
        MerchantDeliveryPersonnel personnel = getById(id);
        if (ObjectUtil.isNull(personnel) || personnel.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "配送人员不存在");
        }
        return personnel;
    }

    /**
     * 是否存在相同的配送人员名称
     */
    private Boolean isExistPhone(String personnelPhone, Integer id, Integer merId) {
        LambdaQueryWrapper<MerchantDeliveryPersonnel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDeliveryPersonnel::getPersonnelPhone, personnelPhone);
        if (id > 0) {
            lqw.ne(MerchantDeliveryPersonnel::getId, id);
        }
        lqw.eq(MerchantDeliveryPersonnel::getMerId, merId);
        lqw.eq(MerchantDeliveryPersonnel::getIsDelete, 0);
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }

    /**
     * 是否存在相同的配送人员手机号
     */
    private Boolean isExistName(String personnelName, Integer id, Integer merId) {
        LambdaQueryWrapper<MerchantDeliveryPersonnel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantDeliveryPersonnel::getPersonnelName, personnelName);
        if (id > 0) {
            lqw.ne(MerchantDeliveryPersonnel::getId, id);
        }
        lqw.eq(MerchantDeliveryPersonnel::getMerId, merId);
        lqw.eq(MerchantDeliveryPersonnel::getIsDelete, 0);
        Integer count = dao.selectCount(lqw);
        return count > 0;
    }
}

