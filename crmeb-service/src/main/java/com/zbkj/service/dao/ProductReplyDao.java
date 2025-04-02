package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.product.ProductReply;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface ProductReplyDao extends BaseMapper<ProductReply> {

    List<ProductReply> getMerchantAdminPage(Map<String, Object> map);

    List<ProductReply> getPlatAdminPage(Map<String, Object> map);
}
