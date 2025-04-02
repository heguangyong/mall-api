package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.city.CityRegion;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.request.UserAddressRequest;
import com.zbkj.common.request.WechatAddressImportRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.UserResultCode;
import com.zbkj.service.dao.UserAddressDao;
import com.zbkj.service.service.CityRegionService;
import com.zbkj.service.service.UserAddressService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserAddressServiceImpl 接口实现
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
public class UserAddressServiceImpl extends ServiceImpl<UserAddressDao, UserAddress> implements UserAddressService {

    @Resource
    private UserAddressDao dao;

    @Autowired
    private UserService userService;
    @Autowired
    private CityRegionService cityRegionService;

    /**
     * 创建地址
     *
     * @param request UserAddressRequest 参数
     * @return 地址ID
     */
    @Override
    public Integer create(UserAddressRequest request) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(request, userAddress);
        userAddress.setUid(userService.getUserIdException());
        userAddress.setId(null);
        if (userAddress.getIsDefault()) {
            //把当前用户其他默认地址取消
            cancelDefault(userAddress.getUid());
        }
        boolean save = save(userAddress);
        if (!save) {
            throw new CrmebException(UserResultCode.USER_ADDRESS_CREATE_FAILED);
        }
        return userAddress.getId();
    }

    /**
     * 用户地址编辑
     *
     * @param request 编辑请求参数
     * @return Boolean
     */
    @Override
    public Boolean edit(UserAddressRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "地址ID不能为空");
        }
        Integer userId = userService.getUserIdException();
        UserAddress userAddress = getById(request.getId());
        if (ObjectUtil.isNull(userAddress) || !userId.equals(userAddress.getUid())) {
            throw new CrmebException(UserResultCode.USER_ADDRESS_NOT_EXIST);
        }
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(request, address);
        if (StrUtil.isBlank(request.getStreet())) {
            address.setStreet("");
        }
        if (address.getIsDefault()) {
            cancelDefault(userId);
        }
        return updateById(address);
    }

    /**
     * 删除
     *
     * @param id Integer id
     * @return UserAddress
     */
    @Override
    public Boolean delete(Integer id) {
        LambdaUpdateWrapper<UserAddress> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(UserAddress::getId, id);
        wrapper.eq(UserAddress::getUid, userService.getUserIdException());
        return dao.delete(wrapper) > 0;
    }

    /**
     * 获取默认地址
     *
     * @return UserAddress
     */
    @Override
    public UserAddress getDefault() {
        LambdaQueryWrapper<UserAddress> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserAddress::getIsDefault, true);
        lqw.eq(UserAddress::getUid, userService.getUserIdException());
        lqw.eq(UserAddress::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    private UserAddress getById(Integer addressId) {
        LambdaQueryWrapper<UserAddress> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserAddress::getId, addressId);
        lqw.eq(UserAddress::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取地址详情
     *
     * @param id 地址id
     * @return UserAddress
     */
    @Override
    public UserAddress getDetail(Integer id) {
        Integer UserId = userService.getUserIdException();
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.select(UserAddress::getId, UserAddress::getRealName, UserAddress::getPhone, UserAddress::getProvince, UserAddress::getProvinceId,
                UserAddress::getCity, UserAddress::getDistrict, UserAddress::getStreet, UserAddress::getDetail,
                UserAddress::getPostCode, UserAddress::getIsDefault, UserAddress::getCityId, UserAddress::getDistrictId);
        lqw.eq(UserAddress::getId, id);
        lqw.eq(UserAddress::getUid, UserId);
        lqw.eq(UserAddress::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取默认地址
     *
     * @return UserAddress
     */
    @Override
    public UserAddress getDefaultByUid(Integer uid) {
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserAddress::getIsDefault, true);
        lqw.eq(UserAddress::getUid, uid);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取所有的用户地址
     *
     * @return List
     */
    @Override
    public List<UserAddress> getAllList() {
        Integer UserId = userService.getUserIdException();
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.select(UserAddress::getId, UserAddress::getRealName, UserAddress::getPhone, UserAddress::getProvince, UserAddress::getProvinceId,
                UserAddress::getCity, UserAddress::getDistrict, UserAddress::getDetail, UserAddress::getPostCode,
                UserAddress::getIsDefault, UserAddress::getCityId, UserAddress::getDistrictId, UserAddress::getStreet);
        lqw.eq(UserAddress::getUid, UserId);
        lqw.eq(UserAddress::getIsDel, false);
        lqw.orderByDesc(UserAddress::getIsDefault);
        lqw.orderByDesc(UserAddress::getId);
        return dao.selectList(lqw);
    }

    /**
     * 设置默认地址
     *
     * @param id 地址id
     * @return Boolean
     */
    @Override
    public Boolean setDefault(Integer id) {
        UserAddress userAddress = getDefault();
        if (ObjectUtil.isNotNull(userAddress)) {
            cancelDefault(userAddress.getUid());
        }
        LambdaUpdateWrapper<UserAddress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(UserAddress::getIsDefault, true);
        wrapper.eq(UserAddress::getId, id);
        wrapper.eq(UserAddress::getUid, userService.getUserIdException());
        return update(wrapper);
    }

    /**
     * 获取微信地址信息
     * @param request 微信地址参数
     */
    @Override
    public UserAddress getWechatInfo(WechatAddressImportRequest request) {

        CityRegion countyRegion = cityRegionService.getByRegionId(Integer.parseInt(request.getNationalCode()));
        CityRegion provinceRegion;
        CityRegion cityRegion;
        if (ObjectUtil.isNotNull(countyRegion) && countyRegion.getRegionType().equals(3)) {
            cityRegion = cityRegionService.getByRegionId(countyRegion.getParentId());
            provinceRegion = cityRegionService.getByRegionId(cityRegion.getParentId());
        } else {
            // 名称搜索匹配
            provinceRegion = cityRegionService.getByRegionName(request.getProvinceName(), 1, 1);
            if (ObjectUtil.isNull(provinceRegion)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("对应省级区域未找到，微信参数provinceName = {}", request.getProvinceName()));
            }
            cityRegion = cityRegionService.getByRegionName(request.getCityName(), provinceRegion.getRegionId(), 2);
            if (ObjectUtil.isNull(provinceRegion)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("对应市级区域未找到，微信参数cityName = {}", request.getCityName()));
            }
            countyRegion = cityRegionService.getByRegionName(request.getCountryName(), cityRegion.getRegionId(), 3);
            if (ObjectUtil.isNull(provinceRegion)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("对应区级区域未找到，微信参数countyName = {}", request.getCountryName()));
            }
        }
        UserAddress userAddress = new UserAddress();
        userAddress.setProvince(provinceRegion.getRegionName());
        userAddress.setProvinceId(provinceRegion.getRegionId());
        userAddress.setCity(cityRegion.getRegionName());
        userAddress.setCityId(cityRegion.getRegionId());
        userAddress.setDistrict(countyRegion.getRegionName());
        userAddress.setDistrictId(countyRegion.getRegionId());
        userAddress.setStreet(StrUtil.isNotBlank(request.getStreetName()) ? request.getStreetName() : "");
        userAddress.setDetail(request.getDetail());
        return userAddress;
    }

    /**
     * 取消默认地址
     *
     * @param userId Integer 城市id
     */
    private void cancelDefault(Integer userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(UserAddress::getIsDefault, false);
        wrapper.eq(UserAddress::getUid, userId);
        update(wrapper);
    }

}

