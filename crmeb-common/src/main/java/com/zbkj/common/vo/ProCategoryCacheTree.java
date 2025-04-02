package com.zbkj.common.vo;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品分类树
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
public class ProCategoryCacheTree {

    private List<ProCategoryCacheVo> categoryList = new ArrayList<ProCategoryCacheVo>();

    public ProCategoryCacheTree(List<ProCategoryCacheVo> categoryList) {
        this.categoryList = categoryList;
    }

    //建立树形结构
    public List<ProCategoryCacheVo> buildTree(){
        List<ProCategoryCacheVo> treeCategory = new ArrayList<ProCategoryCacheVo>();
        for(ProCategoryCacheVo categoryNode : getRootNode()) {
            categoryNode = buildChildTree(categoryNode);
            treeCategory.add(categoryNode);
        }
        return sortList(treeCategory);
    }

    // 排序
    private List<ProCategoryCacheVo> sortList(List<ProCategoryCacheVo> treeMenus) {
        treeMenus = treeMenus.stream().sorted(Comparator.comparing(ProCategoryCacheVo::getSort).reversed()).collect(Collectors.toList());
        treeMenus.forEach(e -> {
            if (CollUtil.isNotEmpty(e.getChildList())) {
                e.setChildList(sortList(e.getChildList()));
            }
        });
        return treeMenus;
    }

    //递归，建立子树形结构
    private ProCategoryCacheVo buildChildTree(ProCategoryCacheVo pNode){
        List<ProCategoryCacheVo> childMenus = new ArrayList<ProCategoryCacheVo>();
        for(ProCategoryCacheVo categoryNode : categoryList) {
            if(categoryNode.getPid().equals(pNode.getId())) {
                childMenus.add(buildChildTree(categoryNode));
            }
        }
        pNode.setChildList(childMenus);
        return pNode;
    }

    //获取根节点
    private List<ProCategoryCacheVo> getRootNode() {
        List<ProCategoryCacheVo> rootMenuLists = new  ArrayList<ProCategoryCacheVo>();
        for(ProCategoryCacheVo categoryNode : categoryList) {
            if(categoryNode.getPid().equals(0)) {
                rootMenuLists.add(categoryNode);
            }
        }
        return rootMenuLists;
    }

}
