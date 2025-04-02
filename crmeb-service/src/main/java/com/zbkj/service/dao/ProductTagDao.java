package com.zbkj.service.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.product.ProductTag;
import com.zbkj.common.response.ProductTagTaskItem;
import com.zbkj.common.response.ProductTagTaskResponse;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dazongzi
 * @since 2023-10-11
 */
public interface ProductTagDao extends BaseMapper<ProductTag> {

    /** 爆品
     * 从订单表查询近30天内的全部订单，根据商品分组后每个商品的订单中的销售量统计当前商品的销售量
     * @param map 参数多少个订单 数字必传
     * @return 当前的爆品商品数据
     */

    List<ProductTagTaskItem> calcProductTagForLastThirtyDaysSalesByOptionUnit(Map<String, Object> map);

    /**
     * 自营标签
     * @return 当前的自营商品列表
     */
    List<ProductTagTaskItem> calcProductTagForIsSelf();

    /**
     * 热卖
     * @return 符合条件了热卖商品
     */
    List<ProductTagTaskItem> calcProductTagForReplayCount(Map<String, Object> map);

    /**
     *  优选
     * @return 符合条件的五星评价
     */
    List<ProductTagTaskItem> calcProductTagForFiveStart(Map<String, Object> map);

    /**
     * 新品
     * @param map 新品查询天数
     * @return 符合条件的新品商品
     */
    List<ProductTagTaskItem> calcProductTagForNewGoods(Map<String, Object> map);

    /**
     *  包邮标签
     * @return 符合包邮标签的商品
     */
    List<ProductTagTaskItem> calcProductTagForFreeDelivery();
}
