package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.enums.RegionTypeEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.city.CityRegion;
import com.zbkj.common.request.CityRegionEditRequest;
import com.zbkj.common.request.CityRegionRequest;
import com.zbkj.common.request.CitySearchRequest;
import com.zbkj.common.response.CityResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.CityTree;
import com.zbkj.common.vo.CityVo;
import com.zbkj.service.dao.CityRegionDao;
import com.zbkj.service.service.CityRegionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemCityServiceImpl 接口实现
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
public class CityRegionServiceImpl extends ServiceImpl<CityRegionDao, CityRegion> implements CityRegionService {

    @Resource
    private CityRegionDao dao;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ShippingTemplatesFreeServiceImpl shippingTemplatesFreeService;
    @Autowired
    private ShippingTemplatesRegionServiceImpl shippingTemplatesRegionService;

    /**
     * 获取tree结构的列表
     *
     * @return Object
     */
    @Override
    public List<CityVo> getCityListTree() {
        List<CityVo> cityList = redisUtil.get(RedisConstants.CITY_LIST_TREE);
        if (CollUtil.isEmpty(cityList)) {
            List<CityVo> treeList = new ArrayList<>();

            LambdaQueryWrapper<CityRegion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(CityRegion::getRegionId, CityRegion::getParentId, CityRegion::getRegionName, CityRegion::getRegionType);
            lambdaQueryWrapper.lt(CityRegion::getRegionType, RegionTypeEnum.STREET.getValue());
            List<CityRegion> allTree = dao.selectList(lambdaQueryWrapper);
            if (CollUtil.isEmpty(allTree)) {
                return treeList;
            }

            // 循环数据，把数据对象变成带list结构的vo
            for (CityRegion cityRegion : allTree) {
                CityVo cityVo = new CityVo();
                BeanUtils.copyProperties(cityRegion, cityVo);
                treeList.add(cityVo);
            }
            CityTree cityTree = new CityTree(treeList);
            cityList = cityTree.buildTree();
            redisUtil.set(RedisConstants.CITY_LIST_TREE, cityList);
        }
        return redisUtil.get(RedisConstants.CITY_LIST_TREE);
    }

    /**
     * 获取城市区域缓存树
     *
     * @return List
     */
    @Override
    public List<CityVo> getRegionListTree() {
        List<CityVo> cityList = redisUtil.get(RedisConstants.CITY_REGION_LIST_TREE);
        if (CollUtil.isEmpty(cityList)) {
            List<CityVo> treeList = new ArrayList<>();

            LambdaQueryWrapper<CityRegion> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(CityRegion::getRegionId, CityRegion::getParentId, CityRegion::getRegionName, CityRegion::getRegionType);
            List<CityRegion> allTree = dao.selectList(lambdaQueryWrapper);
            if (CollUtil.isEmpty(allTree)) {
                return treeList;
            }

            // 循环数据，把数据对象变成带list结构的vo
            for (CityRegion cityRegion : allTree) {
                CityVo cityVo = new CityVo();
                BeanUtils.copyProperties(cityRegion, cityVo);
                treeList.add(cityVo);
            }
            CityTree cityTree = new CityTree(treeList);
            cityList = cityTree.buildTree();
            redisUtil.set(RedisConstants.CITY_REGION_LIST_TREE, cityList);
        }
        return cityList;
    }

    /**
     * 添加城市区域
     *
     * @param request 城市区域参数
     * @return Boolean
     */
    @Override
    public Boolean add(CityRegionRequest request) {
        CityRegion region = getByRegionId(request.getRegionId());
        if (ObjectUtil.isNotNull(region)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "城市区域ID不能相同");
        }
        CityRegion cityRegion = new CityRegion();
        BeanUtils.copyProperties(request, cityRegion);
        return save(cityRegion);
    }

    /**
     * 编辑城市区域
     *
     * @param request 城市区域参数
     * @return Boolean
     */
    @Override
    public Boolean edit(CityRegionEditRequest request) {
        CityRegion region = getByRegionId(request.getOldRegionId());
        if (ObjectUtil.isNull(region)) {
            throw new CrmebException(SystemConfigResultCode.CITY_REGION_NOT_EXIST);
        }
        LambdaUpdateWrapper<CityRegion> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(CityRegion::getRegionId, request.getRegionId());
        wrapper.set(CityRegion::getRegionName, request.getRegionName());
        wrapper.eq(CityRegion::getRegionId, request.getOldRegionId());
        boolean update = update(wrapper);
        if (update) {
            if (!region.getRegionType().equals(RegionTypeEnum.STREET.getValue())) {
                redisUtil.delete(RedisConstants.CITY_LIST_TREE);
            }
            redisUtil.delete(RedisConstants.CITY_REGION_LIST_TREE);
        }
        return update;
    }

    /**
     * 删除城市区域
     *
     * @param regionId 区域id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer regionId) {
        CityRegion region = getByRegionId(regionId);
        if (ObjectUtil.isNull(region)) {
            throw new CrmebException(SystemConfigResultCode.CITY_REGION_NOT_EXIST);
        }
        if (!region.getRegionType().equals(RegionTypeEnum.STREET.getValue())) {
            if (countByParentId(region.getParentId()) > 0) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "城市区域下有子级区域，请先删除子级区域");
            }
        }
        if (region.getRegionType().equals(RegionTypeEnum.AREA.getValue())) {
            if (shippingTemplatesRegionService.existCityId(region.getRegionId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "城市区域有运费模板使用，请先修改运费模板后再删除");
            }
            if (shippingTemplatesFreeService.existCityId(region.getRegionId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "城市区域有运费模板使用，请先修改运费模板后再删除");
            }
        }
        LambdaUpdateWrapper<CityRegion> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(CityRegion::getRegionId, regionId);
        int delete = dao.delete(wrapper);
        if (delete > 0) {
            if (!region.getRegionType().equals(RegionTypeEnum.STREET.getValue())) {
                redisUtil.delete(RedisConstants.CITY_LIST_TREE);
            }
            redisUtil.delete(RedisConstants.CITY_REGION_LIST_TREE);
        }
        return delete > 0;
    }

    /**
     * 获取城市区域列表
     *
     * @param request 请求参数
     * @return 城市区域列表
     */
    @Override
    public List<CityResponse> getCityRegionList(CitySearchRequest request) {
        LambdaQueryWrapper<CityRegion> lqw = Wrappers.lambdaQuery();
        lqw.eq(CityRegion::getParentId, request.getParentId());
        lqw.eq(CityRegion::getRegionType, request.getRegionType());
        List<CityRegion> cityList = dao.selectList(lqw);
        if (CollUtil.isEmpty(cityList)) {
            return CollUtil.newArrayList();
        }
        return cityList.stream().map(e -> {
            CityResponse cityResponse = new CityResponse();
            BeanUtils.copyProperties(e, cityResponse);
            if (e.getRegionType().equals(3)) {
                cityResponse.setIsChild(isExistChild(e.getRegionId()));
            }
            if (e.getRegionType().equals(4)) {
                cityResponse.setIsChild(false);
            }
            return cityResponse;
        }).collect(Collectors.toList());
    }

    /**
     * 是否有下级数据
     *
     * @param parentId 父级id
     */
    private Boolean isExistChild(Integer parentId) {
        LambdaQueryWrapper<CityRegion> lqw = Wrappers.lambdaQuery();
        lqw.select(CityRegion::getRegionId);
        lqw.eq(CityRegion::getParentId, parentId);
        lqw.last(" limit 1");
        CityRegion cityRegion = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(cityRegion);
    }

    private Integer countByParentId(Integer parentId) {
        LambdaQueryWrapper<CityRegion> lqw = Wrappers.lambdaQuery();
        lqw.eq(CityRegion::getParentId, parentId);
        return dao.selectCount(lqw);
    }

    /**
     * 获取城市数据
     *
     * @param regionId 区域id
     * @return 城市数据
     */
    @Override
    public CityRegion getByRegionId(Integer regionId) {
        LambdaQueryWrapper<CityRegion> lqw = Wrappers.lambdaQuery();
        lqw.eq(CityRegion::getRegionId, regionId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 通过区域名称获取城市数据
     *
     * @param regionName 区域名称
     * @param parentId   父区域id
     * @return 城市数据
     */
    @Override
    public CityRegion getByRegionName(String regionName, Integer parentId, Integer regionType) {
        LambdaQueryWrapper<CityRegion> lqw = Wrappers.lambdaQuery();
        lqw.eq(CityRegion::getRegionName, regionName);
        lqw.eq(CityRegion::getRegionType, regionType);
        lqw.eq(CityRegion::getParentId, parentId);
        lqw.last(" limit 1");
        CityRegion cityRegion = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(cityRegion)) {
            return cityRegion;
        }
        lqw.clear();
        lqw.likeRight(CityRegion::getRegionName, StrUtil.subPre(regionName, 2));
        lqw.eq(CityRegion::getRegionType, regionType);
        lqw.eq(CityRegion::getParentId, parentId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取省级城市数据
     */
    @Override
    public List<CityRegion> findProvinceList() {
        return findByRegionType(1, 0);
    }

    /**
     * 获取市级城市数据
     */
    @Override
    public List<CityRegion> findCityList(Integer provinceId) {
        return findByRegionType(2, provinceId);
    }

    /**
     * 获取区级城市数据
     */
    @Override
    public List<CityRegion> findDistrictList(Integer cityId) {
        return findByRegionType(3, cityId);
    }

    /**
     * 获取区级城市数据
     */
    @Override
    public List<CityRegion> findStreetList(Integer districtId) {
        return findByRegionType(4, districtId);
    }

    /**
     * 按区域类型获取城市数据
     * @param regionType 区域类型，0-国家、1-省、2-市、3-区、4-街道
     * @param parentId 父区域id
     */
    private List<CityRegion> findByRegionType(Integer regionType, Integer parentId) {
        LambdaQueryWrapper<CityRegion> lqw = Wrappers.lambdaQuery();
        lqw.eq(CityRegion::getRegionType, regionType);
        if (parentId > 0) {
            lqw.eq(CityRegion::getParentId, parentId);
        }
        return dao.selectList(lqw);
    }
}

