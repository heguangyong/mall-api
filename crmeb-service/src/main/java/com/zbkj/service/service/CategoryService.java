package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.request.CategoryRequest;
import com.zbkj.common.request.CategorySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.CategoryTreeVo;

import java.util.List;

/**
*   CategoryService 接口
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
public interface CategoryService extends IService<Category> {

    List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest);

    int delete(Integer id);

    /**
     * 获取树形结构数据
     * @param type 分类
     * @param status 状态
     * @param name 名称
     * @return List
     */
    List<CategoryTreeVo> getListTree(Integer type, Integer status, String name);

    List<Category> getByIds(List<Integer> ids);

    Boolean update(CategoryRequest request, Integer id);

    /**
     * 更改基础分类状态
     */
    Boolean updateStatus(Integer id);

    /**
     * 新增分类表
     */
    Boolean create(CategoryRequest categoryRequest);

    /**
     * 获取分类详情
     * @param id 分类ID
     * @return Category
     */
    Category getByIdException(Integer id);

    /**
     * 获取基础分类信息
     * @param id 分类ID
     * @return Category
     */
    Category getBaseCategoryInfo(Integer id);
}
