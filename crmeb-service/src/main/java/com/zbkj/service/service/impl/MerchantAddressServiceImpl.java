package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantAddress;
import com.zbkj.common.request.merchant.MerchantAddressSaveRequest;
import com.zbkj.common.request.merchant.MerchantAddressSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MerchantResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.MerchantAddressDao;
import com.zbkj.service.service.MerchantAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * MerchantAddressServiceImpl 接口实现
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
public class MerchantAddressServiceImpl extends ServiceImpl<MerchantAddressDao, MerchantAddress> implements MerchantAddressService {

    @Resource
    private MerchantAddressDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 商户地址列表
     * @param request 请求参数
     * @return List
     */
    @Override
    public List<MerchantAddress> findList(MerchantAddressSearchRequest request,SystemAdmin admin) {
        LambdaQueryWrapper<MerchantAddress> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantAddress::getMerId, admin.getMerId());
        lqw.eq(MerchantAddress::getIsDel, 0);
        if (ObjectUtil.isNotNull(request.getIsShow())) {
            lqw.eq(MerchantAddress::getIsShow, request.getIsShow());
        }
        lqw.orderByDesc(MerchantAddress::getIsDefault, MerchantAddress::getId);
        return dao.selectList(lqw);
    }

    /**
     * 新增商户地址
     * @param request 请求参数
     * @return 新增结果
     */
    @Override
    public Boolean add(MerchantAddressSaveRequest request,SystemAdmin admin) {
        MerchantAddress address = new MerchantAddress();
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setDetail(request.getDetail());
        address.setIsShow(request.getIsShow());
        address.setIsDefault(request.getIsDefault());
        address.setMerId(admin.getMerId());
        return transactionTemplate.execute(e -> {
            if (address.getIsDefault()) {
                clearDefaultByMerId(admin.getMerId());
            }
            save(address);
            return Boolean.TRUE;
        });
    }

    /**
     * 修改商户地址
     * @param request 请求参数
     * @return 修改结果
     */
    @Override
    public Boolean updateAddress(MerchantAddressSaveRequest request,SystemAdmin admin) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请选择商户地址");
        }
        MerchantAddress address = getByIdException(request.getId());
        if (!admin.getMerId().equals(address.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_ADDRESS_NOT_EXIST);
        }
        boolean isDefault = address.getIsDefault();
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setDetail(request.getDetail());
        address.setIsShow(request.getIsShow());
        address.setIsDefault(request.getIsDefault());
        return transactionTemplate.execute(e -> {
            if (address.getIsDefault() && !isDefault) {
                clearDefaultByMerId(admin.getMerId());
            }
            updateById(address);
            return Boolean.TRUE;
        });
    }

    /**
     * 删除商户地址
     * @param id 商户地址ID
     * @return 删除结果
     */
    @Override
    public Boolean delete(Integer id,SystemAdmin admin) {
        MerchantAddress address = getByIdException(id);
        if (!admin.getMerId().equals(address.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_ADDRESS_NOT_EXIST);
        }
        LambdaUpdateWrapper<MerchantAddress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantAddress::getIsDel, 1);
        wrapper.eq(MerchantAddress::getId, id);
        return update(wrapper);
    }

    /**
     * 设置商户默认地址
     * @param id 商户地址ID
     */
    @Override
    public Boolean setDefault(Integer id,SystemAdmin admin) {
        MerchantAddress address = getByIdException(id);
        if (!admin.getMerId().equals(address.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_ADDRESS_NOT_EXIST);
        }
        if (address.getIsDefault()) {
            return Boolean.TRUE;
        }
        address.setIsDefault(true);
        return transactionTemplate.execute(e -> {
            clearDefaultByMerId(admin.getMerId());
            updateById(address);
            return Boolean.TRUE;
        });
    }

    /**
     * 设置商户地址开启状态
     * @param id 商户地址ID
     */
    @Override
    public Boolean updateShow(Integer id,SystemAdmin admin) {
        MerchantAddress address = getByIdException(id);
        if (!admin.getMerId().equals(address.getMerId())) {
            throw new CrmebException(MerchantResultCode.MERCHANT_ADDRESS_NOT_EXIST);
        }
        address.setIsShow(!address.getIsShow());
        return updateById(address);
    }

    @Override
    public MerchantAddress getByIdException(Integer id) {
        MerchantAddress merchantAddress = getById(id);
        if (ObjectUtil.isNull(merchantAddress) || merchantAddress.getIsDel()) {
            throw new CrmebException(MerchantResultCode.MERCHANT_ADDRESS_NOT_EXIST);
        }
        return merchantAddress;
    }

    /**
     * 清除商户默认地址
     * @param merId 商户ID
     */
    private void clearDefaultByMerId(Integer merId) {
        LambdaUpdateWrapper<MerchantAddress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantAddress::getIsDefault, 0);
        wrapper.eq(MerchantAddress::getMerId, merId);
        wrapper.eq(MerchantAddress::getIsDefault, 1);
        update(wrapper);
    }

}

