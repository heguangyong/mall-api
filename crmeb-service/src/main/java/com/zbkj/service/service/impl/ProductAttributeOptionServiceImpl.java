package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.product.ProductAttributeOption;
import com.zbkj.service.dao.ProductAttributeOptionDao;
import com.zbkj.service.service.ProductAttributeOptionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description ProductAttributeOptionServiceImpl 接口实现
 * @date 2024-11-11
 */
@Service
public class ProductAttributeOptionServiceImpl extends ServiceImpl<ProductAttributeOptionDao, ProductAttributeOption> implements ProductAttributeOptionService {

    @Resource
    private ProductAttributeOptionDao dao;

    /**
     * 商品变更导致的删除
     */
    @Override
    public Boolean deleteByProductUpdate(Integer proId) {
        LambdaUpdateWrapper<ProductAttributeOption> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ProductAttributeOption::getIsDel, 1);
        wrapper.eq(ProductAttributeOption::getProductId, proId);
        wrapper.eq(ProductAttributeOption::getIsDel, 0);
        return update(wrapper);
    }

    /**
     * 根据规格ID获取属性列表
     */
    @Override
    public List<ProductAttributeOption> findListByAttrId(Integer attrId) {
        LambdaQueryWrapper<ProductAttributeOption> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttributeOption::getAttributeId, attrId);
        lqw.eq(ProductAttributeOption::getIsDel, 0);
        lqw.orderByAsc(ProductAttributeOption::getSort);
        return dao.selectList(lqw);
    }
}

