package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.cat.Cart;
import com.zbkj.common.request.CartNumRequest;
import com.zbkj.common.request.CartRequest;
import com.zbkj.common.request.CartResetRequest;
import com.zbkj.common.response.CartMerchantResponse;
import com.zbkj.common.response.CartPriceResponse;

import java.util.List;
import java.util.Map;

/**
 * StoreCartService 接口
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
public interface CartService extends IService<Cart> {

    /**
     * 根据有效标识符获取出数据
     * @param isValid 是否失效
     * @return 购物车列表
     */
    List<CartMerchantResponse> getList(boolean isValid);

    /**
     * 获取当前购物车数量
     * @param request 请求参数
     * @return 数量
     */
    Map<String, Integer> getUserCount(CartNumRequest request);

    /**
     * 添加购物车
     * @param request 新增购物车参数
     * @return 新增结果
     */
    Boolean add(CartRequest request);

    /**
     * 新增购物车数据
     * @param cartListRequest 新增购物车参数
     * @return 新增结果
     */
    Boolean batchAdd(List<CartRequest> cartListRequest);

    /**
     * 删除购物车
     * @param ids 待删除id
     * @return 返回删除状态
     */
    Boolean deleteCartByIds(List<Integer> ids);


    /**
     * 检测商品是否有效 更新购物车商品状态
     * @param productId 商品id
     * @return 跟新结果
     */
    Boolean productStatusNotEnable(Integer productId);

    /**
     * 购物车重选提交
     * @param resetRequest 重选数据
     * @return 提交结果
     */
    Boolean resetCart(CartResetRequest resetRequest);

    /**
     * 对应sku购物车生效
     * @param skuIdList skuIdList
     */
    Boolean productStatusNoEnable(List<Integer> skuIdList);

    /**
     * 删除商品对应的购物车
     * @param productId 商品id
     */
    Boolean productDelete(Integer productId);

    /**
     * 通过id和uid获取购物车信息
     * @param id 购物车id
     * @param uid 用户uid
     * @return StoreCart
     */
    Cart getByIdAndUid(Integer id, Integer uid);

    /**
     * 修改购物车商品数量
     * @param id 购物车id
     * @param number 数量
     */
    Boolean updateCartNum(Integer id, Integer number);

    /**
     * 购物车移入收藏
     * @param ids 购物车id列表
     * @return Boolean
     */
    Boolean toCollect(List<Integer> ids);

    /**
     * 通过用户id删除
     * @param uid 用户ID
     */
    Boolean deleteByUid(Integer uid);

    /**
     * 购物车价格计算
     * @param ids 购物车ID列表
     * @return CartPriceResponse
     */
    CartPriceResponse calculatePrice(List<Integer> ids);
}
