package com.zbkj.common.vo;

import cn.hutool.core.collection.CollUtil;
import com.zbkj.common.enums.RegionTypeEnum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 城市树
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
public class CityTree {

    private List<CityVo> cityList = new ArrayList<CityVo>();

    public CityTree(List<CityVo> cityVoList) {
        this.cityList = cityVoList;
    }

    //建立树形结构
    public List<CityVo> buildTree(){
        List<CityVo> treeCity = new ArrayList<CityVo>();
        for(CityVo cityVo : getRootNode()) {
            cityVo = buildChildTree(cityVo);
            treeCity.add(cityVo);
        }
        return sortList(treeCity);
    }

    // 排序
    private List<CityVo> sortList(List<CityVo> treeCityList) {
        treeCityList = treeCityList.stream().sorted(Comparator.comparing(CityVo::getRegionId)).collect(Collectors.toList());
        treeCityList.forEach(e -> {
            if (CollUtil.isNotEmpty(e.getChild())) {
                e.setChild(sortList(e.getChild()));
            }
        });
        return treeCityList;
    }

    //递归，建立子树形结构
    private CityVo buildChildTree(CityVo pNode){
        List<CityVo> childCityList = new ArrayList<CityVo>();
        for(CityVo cityVo : cityList) {
            if(cityVo.getParentId().equals(pNode.getRegionId())) {
                childCityList.add(buildChildTree(cityVo));
            }
        }
        pNode.setChild(childCityList);
        return pNode;
    }

    //获取根节点
    private List<CityVo> getRootNode() {
        List<CityVo> cityVoList = new  ArrayList<CityVo>();
        for(CityVo cityVo : cityList) {
            if(cityVo.getRegionType().equals(RegionTypeEnum.PROVINCE.getValue())) {
                cityVoList.add(cityVo);
            }
        }
        return cityVoList;
    }

}
