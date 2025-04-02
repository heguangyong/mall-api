package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantAddress;
import com.zbkj.common.request.merchant.MerchantAddressSaveRequest;
import com.zbkj.common.request.merchant.MerchantAddressSearchRequest;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.MerchantAddressDao;
import com.zbkj.service.service.MerchantAddressManagerService;
import com.zbkj.service.service.MerchantAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class MerchantAddressManagerServiceImpl extends ServiceImpl<MerchantAddressDao, MerchantAddress> implements MerchantAddressManagerService {

    @Autowired
    private MerchantAddressService merchantAddressService;

    /**
     * 商户地址列表
     * @param request 请求参数
     * @return List
     */
    @Override
    public List<MerchantAddress> findList(MerchantAddressSearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return merchantAddressService.findList(request,systemAdmin);
    }

    /**
     * 新增商户地址
     * @param request 请求参数
     * @return 新增结果
     */
    @Override
    public Boolean add(MerchantAddressSaveRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return merchantAddressService.add(request, systemAdmin);
    }

    /**
     * 修改商户地址
     * @param request 请求参数
     * @return 修改结果
     */
    @Override
    public Boolean updateAddress(MerchantAddressSaveRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return merchantAddressService.updateAddress(request, systemAdmin);
    }

    /**
     * 删除商户地址
     * @param id 商户地址ID
     * @return 删除结果
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return merchantAddressService.delete(id, systemAdmin);
    }

    /**
     * 设置商户默认地址
     * @param id 商户地址ID
     */
    @Override
    public Boolean setDefault(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return merchantAddressService.setDefault(id, systemAdmin);
    }

    /**
     * 设置商户地址开启状态
     * @param id 商户地址ID
     */
    @Override
    public Boolean updateShow(Integer id) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        return merchantAddressService.updateShow(id, systemAdmin);
    }

}

