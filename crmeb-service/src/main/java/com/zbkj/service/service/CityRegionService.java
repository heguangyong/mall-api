package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.city.CityRegion;
import com.zbkj.common.request.CityRegionEditRequest;
import com.zbkj.common.request.CityRegionRequest;
import com.zbkj.common.request.CitySearchRequest;
import com.zbkj.common.response.CityResponse;
import com.zbkj.common.vo.CityVo;

import java.util.List;

/**
 * SystemCityService 接口
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
public interface CityRegionService extends IService<CityRegion> {

    /**
     * 获取城市树
     */
    List<CityVo> getCityListTree();

    /**
     * 获取城市区域缓存树
     *
     * @return List
     */
    List<CityVo> getRegionListTree();

    /**
     * 添加城市区域
     *
     * @param request 城市区域参数
     * @return Boolean
     */
    Boolean add(CityRegionRequest request);

    /**
     * 编辑城市区域
     *
     * @param request 城市区域参数
     * @return Boolean
     */
    Boolean edit(CityRegionEditRequest request);

    /**
     * 删除城市区域
     *
     * @param regionId 区域id
     * @return Boolean
     */
    Boolean delete(Integer regionId);

    /**
     * 获取城市区域列表
     * @param request 请求参数
     * @return 城市区域列表
     */
    List<CityResponse> getCityRegionList(CitySearchRequest request);

    /**
     * 通过区域ID获取城市数据
     * @param regionId 区域id
     * @return 城市数据
     */
    CityRegion getByRegionId(Integer regionId);

    /**
     * 通过区域名称获取城市数据
     * @param regionName 区域名称
     * @param parentId 父区域id
     * @return 城市数据
     */
    CityRegion getByRegionName(String regionName, Integer parentId, Integer regionType);

    /**
     * 获取省级城市数据
     */
    List<CityRegion> findProvinceList();

    /**
     * 获取市级城市数据
     */
    List<CityRegion> findCityList(Integer provinceId);

    /**
     * 获取区级城市数据
     */
    List<CityRegion> findDistrictList(Integer cityId);

    /**
     * 获取街道级城市数据
     */
    List<CityRegion> findStreetList(Integer districtId);

}
