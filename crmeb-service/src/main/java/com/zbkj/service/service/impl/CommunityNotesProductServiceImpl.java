package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.community.CommunityNotesProduct;
import com.zbkj.common.model.product.Product;
import com.zbkj.service.dao.community.CommunityNotesProductDao;
import com.zbkj.service.service.CommunityNotesProductService;
import com.zbkj.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* CommunityNotesProduct 接口实现
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
public class CommunityNotesProductServiceImpl extends ServiceImpl<CommunityNotesProductDao, CommunityNotesProduct> implements CommunityNotesProductService {

    @Resource
    private CommunityNotesProductDao dao;

    @Autowired
    private ProductService productService;

    /**
     * 获取笔记关联商品
     * @param noteId 笔记ID
     */
    @Override
    public List<CommunityNotesProduct> findListByNoteId(Integer noteId) {
        LambdaQueryWrapper<CommunityNotesProduct> lqw = Wrappers.lambdaQuery();
        lqw.eq(CommunityNotesProduct::getNoteId, noteId);
        List<CommunityNotesProduct> list = dao.selectList(lqw);
        list.forEach(np -> {
            Product product = productService.getById(np.getProductId());
            if (ObjectUtil.isNotNull(product)) {
                np.setProductName(product.getName());
                np.setProductImage(product.getImage());
                np.setPrice(product.getPrice());
                np.setOtPrice(product.getOtPrice());
            }
        });
        return list;
    }

    /**
     * 通过笔记ID删除关联商品
     * @param noteId 笔记ID
     */
    @Override
    public void deleteByNoteId(Integer noteId) {
        LambdaUpdateWrapper<CommunityNotesProduct> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(CommunityNotesProduct::getNoteId, noteId);
        dao.delete(wrapper);
    }
}

