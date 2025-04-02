package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.order.Order;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.ProductTag;
import com.zbkj.common.model.seckill.SeckillProduct;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.producttag.ProductTagSearchRequest;
import com.zbkj.common.response.ProductTagTaskItem;
import com.zbkj.common.response.ProductTagsFrontResponse;
import com.zbkj.common.response.productTag.ProductTagsForSearchResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.dao.OrderDao;
import com.zbkj.service.dao.OrderDetailDao;
import com.zbkj.service.dao.ProductTagDao;
import com.zbkj.service.dao.SeckillProductDao;
import com.zbkj.service.service.ProductTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dazongzi
 * @description ProductTagServiceImpl 接口实现
 * @date 2023-10-11
 */
@Service
public class ProductTagServiceImpl extends ServiceImpl<ProductTagDao, ProductTag> implements ProductTagService {
    private final Logger LOGGER = LoggerFactory.getLogger(ProductTagService.class);

    @Autowired
    CrmebConfig crmebConfig;

    @Resource
    private ProductTagDao dao;
    @Resource
    private OrderDao orderdao;

    @Resource
    private OrderDetailDao orderDetailDao;

    @Resource
    private SeckillProductDao seckillProductDao;


    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<ProductTag>
     * @author dazongzi
     * @since 2023-10-11
     */
    @Override
    public List<ProductTag> getList(ProductTagSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 ProductTag 类的多条件查询
        LambdaQueryWrapper<ProductTag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lambdaQueryWrapper.like(ProductTag::getTagName, URLUtil.decode(request.getKeywords()));
        }
        lambdaQueryWrapper.orderByDesc(ProductTag::getSort);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 编辑商品标签
     *
     * @param productTag 标签对象
     * @return 编辑结果
     */
    @Override
    public Boolean editProductTag(ProductTag productTag) {
        // 检查标签名称是否存在
        List<ProductTag> exitProductTags = checkTagNameOnlyOne(productTag);
        if (CollUtil.isNotEmpty(exitProductTags)) {
            List<ProductTag> seamTagNameList = exitProductTags.stream().filter(tag -> tag.getTagName().equals(productTag.getTagName())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(seamTagNameList)) {
                List<ProductTag> otherNameSeam = seamTagNameList.stream().filter(tag -> !tag.getId().equals(productTag.getId())).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(otherNameSeam)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "标签名称已经存在！");
                }
            }
        }

        checkProductTagStatusOnLimit(productTag, productTag.getStatus(), Boolean.TRUE);
        ProductTag forProductTagUpdate = new ProductTag();
        forProductTagUpdate.setId(productTag.getId());
        forProductTagUpdate.setPosition(productTag.getPosition());
        forProductTagUpdate.setPlayProducts(productTag.getPlayProducts());
        forProductTagUpdate.setSort(productTag.getSort());
        forProductTagUpdate.setStatus(productTag.getStatus());
        forProductTagUpdate.setStartTime(productTag.getStartTime());
        forProductTagUpdate.setEndTime(productTag.getEndTime());
        // owner = 0 的是系统内置的，内置的只能修改配置数据不能修改标签名称
        if (productTag.getOwner() > 0) {
            forProductTagUpdate.setTagName(productTag.getTagName());
            forProductTagUpdate.setTagNote(productTag.getTagNote());
        }
        return updateById(productTag);
    }

    /**
     * 新增商品标签
     *
     * @param productTag 待新增对象
     * @return 新增结果
     */
    @Override
    public Boolean saveProductTag(ProductTag productTag) {
        // 检查标签名称是否存在
        List<ProductTag> exitProductTags = checkTagNameOnlyOne(productTag);
        if (CollUtil.isNotEmpty(exitProductTags)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "标签名称已存在！");
        }
        checkProductTagStatusOnLimit(productTag, productTag.getStatus(), Boolean.FALSE);
        return dao.insert(productTag) > 0;
    }


    /**
     * 更新商品标签
     *
     * @param id     标签id
     * @param status 状态#0关闭|1开启
     * @return 结果
     */
    @Override
    public Boolean updateStatus(Integer id, Integer status) {

        checkProductTagStatusOnLimit(getById(id), status, Boolean.FALSE);
        LambdaUpdateWrapper<ProductTag> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(ProductTag::getId, id);
        updateWrapper.set(ProductTag::getStatus, status);
        return update(updateWrapper);
    }


    /**
     * 删除商品标签 仅仅能删除平台自建的
     *
     * @param id 待删除id
     * @return 删除结果
     */
    @Override
    public Boolean deleteProductTag(Integer id) {
        ProductTag productTagForDelete = getById(id);
        if (null == productTagForDelete || ObjectUtil.isNull(productTagForDelete.getId())) return Boolean.TRUE;
        if (productTagForDelete.getOwner().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "平台内置商品标签，不能删除");
        }
        if (productTagForDelete.getStatus() == 1) {
            throw new CrmebException(ProductResultCode.PRODUCT_TAG_STATUS_TRUE);
        }
        return removeById(id);
    }

    /**
     * @param productId           商品id
     * @param productTagsResponse 标签对象
     * @return
     */
    @Override
    public ProductTagsFrontResponse setProductTagByProductTagsRules(Integer productId, Integer brandId, Integer merId, Integer cateId, ProductTagsFrontResponse productTagsResponse) {
        Map<Integer, String> productTags = getProductTags();
        ProductTagsFrontResponse tagsFront = new ProductTagsFrontResponse();
        if (productTags.containsKey(productId)) {
            List<ProductTagTaskItem> items = JSON.parseArray(productTags.get(productId), ProductTagTaskItem.class);
            for (ProductTagTaskItem item : items) {
                // 商城出现位置 =》 0标题下，1标题前 标题前最多展示1个标签，标签下最多展示3个标签；系统根据标签顺序进行展示
                setProductTags(item, tagsFront);
            }
        }


        // 设置自定义标签
        String selfProductTagsString = productTags.get(0);
        List<ProductTag> selfProductList = JSON.parseArray(selfProductTagsString, ProductTag.class);
        if (ObjectUtil.isNotNull(selfProductList) && !selfProductList.isEmpty()) {
            for (ProductTag productTag : selfProductList) {
                ProductTagsFrontResponse selfTag = tagsFront;
                List<ProductTagTaskItem> leftItems = tagsFront.getLocationLeftTitle();
                List<ProductTagTaskItem> underItems = tagsFront.getLocationUnderTitle();
                // 这里需要对当前商品标签上线3条做判断，提高效率
                if (!leftItems.isEmpty() && underItems.size() >= 3) {
                    return productTagsResponse;
                }
                switch (productTag.getPlayType()) {
                    case "product":
                        setProductTagForSelfTagConfig(productTag, leftItems, underItems, selfTag, productId);
                        continue;
                    case "brand":
                        setProductTagForSelfTagConfig(productTag, leftItems, underItems, selfTag, brandId);
                        continue;
                    case "merchant":
                        setProductTagForSelfTagConfig(productTag, leftItems, underItems, selfTag, merId);
                        continue;
                    case "category":
                        setProductTagForSelfTagConfig(productTag, leftItems, underItems, selfTag, cateId);
                }

            }
        }
        return tagsFront;
    }


    /**
     * 获取已经开启的商品标签列表
     *
     * @return 商品标签列表
     */
    @Override
    public List<ProductTag> getStatusOnProductTagList() {
        LambdaQueryWrapper<ProductTag> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ProductTag::getStatus, 1);
        List<ProductTag> productTags = dao.selectList(lambdaQueryWrapper);
        productTags = productTags.stream().filter(item -> item.getStartTime().compareTo(CrmebDateUtil.nowDateTime()) <= 0
                && item.getEndTime().compareTo(CrmebDateUtil.nowDateTime()) > 0).collect(Collectors.toList());
        return productTags;
    }

    /**
     * 根据商品标签id加载商品id集合
     *
     * @param tagId 标签id
     * @return 商品id集合
     */
    @Override
    public ProductTagsForSearchResponse getProductIdListByProductTagId(Integer tagId) {
        ProductTag currentTag = getById(tagId);
        ProductTagsForSearchResponse searchResponse = new ProductTagsForSearchResponse();
        // 系统内置标签处理
        if (currentTag.getOwner().equals(0)) {
            Map<Integer, String> productTags = getProductTags();
            for (Map.Entry<Integer, String> entry : productTags.entrySet()) {
                List<ProductTagTaskItem> productTagTaskItems = JSON.parseArray(entry.getValue(), ProductTagTaskItem.class);
                // 查找和标签相同的商品集合
                List<ProductTagTaskItem> isSeamTagList = productTagTaskItems.stream().filter(item -> {
                    return item.getTagName().equals(currentTag.getTagName());
                }).collect(Collectors.toList());
                List<Integer> isConformProductIdList = isSeamTagList.stream().map(ProductTagTaskItem::getId).collect(Collectors.toList());
                searchResponse.getProductIds().addAll(isConformProductIdList);
            }
        }
        // 自定义标签处理
        if (currentTag.getOwner().equals(1)) {
            switch (currentTag.getPlayType()) {
                case "product":
                    searchResponse.getProductIds().addAll(Arrays.stream(currentTag.getPlayProducts().split(",")).map(Integer::parseInt).collect(Collectors.toList()));
                    break;
                case "brand":
                    searchResponse.setBrandId(Arrays.stream(currentTag.getPlayProducts().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    break;
                case "merchant":
                    searchResponse.setMerId(Arrays.stream(currentTag.getPlayProducts().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
                    break;
                case "category":
                    searchResponse.setCategoryId(Arrays.stream(currentTag.getPlayProducts().split(",")).map(Integer::valueOf).collect(Collectors.toList()));
            }
        }
        return searchResponse;
    }

    /**
     * 获取商品标签，根据系统配置得条件
     *
     * @return 获取并根据条件缓存商品标签数据
     */
    private Map<Integer, String> getProductTags() {
        String cacheKey = "product_tag";
        // 此对象key=0存储自定义标签数据，其余均为正常数据
        Map<Integer, String> cacheProductTags = new HashMap<>();
        Map<Integer, List<ProductTagTaskItem>> cacheProduct = new HashMap<>();
        // 根据条件查询符合对应商品标签的商品并缓存
        LambdaQueryWrapper<ProductTag> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ProductTag::getStatus, 1);
        lambdaQueryWrapper.eq(ProductTag::getOwner, 0);
        lambdaQueryWrapper.orderByDesc(ProductTag::getSort);
        List<ProductTag> systemIsShowDefaultProductTags = dao.selectList(lambdaQueryWrapper);
        systemIsShowDefaultProductTags = systemIsShowDefaultProductTags.stream().filter(item -> item.getStartTime().compareTo(CrmebDateUtil.nowDateTime()) <= 0
                && item.getEndTime().compareTo(CrmebDateUtil.nowDateTime()) > 0).collect(Collectors.toList());
        for (ProductTag productTag : systemIsShowDefaultProductTags) {
            if ("新品".equals(productTag.getTagName())) {
                List<ProductTagTaskItem> boomGoods = calcProductTagForNewGoodsByOptionUnit(Integer.valueOf(productTag.getPlayProducts()));
                setProductTagsItemAndSored(productTag, boomGoods, cacheProduct);
            }
            if ("爆品".equals(productTag.getTagName())) {
                List<ProductTagTaskItem> boomGoods = calcProductTagForLastThirtyDaysSalesByOptionUnitByJava(Integer.valueOf(productTag.getPlayProducts()));
                setProductTagsItemAndSored(productTag, boomGoods, cacheProduct);
            }
            if ("自营".equals(productTag.getTagName())) {
                List<ProductTagTaskItem> selfOperated = calcProductTagForMerchantIsSelf();
                setProductTagsItemAndSored(productTag, selfOperated, cacheProduct);
            }
            if ("热卖".equals(productTag.getTagName())) {
                List<ProductTagTaskItem> hotSaleGoods = calcProductTagForLastThirtyDaysReplyCountByOptionUnit(Integer.valueOf(productTag.getPlayProducts()));
                setProductTagsItemAndSored(productTag, hotSaleGoods, cacheProduct);
            }
            if ("优选".equals(productTag.getTagName())) {
                List<ProductTagTaskItem> preferredGoods = calcProductTagForLastThirtyDaysReplyFiveStartByOptionUnit(Integer.valueOf(productTag.getPlayProducts()));
                setProductTagsItemAndSored(productTag, preferredGoods, cacheProduct);
            }
            if ("包邮".equals(productTag.getTagName())) {
                List<ProductTagTaskItem> freeShippingGoods = calcProductTagForFreeDelivery();
                setProductTagsItemAndSored(productTag, freeShippingGoods, cacheProduct);
            }
        }
        // 加载个人配置的标签信息
        LambdaQueryWrapper<ProductTag> getTagForSelfConfig = Wrappers.lambdaQuery();
        getTagForSelfConfig.eq(ProductTag::getStatus, 1);
        getTagForSelfConfig.eq(ProductTag::getOwner, 1);
        List<ProductTag> productTagsForSelf = dao.selectList(getTagForSelfConfig);
        if (!productTagsForSelf.isEmpty()) {
            List<ProductTag> selfEffProductList = productTagsForSelf.stream().filter(item -> item.getStartTime().compareTo(CrmebDateUtil.nowDateTime()) <= 0
                    && item.getEndTime().compareTo(CrmebDateUtil.nowDateTime()) > 0).collect(Collectors.toList());
            cacheProductTags.put(0, JSON.toJSONString(selfEffProductList));
        }

        for (Map.Entry<Integer, List<ProductTagTaskItem>> entry : cacheProduct.entrySet()) {
            List<ProductTagTaskItem> list = entry.getValue();
            cacheProductTags.put(entry.getKey(), JSON.toJSONString(entry.getValue()));
        }
        return cacheProductTags;
    }

//********************************************************** 任务计算逻辑 START **********************************/
    /* * *
     *     1. 爆品规则 最近30天销量大于 x 件
     *     2. 自营规则 商家标签是自营
     *     3. 热卖规则 最近30天用户评论大于 x 条
     *     4. 优选规则 最近30天评论星级大于 x 星
     *     5. 包邮规则 全国包邮商品（反向根据包邮模版查询）
     */


    private static void setProductTagsItemAndSored(ProductTag productTag, List<ProductTagTaskItem> items, Map<Integer, List<ProductTagTaskItem>> cacheProduct) {
        for (ProductTagTaskItem item : items) {
            // 当前商品的标签数量大于等于4时跳出，因为商品标签上限时4条，标签左侧1一个，标签下三个
            if (ObjectUtil.isNotNull(cacheProduct.get(item.getId())) && cacheProduct.get(item.getId()).size() >= 4) {
                continue;
            }
            item.setSort(productTag.getSort());
            item.setPosition(productTag.getPosition());
            item.setTagName(productTag.getTagName());
            if (cacheProduct.containsKey(item.getId())) {
                cacheProduct.get(item.getId()).add(item);
            } else {
                List<ProductTagTaskItem> tagResponse = new ArrayList<>();
                item.setPosition(productTag.getPosition());
                item.setTagName(productTag.getTagName());
                tagResponse.add(item);
                cacheProduct.put(item.getId(), tagResponse);
            }
            cacheProduct.get(item.getId()).sort(Comparator.comparingInt(ProductTagTaskItem::getSort).reversed());
        }
    }

    // 前面的参数均为通用参数组装是的对应对象，最后一个Integer 类型的对象为参与自定义标签的类型区分 分别是playType字段对应的业务类型
    private static void setProductTagForSelfTagConfig(ProductTag productTag, List<ProductTagTaskItem> leftItems, List<ProductTagTaskItem> underItems,
                                                      ProductTagsFrontResponse selfTag, Integer playTypeConfig) {
        for (String s : productTag.getPlayProducts().split(",")) {
            if (Integer.valueOf(s).equals(playTypeConfig)) {
                ProductTagTaskItem tagTaskItem = new ProductTagTaskItem();
                tagTaskItem.setTagName(productTag.getTagName());
                tagTaskItem.setSort(productTag.getSort());
                tagTaskItem.setId(productTag.getId());
                tagTaskItem.setPosition(productTag.getPosition());
                // 标签在商城中的位置#0=标题下|1=标题前
                if (productTag.getPosition().equals(1)) {
                    leftItems.add(tagTaskItem);
                    leftItems = leftItems.stream().sorted(Comparator.comparing(ProductTagTaskItem::getSort).reversed()).collect(Collectors.toList());
                    selfTag.setLocationLeftTitle(leftItems);
                } else {
                    underItems.add(tagTaskItem);
                    underItems = underItems.stream().sorted(Comparator.comparing(ProductTagTaskItem::getSort).reversed()).collect(Collectors.toList());
                    selfTag.setLocationUnderTitle(underItems);
                }
            }
        }
    }


    /**
     * 根据后台存储的格式，优化成商城续要的格式并根据业务排序
     *
     * @param item      已经缓存的商品标签数据
     * @param tagsFront 后端标签承载的商品标签对象
     * @return tagsFront 当前商品对应标签
     */
    private static ProductTagsFrontResponse setProductTags(ProductTagTaskItem item, ProductTagsFrontResponse tagsFront) {
        if (ObjectUtil.isNotNull(item)) {
            List<ProductTagTaskItem> tags = null;
            if (item.getPosition() == 0) {
                tags = tagsFront.getLocationUnderTitle();
                tags.sort(Comparator.comparingInt(ProductTagTaskItem::getSort));
                tags.add(item);
                tagsFront.setLocationUnderTitle(tags);
            } else if (item.getPosition() == 1) {
                tags = tagsFront.getLocationLeftTitle();
                tags.add(item);
                tags.sort(Comparator.comparingInt(ProductTagTaskItem::getSort));
                tagsFront.setLocationLeftTitle(tags);
            }
            return tagsFront;
        }
        return tagsFront;
    }

    /**
     * 根据业务检测状态开启验证
     *
     * @param productTag 当前商品标签
     */
    private void checkProductTagStatusOnLimit(ProductTag productTag, Integer status, Boolean isEdit) {
        if (1 == status) {
            List<ProductTag> effProductTags = getStatusOnProductTagList();
            if (isEdit) {
                List<ProductTag> currentTags = effProductTags.stream().filter(tag -> tag.getId().equals(productTag.getId())).collect(Collectors.toList());
                if (currentTags.isEmpty() && effProductTags.size() >= 5) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品标签同时开启的不得超过5条");
                }
            } else {
                if (effProductTags.size() >= 5) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商品标签同时开启的不得超过5条");
                }
            }
        }
    }

    /**
     * 查询爆品用java代码重写
     *
     * @param unit
     * @return
     */
    private List<ProductTagTaskItem> calcProductTagForLastThirtyDaysSalesByOptionUnitByJava(Integer unit) {
        List<ProductTagTaskItem> itemList = new ArrayList<>();
        /* 1. 查询所有的30天以内的订单详情
               a.查询product_type = 0 的普通商品
               b.查询product_type = 1 的秒杀商品 TODO 如果后期还有更多类型需需要在此根据逻辑补全对应营销商品
           2. 将上面product_type = 1 的秒杀商品id根据业务替换成真实的商品id 作为爆款的统计数据
           3. 将数据整理成标签数据所需格式
           5. 再根据订单查询订单表去掉未支付数据
        **/
        // 根据支付订单信息查询购买数量

        LambdaQueryWrapper<Order> lqr_Order = Wrappers.lambdaQuery();
        lqr_Order.ge(Order::getLevel, 1).eq(Order::getPaid, 1).gt(Order::getPayTime, LocalDate.now().minusDays(30).toString());
        List<Order> orders = orderdao.selectList(lqr_Order);

        // 查询全部商品订单详情
        LambdaQueryWrapper<OrderDetail> lqr_OrderDetail = Wrappers.lambdaQuery();
        lqr_OrderDetail.in(OrderDetail::getOrderNo, orders.stream().map(Order::getOrderNo).collect(Collectors.toList()));
        List<OrderDetail> orderDetails = orderDetailDao.selectList(lqr_OrderDetail);

        // 处理秒杀商品数据 根据秒杀商品关联关系 修改订单中的商品id为真实商品id 仅仅在此计算中生效
        List<OrderDetail> OrderDetailIsSkillProduct = orderDetails.stream().filter(orderDetail -> orderDetail.getProductMarketingType().equals(ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL)).collect(Collectors.toList());

        if (!OrderDetailIsSkillProduct.isEmpty()) {
            List<Integer> secKillIds = OrderDetailIsSkillProduct.stream().map(OrderDetail::getProductId).distinct().collect(Collectors.toList());
            if (!secKillIds.isEmpty()) {
                LambdaQueryWrapper<SeckillProduct> lqr_secKill = Wrappers.lambdaQuery();
                lqr_secKill.in(SeckillProduct::getId, secKillIds);
                List<SeckillProduct> seckillProducts = seckillProductDao.selectList(lqr_secKill);
                for (OrderDetail orderDetail : orderDetails) {
                    List<SeckillProduct> seckillPs = seckillProducts.stream().filter(seckillProduct -> orderDetail.getProductId().equals(seckillProduct.getId())).collect(Collectors.toList());
                    for (SeckillProduct seckillP : seckillPs) {
                        if (orderDetail.getProductMarketingType().equals(ProductConstants.PRODUCT_MARKETING_TYPE_SECKILL) && seckillP.getId().equals(orderDetail.getProductId())) {
                            orderDetail.setProductId(seckillP.getProductId());
                            break;
                        }
                    }
                }
            }
        }

        // 筛过秒杀订单之后 将秒杀订单销量赚到普通订单的商品之后的数据
        Map<Integer, ProductTagTaskItem> itemList_temp = new HashMap<>();
        // 构造满足条件的商品
        for (OrderDetail orderDetail : orderDetails) {
            ProductTagTaskItem item = itemList_temp.get(orderDetail.getProductId());
            if (null != item && item.getId() > 0) {
                item.setFlag(item.getFlag() + orderDetail.getPayNum());
            } else {
                // 以商品id为主键 添加标签数据
                ProductTagTaskItem tagTaskItem = new ProductTagTaskItem();
                tagTaskItem.setFlag(orderDetail.getPayNum());
                tagTaskItem.setTagName("爆品");
                tagTaskItem.setId(orderDetail.getProductId());
                itemList_temp.put(tagTaskItem.getId(), tagTaskItem);
            }
        }
        // 合并数组中根据商品id唯一合并flag的操作，这里的flag就是购买数量
        for (Integer id : itemList_temp.keySet()) {
            itemList.add(itemList_temp.get(id));
        }
        itemList = itemList.stream().filter(tag -> tag.getFlag() > unit).collect(Collectors.toList());
        return itemList;
    }

    /**
     * 自营规则 商家标签是自营
     * 自营 标签数据任务
     */
    private List<ProductTagTaskItem> calcProductTagForMerchantIsSelf() {
        return dao.calcProductTagForIsSelf();
    }

    /**
     * 热卖规则 最近30天用户评论大于 x 条
     * 热卖 标签
     *
     * @param unit 多少条
     */
    private List<ProductTagTaskItem> calcProductTagForLastThirtyDaysReplyCountByOptionUnit(Integer unit) {
        Map<String, Object> prams = new HashMap<>();
        prams.put("unit", unit);
        return dao.calcProductTagForReplayCount(prams);
    }

    /**
     * 优选规则 最近30天评论星级大于 x 星
     * 优选 标签
     *
     * @param unit 多少条
     */
    private List<ProductTagTaskItem> calcProductTagForLastThirtyDaysReplyFiveStartByOptionUnit(Integer unit) {
        Map<String, Object> prams = new HashMap<>();
        prams.put("unit", unit);
        return dao.calcProductTagForFiveStart(prams);
    }

    /**
     * 新品规则 最近X天内创建的商品
     *
     * @param unit 天数
     * @return 新品商品
     */
    private List<ProductTagTaskItem> calcProductTagForNewGoodsByOptionUnit(Integer unit) {
        Map<String, Object> prams = new HashMap<>();
        prams.put("unit", unit);
        return dao.calcProductTagForNewGoods(prams);
    }

    /**
     * 包邮规则 全国包邮商品（反向根据包邮模版查询）
     * 包邮 标签
     */
    private List<ProductTagTaskItem> calcProductTagForFreeDelivery() {
        return dao.calcProductTagForFreeDelivery();
    }
    //********************************************************** 任务计算逻辑 END **********************************/

    /**
     * 检查商品标签名称是否重复
     *
     * @param productTag 待检查的商品标签对象
     */
    private List<ProductTag> checkTagNameOnlyOne(ProductTag productTag) {
        LambdaQueryWrapper<ProductTag> queryForTagNameOnlyOne = Wrappers.lambdaQuery();
        queryForTagNameOnlyOne.eq(ProductTag::getTagName, productTag.getTagName());
        return dao.selectList(queryForTagNameOnlyOne);
    }
}

