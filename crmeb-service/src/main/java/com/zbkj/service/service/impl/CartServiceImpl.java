package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.CouponConstants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.dto.ProductPriceCalculateDto;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.cat.Cart;
import com.zbkj.common.model.coupon.Coupon;
import com.zbkj.common.model.coupon.CouponUser;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.product.ProductCategory;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.CartNumRequest;
import com.zbkj.common.request.CartRequest;
import com.zbkj.common.request.CartResetRequest;
import com.zbkj.common.response.CartInfoResponse;
import com.zbkj.common.response.CartMerchantResponse;
import com.zbkj.common.response.CartPriceResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.service.dao.CartDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StoreCartServiceImpl 接口实现
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
public class CartServiceImpl extends ServiceImpl<CartDao, Cart> implements CartService {

    @Resource
    private CartDao dao;

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ProductRelationService productRelationService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private ProductCategoryService productCategoryService;

    /**
     * 列表
     *
     * @param isValid 是否失效
     * @return List<CartMerchantResponse>
     */
    @Override
    public List<CartMerchantResponse> getList(boolean isValid) {
        Integer userId = userService.getUserIdException();
        //带 StoreCart 类的多条件查询
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getUid, userId);
        lqw.eq(Cart::getStatus, isValid);
        lqw.orderByDesc(Cart::getId);
        List<Cart> cartList = dao.selectList(lqw);
        if (CollUtil.isEmpty(cartList)) {
            return CollUtil.newArrayList();
        }

        List<Integer> merIdList = cartList.stream().map(Cart::getMerId).distinct().collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMerIdMapByIdList(merIdList);
        List<CartMerchantResponse> responseList = CollUtil.newArrayList();
        merIdList.forEach(merId -> {
            CartMerchantResponse merchantResponse = new CartMerchantResponse();
            merchantResponse.setMerId(merId);
            merchantResponse.setMerName(merchantMap.get(merId).getName());
            merchantResponse.setMerIsSelf(merchantMap.get(merId).getIsSelf());
            List<Cart> merCartList = cartList.stream().filter(e -> e.getMerId().equals(merId)).collect(Collectors.toList());
            List<CartInfoResponse> infoResponseList = merCartList.stream().map(storeCart -> {
                CartInfoResponse cartInfoResponse = new CartInfoResponse();
                BeanUtils.copyProperties(storeCart, cartInfoResponse);
                // 获取商品信息
                Product product = productService.getCartByProId(storeCart.getProductId());
                cartInfoResponse.setImage(product.getImage());
                cartInfoResponse.setProName(product.getName());
                cartInfoResponse.setDeliveryMethod(product.getDeliveryMethod());
                if (!isValid) {// 失效商品直接掠过
                    cartInfoResponse.setAttrStatus(false);
                    return cartInfoResponse;
                }
                // 获取对应的商品规格信息(只会有一条信息)
                ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(storeCart.getProductId(),
                        storeCart.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
                // 规格不存在即失效
                if (ObjectUtil.isNull(attrValue) || !attrValue.getIsShow()) {
                    cartInfoResponse.setAttrStatus(false);
                    return cartInfoResponse;
                }
                if (StrUtil.isNotBlank(attrValue.getImage())) {
                    cartInfoResponse.setImage(attrValue.getImage());
                }
                cartInfoResponse.setSku(attrValue.getSku());
                cartInfoResponse.setPrice(attrValue.getPrice());
                cartInfoResponse.setAttrId(attrValue.getId());
                cartInfoResponse.setAttrStatus(attrValue.getStock() > 0);
                cartInfoResponse.setStock(attrValue.getStock());
                if (product.getIsPaidMember()) {
                    cartInfoResponse.setVipPrice(attrValue.getVipPrice());
                    cartInfoResponse.setIsPaidMember(true);
                }
                return cartInfoResponse;
            }).collect(Collectors.toList());
            merchantResponse.setCartInfoList(infoResponseList);
            responseList.add(merchantResponse);
        });
        return responseList;
    }

    /**
     * 购物车数量
     *
     * @param request 请求参数
     * @return Map<String, Integer>
     */
    @Override
    public Map<String, Integer> getUserCount(CartNumRequest request) {
        Integer userId = userService.getUserIdException();
        Map<String, Integer> map = new HashMap<>();
        int num;
        if (request.getType().equals("total")) {
            num = getUserCountByStatus(userId, request.getNumType());
        } else {
            num = getUserSumByStatus(userId, request.getNumType());
        }
        map.put("count", num);
        return map;
    }

    /**
     * 添加购物车
     *
     * @param request 新增购物车参数
     * @return 新增结果
     */
    @Override
    public Boolean add(CartRequest request) {
        // 判断商品正常
        Product product = getAwaitAddProduct(request.getProductId());
        ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(product.getId(), request.getProductAttrUnique(),
                ProductConstants.PRODUCT_TYPE_NORMAL, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (ObjectUtil.isNull(attrValue) || !attrValue.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_ATTR_VALUE_NOT_EXIST);
        }
        if (attrValue.getStock() < request.getCartNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "货物库存不足");
        }
        if (product.getSystemFormId() > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "关联自定义系统表单的商品，不支持加购物车");
        }

        // 普通商品部分(只有普通商品才能添加购物车)
        // 是否已经有同类型商品在购物车，有则添加数量没有则新增
        User user = userService.getInfo();
        Cart forUpdateStoreCart = getByUniqueAndUid(request.getProductAttrUnique(), user.getId());
        Boolean execute = transactionTemplate.execute(e -> {
            if (ObjectUtil.isNotNull(forUpdateStoreCart)) { // 购物车添加数量
                forUpdateStoreCart.setCartNum(forUpdateStoreCart.getCartNum() + request.getCartNum());
                boolean updateResult = updateById(forUpdateStoreCart);
                if (!updateResult) throw new CrmebException("添加购物车失败");
            } else {// 新增购物车数据
                Cart storeCart = new Cart();
                BeanUtils.copyProperties(request, storeCart);
                storeCart.setUid(user.getId());
                storeCart.setMerId(product.getMerId());
                if (dao.insert(storeCart) <= 0) throw new CrmebException("添加购物车失败");
            }
            return Boolean.TRUE;
        });
        if (execute) {
            purchaseStatistics(request.getProductId(), request.getCartNum());
        }
        return execute;
    }

    /**
     * 获取待添加商品
     * @param productId 商品ID
     */
    private Product getAwaitAddProduct(Integer productId) {
        Product product = productService.getById(productId);
        if (ObjectUtil.isNull(product) || product.getIsDel() || !product.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        if (product.getMarketingType() > ProductConstants.PRODUCT_MARKETING_TYPE_BASE) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "营销商品不支持加入购物车");
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CLOUD)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "云盘商品不支持加入购物车");
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_CDKEY)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "卡密商品不支持加入购物车");
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_COMPONENT)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "视频号商品不支持加入购物车");
        }
        if (product.getType().equals(ProductConstants.PRODUCT_TYPE_VIRTUALLY)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "虚拟商品不支持加入购物车");
        }
        return product;
    }

    /**
     * 新增商品至购物车
     *
     * @param cartListRequest 购物车参数
     * @return 添加后的成功标识
     */
    @Override
    public Boolean batchAdd(List<CartRequest> cartListRequest) {
        if (CollUtil.isEmpty(cartListRequest)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物车添加数据为空");
        }
        List<Cart> updateCartList = new ArrayList<>();
        List<Cart> addCartList = new ArrayList<>();
        User currentUser = userService.getInfo();
        cartListRequest.forEach(cartRequest -> {
            // 判断商品正常
            Product product = getAwaitAddProduct(cartRequest.getProductId());
            ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(cartRequest.getProductId(),
                    cartRequest.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
            if (ObjectUtil.isNull(attrValue) || !attrValue.getIsShow()) {
                throw new CrmebException(ProductResultCode.PRODUCT_ATTR_VALUE_NOT_EXIST);
            }
            if (attrValue.getStock() < cartRequest.getCartNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "货物库存不足");
            }
            // 普通商品部分(只有普通商品才能添加购物车)
            // 是否已经有同类型商品在购物车，有则添加数量没有则新增
            Cart forUpdateStoreCart = getByUniqueAndUid(cartRequest.getProductAttrUnique(), currentUser.getId());
            if (ObjectUtil.isNotNull(forUpdateStoreCart)) { // 购物车添加数量
                forUpdateStoreCart.setCartNum(forUpdateStoreCart.getCartNum() + cartRequest.getCartNum());
                updateCartList.add(forUpdateStoreCart);
            } else {// 新增购物车数据
                Cart cart = new Cart();
                BeanUtils.copyProperties(cartRequest, cart);
                cart.setUid(currentUser.getId());
                cart.setMerId(product.getMerId());
                addCartList.add(cart);
            }
        });

        Boolean execute = transactionTemplate.execute(exec -> {
            saveBatch(addCartList);
            updateBatchById(updateCartList);
            return Boolean.TRUE;
        });
        if (execute) {
            cartListRequest.forEach(cart -> purchaseStatistics(cart.getProductId(), cart.getCartNum()));
        }
        return execute;
    }

    /**
     * 加购商品统计
     *
     * @param productId 商品id
     * @param num       加购数量
     */
    private void purchaseStatistics(Integer productId, Integer num) {
        String todayStr = DateUtil.date().toString(DateConstants.DATE_FORMAT_DATE);
        // 商品加购量统计(每日/商城)
        redisUtil.incrAndCreate(RedisConstants.PRO_ADD_CART_KEY + todayStr, num);
        // 商品加购量统计(每日/个体)
        redisUtil.incrAndCreate(StrUtil.format(RedisConstants.PRO_PRO_ADD_CART_KEY, todayStr, productId), num);
    }

    /**
     * 获取购物车信息
     *
     * @param productAttrUnique 商品规制值
     * @param uid               uid
     * @return StoreCart
     */
    private Cart getByUniqueAndUid(Integer productAttrUnique, Integer uid) {
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.eq(Cart::getProductAttrUnique, productAttrUnique);
        lqw.eq(Cart::getUid, uid);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 删除购物车信息
     *
     * @param ids 待删除id
     * @return 删除结果状态
     */
    @Override
    public Boolean deleteCartByIds(List<Integer> ids) {
        return dao.deleteBatchIds(ids) > 0;
    }

    /**
     * 检测商品是否有效 更新购物车商品状态
     *
     * @param productId 商品id
     * @return 跟新结果
     */
    @Override
    public Boolean productStatusNotEnable(Integer productId) {
        LambdaUpdateWrapper<Cart> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Cart::getStatus, false);
        wrapper.eq(Cart::getProductId, productId);
        wrapper.eq(Cart::getStatus, true);
        return update(wrapper);
    }

    /**
     * 购物车重选
     *
     * @param resetRequest 重选数据
     * @return 重选结果
     */
    @Override
    public Boolean resetCart(CartResetRequest resetRequest) {
        Cart cart = getById(resetRequest.getId());
        if (ObjectUtil.isNull(cart)) throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物车不存在");
        // 判断商品正常
        Product product = getAwaitAddProduct(resetRequest.getProductId());
        ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(product.getId(),
                resetRequest.getUnique(), ProductConstants.PRODUCT_TYPE_NORMAL, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (ObjectUtil.isNull(attrValue) || !attrValue.getIsShow()) {
            throw new CrmebException(ProductResultCode.PRODUCT_ATTR_VALUE_NOT_EXIST);
        }
        if (attrValue.getStock() < resetRequest.getNum()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "货物库存不足");
        }
        cart.setCartNum(resetRequest.getNum());
        cart.setProductAttrUnique(resetRequest.getUnique());
        cart.setStatus(true);
        cart.setMerId(product.getMerId());
        boolean updateResult = dao.updateById(cart) > 0;
        if (!updateResult) throw new CrmebException("重选添加购物车失败");
        purchaseStatistics(cart.getProductId(), resetRequest.getNum());
        return updateResult;
    }

    /**
     * 对应sku购物车生效
     *
     * @param skuIdList skuIdList
     * @return Boolean
     */
    @Override
    public Boolean productStatusNoEnable(List<Integer> skuIdList) {
        LambdaUpdateWrapper<Cart> lqw = new LambdaUpdateWrapper<>();
        lqw.set(Cart::getStatus, true);
        lqw.in(Cart::getProductAttrUnique, skuIdList);
        return update(lqw);
    }

    /**
     * 删除商品对应的购物车
     *
     * @param productId 商品id
     */
    @Override
    public Boolean productDelete(Integer productId) {
        LambdaUpdateWrapper<Cart> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Cart::getProductId, productId);
        return dao.delete(wrapper) > 0;
    }

    /**
     * 通过id和uid获取购物车信息
     *
     * @param id  购物车id
     * @param uid 用户uid
     * @return StoreCart
     */
    @Override
    public Cart getByIdAndUid(Integer id, Integer uid) {
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.eq(Cart::getId, id);
        lqw.eq(Cart::getUid, uid);
        lqw.eq(Cart::getStatus, true);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 修改购物车商品数量
     *
     * @param id     购物车id
     * @param number 数量
     */
    @Override
    public Boolean updateCartNum(Integer id, Integer number) {
        if (number <= 0 || number > 999)
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "加购数不能小于1且大于999");
        Cart storeCart = getById(id);
        if (ObjectUtil.isNull(storeCart)) throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前购物车不存在");
        if (storeCart.getCartNum().equals(number)) {
            return Boolean.TRUE;
        }
        ProductAttrValue attrValue = productAttrValueService.getByProductIdAndAttrId(storeCart.getProductId(),
                storeCart.getProductAttrUnique(), ProductConstants.PRODUCT_TYPE_NORMAL, ProductConstants.PRODUCT_MARKETING_TYPE_BASE);
        if (ObjectUtil.isNull(attrValue)) {
            throw new CrmebException(ProductResultCode.PRODUCT_ATTR_VALUE_NOT_EXIST);
        }
        if (attrValue.getStock() < number) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "货物库存不足");
        }
        storeCart.setCartNum(number);
        return updateById(storeCart);
    }

    /**
     * 购物车移入收藏
     *
     * @param ids 购物车id列表
     * @return Boolean
     */
    @Override
    public Boolean toCollect(List<Integer> ids) {
        Integer userId = userService.getUserIdException();
        List<Cart> cartList = findListByIds(ids);
        if (CollUtil.isEmpty(cartList)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物车不存在");
        }
        if (cartList.size() != ids.size()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物车数据异常，请重新勾选");
        }
        List<Integer> proIdList = cartList.stream().map(Cart::getProductId).distinct().collect(Collectors.toList());
        return transactionTemplate.execute(e -> {
            deleteCartByIds(ids);
            productRelationService.deleteByUidAndProIdList(userId, proIdList);
            productRelationService.batchAdd(userId, proIdList);
            return Boolean.TRUE;
        });
    }

    /**
     * 通过用户id删除
     *
     * @param uid 用户ID
     */
    @Override
    public Boolean deleteByUid(Integer uid) {
        LambdaUpdateWrapper<Cart> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(Cart::getUid, uid);
        return remove(wrapper);
    }

    /**
     * 购物车价格计算
     *
     * @param ids 购物车ID列表
     * @return CartPriceResponse
     */
    @Override
    public CartPriceResponse calculatePrice(List<Integer> ids) {
        Integer userId = userService.getUserIdException();
        List<Cart> cartList = findListByIds(ids);
        User user = userService.getById(userId);

        List<Integer> proIdList = new ArrayList<>();
        Map<Integer, ProductAttrValue> attrValueMap = new HashMap<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal proTotalPrice = BigDecimal.ZERO;
        List<Product> productList = new ArrayList<>();
        List<ProductPriceCalculateDto> dtoList = new ArrayList<>();
        for (Cart cart : cartList) {
            if (!cart.getStatus()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物扯商品状态异常，请重新选择商品");
            }
            // 查询商品信息
            Product product = productService.getById(cart.getProductId());
            if (ObjectUtil.isNull(product) || product.getIsDel()) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (!product.getIsShow()) {
                throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
            }
            if (product.getStock().equals(0) || cart.getCartNum() > product.getStock()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
            }
            // 查询商品规格属性值信息
            ProductAttrValue attrValue = productAttrValueService.getByIdAndProductIdAndType(cart.getProductAttrUnique(), cart.getProductId(), ProductConstants.PRODUCT_TYPE_NORMAL);
            if (ObjectUtil.isNull(attrValue)) {
                throw new CrmebException(ProductResultCode.PRODUCT_ATTR_VALUE_NOT_EXIST);
            }
            if (attrValue.getStock() < cart.getCartNum()) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购买的商品库存不足");
            }
            if (!proIdList.contains(product.getId())) {
                productList.add(product);
                proIdList.add(product.getId());
            }
            attrValue.setIsPaidMember(product.getIsPaidMember());
            attrValueMap.put(attrValue.getId(), attrValue);

            totalPrice = totalPrice.add(attrValue.getPrice().multiply(new BigDecimal(cart.getCartNum().toString())));
            if (product.getIsPaidMember() && user.getIsPaidMember()) {
                proTotalPrice = proTotalPrice.add(attrValue.getVipPrice().multiply(new BigDecimal(cart.getCartNum().toString())));
            } else {
                proTotalPrice = proTotalPrice.add(attrValue.getPrice().multiply(new BigDecimal(cart.getCartNum().toString())));
            }

            ProductPriceCalculateDto dto = new ProductPriceCalculateDto();
            dto.setProductId(product.getId());
            dto.setAttrValueId(attrValue.getId());
            dto.setMerchantId(product.getMerId());
            dto.setNum(cart.getCartNum());
            dto.setPrice(attrValue.getPrice());
            dto.setVipPrice(attrValue.getVipPrice());
            dto.setIsPaidMember(product.getIsPaidMember());
            dtoList.add(dto);
        }
        // 自动选择最合适的优惠券
        Map<Integer, List<Cart>> merCartMap = cartList.stream().collect(Collectors.groupingBy(Cart::getMerId));
        List<Integer> merIdList = new ArrayList<>(merCartMap.keySet());
        BigDecimal merCouponPrice = BigDecimal.ZERO;
        for (Map.Entry<Integer, List<Cart>> entry : merCartMap.entrySet()) {
            Integer merId = entry.getKey();
            List<Cart> carts = entry.getValue();
            BigDecimal merProTotalPrice = BigDecimal.ZERO;
            for (Cart cart : carts) {
                ProductAttrValue attrValue = attrValueMap.get(cart.getProductAttrUnique());
                BigDecimal multiply;
                if (attrValue.getIsPaidMember() && user.getIsPaidMember()) {
                    multiply = attrValue.getVipPrice().multiply(new BigDecimal(cart.getCartNum().toString()));
                } else {
                    multiply = attrValue.getPrice().multiply(new BigDecimal(cart.getCartNum().toString()));
                }
                merProTotalPrice = merProTotalPrice.add(multiply);
            }
            List<Integer> merProIdList = carts.stream().map(Cart::getProductId).distinct().collect(Collectors.toList());
            List<Coupon> merCouponList = couponService.findManyByMerIdAndMoney(merId, merProIdList, merProTotalPrice);
            if (CollUtil.isNotEmpty(merCouponList)) {
                for (int i = 0; i < merCouponList.size(); ) {
                    Coupon coupon = merCouponList.get(i);
                    if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                        i++;
                        continue;
                    }
                    List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                    List<Cart> merCartList = carts.stream().filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                    BigDecimal proPrice = merCartList.stream()
                            .map(e -> {
                                BigDecimal price;
                                ProductAttrValue productAttrValue = attrValueMap.get(e.getProductAttrUnique());
                                if (productAttrValue.getIsPaidMember() && user.getIsPaidMember()) {
                                    price = productAttrValue.getVipPrice().multiply(new BigDecimal(e.getCartNum().toString()));
                                } else {
                                    price = productAttrValue.getPrice().multiply(new BigDecimal(e.getCartNum().toString()));
                                }
                                return price;
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                        merCouponList.remove(i);
                        continue;
                    }
                    if (proPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                        merCouponList.remove(i);
                        continue;
                    }
                    i++;
                }
            }

            // 查询适用的用户优惠券
            List<CouponUser> merCouponUserList = couponUserService.findManyByUidAndMerIdAndMoneyAndProList(userId, merId, merProIdList, merProTotalPrice);
            if (CollUtil.isNotEmpty(merCouponUserList)) {
                for (int i = 0; i < merCouponUserList.size(); ) {
                    CouponUser couponUser = merCouponUserList.get(i);
                    if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
                        i++;
                        continue;
                    }
                    Coupon coupon = couponService.getById(couponUser.getCouponId());
                    List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                    List<Cart> merCartList = carts.stream().filter(f -> cpIdList.contains(f.getProductId())).collect(Collectors.toList());
                    BigDecimal proPrice = merCartList.stream()
                            .map(e -> {
                                BigDecimal price;
                                ProductAttrValue productAttrValue = attrValueMap.get(e.getProductAttrUnique());
                                if (productAttrValue.getIsPaidMember() && user.getIsPaidMember()) {
                                    price = productAttrValue.getVipPrice().multiply(new BigDecimal(e.getCartNum().toString()));
                                } else {
                                    price = productAttrValue.getPrice().multiply(new BigDecimal(e.getCartNum().toString()));
                                }
                                return price;
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                        merCouponUserList.remove(i);
                        continue;
                    }
                    if (proPrice.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                        merCouponUserList.remove(i);
                        continue;
                    }
                    i++;
                }
            }

            List<Integer> cidList = merCouponUserList.stream().map(CouponUser::getCouponId).collect(Collectors.toList());
            for (int i = 0; i < merCouponList.size(); ) {
                if (cidList.contains(merCouponList.get(i).getId())) {
                    merCouponList.remove(i);
                    continue;
                }
                if (!couponUserService.userIsCanReceiveCoupon(merCouponList.get(i), userId)) {
                    merCouponList.remove(i);
                    continue;
                }
                i++;
            }
            if (CollUtil.isEmpty(merCouponList) && CollUtil.isEmpty(merCouponUserList)) {
                continue;
            }
            if (CollUtil.isEmpty(merCouponList)) {
                CouponUser couponUser = merCouponUserList.get(0);
                merCouponPrice = new BigDecimal(couponUser.getMoney().toString());
                setMerProCouponPrice(dtoList, merId, merCouponPrice, null, couponUser, user.getIsPaidMember());
                continue;
            }
            if (CollUtil.isEmpty(merCouponUserList)) {
                Coupon coupon = merCouponList.get(0);
                merCouponPrice = new BigDecimal(coupon.getMoney().toString());
                setMerProCouponPrice(dtoList, merId, merCouponPrice, coupon, null, user.getIsPaidMember());
                continue;
            }
            Coupon coupon = merCouponList.get(0);
            CouponUser couponUser = merCouponUserList.get(0);
            if (couponUser.getMoney() >= coupon.getMoney()) {
                // 使用用户优惠券
                merCouponPrice = new BigDecimal(couponUser.getMoney().toString());
                setMerProCouponPrice(dtoList, merId, merCouponPrice, null, couponUser, user.getIsPaidMember());
            } else {
                // 领取优惠券下单
                merCouponPrice = new BigDecimal(coupon.getMoney().toString());
                setMerProCouponPrice(dtoList, merId, merCouponPrice, coupon, null, user.getIsPaidMember());
            }
        }

        merCouponPrice = dtoList.stream().map(ProductPriceCalculateDto::getMerCouponPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        // 自动领平台券计算
        // 查所有适用的平台优惠券
        BigDecimal remainingAmount = proTotalPrice.subtract(merCouponPrice);
        List<Integer> proCategoryIdList = productList.stream().map(Product::getCategoryId).collect(Collectors.toList());
        List<Integer> secondParentIdList = productCategoryService.findParentIdByChildIds(proCategoryIdList);
        List<Integer> firstParentIdList = productCategoryService.findParentIdByChildIds(secondParentIdList);
        proCategoryIdList.addAll(secondParentIdList);
        proCategoryIdList.addAll(firstParentIdList);
        List<Integer> brandIdList = productList.stream().map(Product::getBrandId).collect(Collectors.toList());
        List<Coupon> platCouponList = couponService.findManyPlatByMerIdAndMoney(proIdList, proCategoryIdList, merIdList, brandIdList, proTotalPrice);
        for (int i = 0; i < platCouponList.size(); ) {
            Coupon coupon = platCouponList.get(i);
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                if (proTotalPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (remainingAmount.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                BigDecimal proPrice = dtoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(coupon.getMinPrice().toString())) < 0) {
                    platCouponList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponList.remove(i);
                    continue;
                }
            }
            i++;
        }

        // 查询适用的用户平台优惠券
        List<CouponUser> platCouponUserList = couponUserService.findManyPlatByUidAndMerIdAndMoneyAndProList(userId, proIdList, proCategoryIdList, merIdList, brandIdList, proTotalPrice);
        for (int i = 0; i < platCouponUserList.size(); ) {
            CouponUser couponUser = platCouponUserList.get(i);
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_UNIVERSAL)) {
                if (proTotalPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (remainingAmount.compareTo(new BigDecimal(couponUser.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            Coupon coupon = couponService.getById(couponUser.getCouponId());
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
                List<Integer> cpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                BigDecimal proPrice = dtoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> cpIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT_CATEGORY)) {
                List<Integer> cidList = new ArrayList<>();
                Integer categoryId = Integer.valueOf(coupon.getLinkedData());
                ProductCategory category = productCategoryService.getById(categoryId);
                if (category.getLevel().equals(3)) {
                    cidList.add(categoryId);
                } else {
                    List<ProductCategory> productCategoryList = productCategoryService.findAllChildListByPid(category.getId(), category.getLevel());
                    if (category.getLevel().equals(1)) {
                        productCategoryList = productCategoryList.stream().filter(f -> f.getLevel().equals(3)).collect(Collectors.toList());
                    }
                    cidList.addAll(productCategoryList.stream().map(ProductCategory::getId).collect(Collectors.toList()));
                }
                List<Integer> pIdList = productList.stream().filter(f -> cidList.contains(f.getCategoryId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_BRAND)) {
                Integer brandId = Integer.valueOf(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> brandId.equals(f.getBrandId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            if (couponUser.getCategory().equals(CouponConstants.COUPON_CATEGORY_JOINT_MERCHANT)) {
                List<Integer> mpIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
                List<Integer> pIdList = productList.stream().filter(f -> mpIdList.contains(f.getMerId())).map(Product::getId).collect(Collectors.toList());
                BigDecimal proPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal proSubPrice = dtoList.stream().filter(f -> pIdList.contains(f.getProductId()))
                        .map(e -> {
                            BigDecimal price;
                            if (e.getIsPaidMember() && user.getIsPaidMember()) {
                                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
                            } else {
                                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
                            }
                            return price.subtract(e.getMerCouponPrice());
                        }).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (proPrice.compareTo(new BigDecimal(couponUser.getMinPrice().toString())) < 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
                if (proSubPrice.compareTo(new BigDecimal(coupon.getMoney().toString())) <= 0) {
                    platCouponUserList.remove(i);
                    continue;
                }
            }
            i++;
        }

        BigDecimal platCouponPrice = BigDecimal.ZERO;
        List<Integer> platCidList = platCouponUserList.stream().map(CouponUser::getCouponId).collect(Collectors.toList());
        for (int i = 0; i < platCouponList.size(); ) {
            if (platCidList.contains(platCouponList.get(i).getId())) {
                platCouponList.remove(i);
                continue;
            }
            if (!couponUserService.userIsCanReceiveCoupon(platCouponList.get(i), userId)) {
                platCouponList.remove(i);
                continue;
            }
            i++;
        }
        if (CollUtil.isEmpty(platCouponList) && CollUtil.isNotEmpty(platCouponUserList)) {
            platCouponPrice = new BigDecimal(platCouponUserList.get(0).getMoney().toString());
        }
        if (CollUtil.isNotEmpty(platCouponList) && CollUtil.isEmpty(platCouponUserList)) {
            platCouponPrice = new BigDecimal(platCouponList.get(0).getMoney().toString());
        }
        if (CollUtil.isNotEmpty(platCouponList) && CollUtil.isNotEmpty(platCouponUserList)) {
            Long platCouponMoney = platCouponList.get(0).getMoney();
            Long platCouponUserMoney = platCouponUserList.get(0).getMoney();
            if (platCouponUserMoney >= platCouponMoney) {
                // 使用用户优惠券
                platCouponPrice = new BigDecimal(platCouponUserMoney.toString());
            } else {
                // 领取优惠券下单
                platCouponPrice = new BigDecimal(platCouponMoney.toString());
            }
        }

        CartPriceResponse cartPriceResponse = new CartPriceResponse();
        cartPriceResponse.setMerCouponPrice(merCouponPrice);
        cartPriceResponse.setPlatCouponPrice(platCouponPrice);
        cartPriceResponse.setProTotalPrice(totalPrice);
        cartPriceResponse.setSvipDiscountPrice(totalPrice.subtract(proTotalPrice));
        cartPriceResponse.setTotalCouponPrice(platCouponPrice.add(merCouponPrice).add(cartPriceResponse.getSvipDiscountPrice()));
        cartPriceResponse.setTotalPrice(totalPrice.subtract(cartPriceResponse.getTotalCouponPrice()));
        return cartPriceResponse;
    }

    /**
     * 设置dto优惠价格
     */
    private void setMerProCouponPrice(List<ProductPriceCalculateDto> dtoList, Integer merId, BigDecimal merCouponPrice, Coupon coupon, CouponUser couponUser, boolean userIsPaidMember) {
        if (ObjectUtil.isNull(coupon)) {
            coupon = couponService.getById(couponUser.getCouponId());
        }
        List<ProductPriceCalculateDto> proDtoList = new ArrayList<>();
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_MERCHANT)) {
            proDtoList = dtoList.stream().filter(d -> d.getMerchantId().equals(merId)).collect(Collectors.toList());
        }
        if (coupon.getCategory().equals(CouponConstants.COUPON_CATEGORY_PRODUCT)) {
            List<Integer> proIdList = CrmebUtil.stringToArray(coupon.getLinkedData());
            proDtoList = dtoList.stream().filter(d -> proIdList.contains(d.getProductId())).collect(Collectors.toList());
        }
        BigDecimal proTotalPrice = proDtoList.stream().map(e -> {
            BigDecimal price;
            if (e.getIsPaidMember() && userIsPaidMember) {
                price = e.getVipPrice().multiply(new BigDecimal(e.getNum().toString()));
            } else {
                price = e.getPrice().multiply(new BigDecimal(e.getNum().toString()));
            }
            return price;
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal couponPrice = merCouponPrice;
        for (int i = 0; i < proDtoList.size(); i++) {
            ProductPriceCalculateDto d = proDtoList.get(i);
            if (proDtoList.size() == (i + 1)) {
                d.setMerCouponPrice(couponPrice);
                break;
            }
            BigDecimal proPrice;
            if (d.getIsPaidMember() && userIsPaidMember) {
                proPrice = d.getVipPrice().multiply(new BigDecimal(d.getNum().toString()));
            } else {
                proPrice = d.getPrice().multiply(new BigDecimal(d.getNum().toString()));
            }
            BigDecimal ratio = proPrice.divide(proTotalPrice, 10, BigDecimal.ROUND_HALF_UP);
            BigDecimal detailCouponFee = couponPrice.multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            couponPrice = couponPrice.subtract(detailCouponFee);
            d.setMerCouponPrice(detailCouponFee);
        }
    }

    private List<Cart> findListByIds(List<Integer> ids) {
        LambdaQueryWrapper<Cart> lqw = Wrappers.lambdaQuery();
        lqw.in(Cart::getId, ids);
        return dao.selectList(lqw);
    }

    ///////////////////////////////////////////////////////////////////自定义方法

    /**
     * 购物车商品数量（条数）
     *
     * @param userId Integer 用户id
     * @param status Boolean 商品类型：true-有效商品，false-无效商品
     * @return Integer
     */
    private Integer getUserCountByStatus(Integer userId, Boolean status) {
        //购物车商品种类数量
        LambdaQueryWrapper<Cart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Cart::getUid, userId);
        lqw.eq(Cart::getStatus, status);
        return dao.selectCount(lqw);
    }

    /**
     * 购物车购买商品总数量
     *
     * @param userId Integer 用户id
     * @param status 商品类型：true-有效商品，false-无效商品
     * @return Integer
     */
    private Integer getUserSumByStatus(Integer userId, Boolean status) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("ifnull(sum(cart_num), 0) as cart_num");
        queryWrapper.eq("uid", userId);
        queryWrapper.eq("status", status);
        Cart cart = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(cart)) {
            return 0;
        }
        return cart.getCartNum();
    }
}

