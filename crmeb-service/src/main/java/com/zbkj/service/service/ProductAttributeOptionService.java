package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.product.ProductAttributeOption;

import java.util.List;

/**
 * @author HZW
 * @description ProductAttributeOptionService 接口
 * @date 2024-11-11
 */
public interface ProductAttributeOptionService extends IService<ProductAttributeOption> {

    /**
     * 商品变更导致的删除
     */
    Boolean deleteByProductUpdate(Integer proId);

    /**
     * 根据规格ID获取属性列表
     */
    List<ProductAttributeOption> findListByAttrId(Integer attrId);
}