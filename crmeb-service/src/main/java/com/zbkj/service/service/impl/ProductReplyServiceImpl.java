package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.ProductConstants;
import com.zbkj.common.constants.UserConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.model.order.OrderDetail;
import com.zbkj.common.model.product.Product;
import com.zbkj.common.model.product.ProductAttrValue;
import com.zbkj.common.model.product.ProductReply;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.ProductReplyCommentRequest;
import com.zbkj.common.request.ProductReplySearchRequest;
import com.zbkj.common.request.ProductReplyVirtualRequest;
import com.zbkj.common.response.ProductDetailReplyResponse;
import com.zbkj.common.response.ProductReplayCountResponse;
import com.zbkj.common.response.ProductReplyResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.ProductResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.vo.DateLimitUtilVo;
import com.zbkj.service.dao.ProductReplyDao;
import com.zbkj.service.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ProductReplyServiceImpl 接口实现
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
public class ProductReplyServiceImpl extends ServiceImpl<ProductReplyDao, ProductReply>
        implements ProductReplyService {

    @Resource
    private ProductReplyDao dao;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ProductAttrValueService attrValueService;
    @Autowired
    private UserService userService;


    /**
     * 平台端商品评论列表
     *
     * @param request          请求参数
     * @return List<ProductReply>
     */
    @Override
    public PageInfo<ProductReplyResponse> getAdminPage(ProductReplySearchRequest request) {
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (ObjectUtil.isNotNull(request.getMerId()) && request.getMerId() > 0) {
            map.put("merId", request.getMerId());
        }
        if (ObjectUtil.isNotNull(request.getStar())) {
            map.put("star", request.getStar());
        }
        if (ObjectUtil.isNotNull(request.getIsReply())) {
            map.put("isReply", request.getIsReply() ? 1 :0);
        }
        if (StrUtil.isNotBlank(request.getProductSearch())) {
            map.put("productSearch", URLUtil.decode(request.getProductSearch()));
        }
        Page<ProductReply> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ProductReply> dataList = dao.getPlatAdminPage(map);
        if (CollUtil.isEmpty(dataList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<Integer> uidList = dataList.stream().map(ProductReply::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(uidList)) {
            userMap = userService.getUidMapList(uidList);
        }
        List<ProductReplyResponse> dataResList = new ArrayList<>();
        for (ProductReply productReply : dataList) {
            ProductReplyResponse productReplyResponse = new ProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            if (productReply.getOrderDetailId() > 0) {
                OrderDetail orderDetail = orderDetailService.getById(productReply.getOrderDetailId());
                productReplyResponse.setProductName(orderDetail.getProductName());
                productReplyResponse.setProductImage(orderDetail.getImage());
                Merchant merchant = merchantService.getById(orderDetail.getMerId());
                productReplyResponse.setMerName(merchant.getName());
            } else {
                Product product = productService.getById(productReply.getProductId());
                productReplyResponse.setProductName(product.getName());
                productReplyResponse.setProductImage(product.getImage());
                Merchant merchant = merchantService.getById(product.getMerId());
                productReplyResponse.setMerName(merchant.getName());
            }
            if (StrUtil.isNotBlank(productReply.getPics())) {
                productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            }
            if (productReply.getUid() > 0) {
                productReplyResponse.setIsLogoff(userMap.get(productReply.getUid()).getIsLogoff());
            }
            dataResList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(page, dataResList);
    }

    /**
     * 商户端商品评论分页列表
     * @param request 请求参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductReplyResponse> getMerchantAdminPage(ProductReplySearchRequest request) {
        SystemAdmin systemAdmin = SecurityUtil.getLoginUserVo().getUser();
        Map<String, Object> map = CollUtil.newHashMap();
        if (StrUtil.isNotBlank(request.getContent())) {
            ValidateFormUtil.validatorUserCommonSearch(request);
            String keywords = URLUtil.decode(request.getContent());
            switch (request.getSearchType()) {
                case UserConstants.USER_SEARCH_TYPE_ALL:
                    map.put("keywords", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_UID:
                    map.put("uid", Integer.valueOf(request.getContent()));
                    break;
                case UserConstants.USER_SEARCH_TYPE_NICKNAME:
                    map.put("nickname", keywords);
                    break;
                case UserConstants.USER_SEARCH_TYPE_PHONE:
                    map.put("phone", request.getContent());
                    break;
            }
        }
        //时间范围
        if (StrUtil.isNotBlank(request.getDateLimit())) {
            DateLimitUtilVo dateLimit = CrmebDateUtil.getDateLimit(request.getDateLimit());
            //判断时间
            int compareDateResult = CrmebDateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), DateConstants.DATE_FORMAT);
            if (compareDateResult == -1) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "开始时间不能大于结束时间！");
            }
            if (StrUtil.isNotBlank(dateLimit.getStartTime())) {
                map.put("startTime", dateLimit.getStartTime());
                map.put("endTime", dateLimit.getEndTime());
            }
        }
        if (!systemAdmin.getMerId().equals(0)) {
            map.put("merId", systemAdmin.getMerId());
        }
        if (ObjectUtil.isNotNull(request.getStar())) {
            map.put("star", request.getStar());
        }
        if (ObjectUtil.isNotNull(request.getIsReply())) {
            map.put("isReply", request.getIsReply() ? 1 :0);
        }
        if (StrUtil.isNotBlank(request.getProductSearch())) {
            map.put("productSearch", URLUtil.decode(request.getProductSearch()));
        }
        Page<ProductReply> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<ProductReply> dataList = dao.getMerchantAdminPage(map);
        if (CollUtil.isEmpty(dataList)) {
            return CommonPage.copyPageInfo(page, new ArrayList<>());
        }
        List<Integer> uidList = dataList.stream().map(ProductReply::getUid).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = CollUtil.newHashMap();
        if (CollUtil.isNotEmpty(uidList)) {
            userMap = userService.getUidMapList(uidList);
        }
        List<ProductReplyResponse> dataResList = new ArrayList<>();
        for (ProductReply productReply : dataList) {
            ProductReplyResponse productReplyResponse = new ProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            if (productReply.getOrderDetailId() > 0) {
                OrderDetail orderDetail = orderDetailService.getById(productReply.getOrderDetailId());
                productReplyResponse.setProductName(orderDetail.getProductName());
                productReplyResponse.setProductImage(orderDetail.getImage());
            } else {
                Product product = productService.getById(productReply.getProductId());
                productReplyResponse.setProductName(product.getName());
                productReplyResponse.setProductImage(product.getImage());
            }
            if (StrUtil.isNotBlank(productReply.getPics())) {
                productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            }
            if (productReply.getUid() > 0) {
                productReplyResponse.setIsLogoff(userMap.get(productReply.getUid()).getIsLogoff());
            }
            dataResList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(page, dataResList);
    }

    /**
     * 添加虚拟评论
     * @param request 评论参数
     * @return 评论结果
     */
    @Override
    public Boolean virtualCreate(ProductReplyVirtualRequest request) {
        Product product = productService.getById(request.getProductId());
        if (ObjectUtil.isNull(product) || product.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(product.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_NOT_EXIST);
        }
        ProductAttrValue attrValue = attrValueService.getById(request.getAttrValueId());
        if (ObjectUtil.isNull(attrValue) || attrValue.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_ATTR_VALUE_NOT_EXIST);
        }
        ProductReply productReply = new ProductReply();
        BeanUtils.copyProperties(request, productReply);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        productReply.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar(), cdnUrl));
        if (CollUtil.isNotEmpty(request.getPics())) {
            List<String> pics = request.getPics().stream().map(e -> systemAttachmentService.clearPrefix(e, cdnUrl)).collect(Collectors.toList());
            productReply.setPics(String.join(",", pics));
        }
        productReply.setUid(0);
        productReply.setMerId(product.getMerId());
        productReply.setSku(attrValue.getSku());
        return save(productReply);
    }

    /**
     * H5商品评论统计
     *
     * @param productId 商品编号
     * @return MyRecord
     */
    @Override
    public ProductReplayCountResponse getH5Count(Integer productId) {
        // 评论总数
        Integer sumCount = getCountByScore(productId, ProductConstants.PRODUCT_REPLY_TYPE_ALL);
        // 好评总数
        Integer goodCount = getCountByScore(productId, ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
        // 中评总数
        Integer mediumCount = getCountByScore(productId, ProductConstants.PRODUCT_REPLY_TYPE_MEDIUM);
        // 差评总数
        Integer poorCount = getCountByScore(productId, ProductConstants.PRODUCT_REPLY_TYPE_POOR);
        // 好评率
        String replyChance = "0";
        if (sumCount > 0 && goodCount > 0) {
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }
        // 评分星数 = 总星数/评价数
        Integer replyStar = 0;
        if (sumCount > 0) {
            replyStar = getSumStar(productId);
            BigDecimal divide = new BigDecimal(replyStar).divide(new BigDecimal(sumCount.toString()), 0, BigDecimal.ROUND_DOWN);
            replyStar = divide.intValue();
        }
        return new ProductReplayCountResponse(sumCount, goodCount, mediumCount, poorCount, replyChance, replyStar);
    }

    /**
     * H5商品详情评论信息
     *
     * @param proId 商品编号
     * @return ProductDetailReplyResponse
     */
    @Override
    public ProductDetailReplyResponse getH5ProductReply(Integer proId) {
        ProductDetailReplyResponse response = new ProductDetailReplyResponse();

        // 评论总数
        Integer sumCount = getCountByScore(proId, ProductConstants.PRODUCT_REPLY_TYPE_ALL);
        if (sumCount.equals(0)) {
            response.setSumCount(0);
            response.setReplyChance("0");
            return response;
        }
        // 好评总数
        Integer goodCount = getCountByScore(proId, ProductConstants.PRODUCT_REPLY_TYPE_GOOD);
        // 好评率
        String replyChance = "0";
        if (sumCount > 0 && goodCount > 0) {
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }

        // 查询最后一条评论
        LambdaQueryWrapper<ProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductReply::getProductId, proId);
        lqw.eq(ProductReply::getIsDel, false);
        lqw.orderByDesc(ProductReply::getId);
        lqw.last(" limit 1");
        ProductReply productReply = dao.selectOne(lqw);
        ProductReplyResponse productReplyResponse = new ProductReplyResponse();
        BeanUtils.copyProperties(productReply, productReplyResponse);
        // 评价图
        if (StrUtil.isNotBlank(productReply.getPics())) {
            productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
        }
        // 昵称
        String nickname = productReply.getNickname();
        if (StrUtil.isNotBlank(nickname)) {
            if (nickname.length() == 1) {
                nickname = nickname.concat("**");
            } else if (nickname.length() == 2) {
                nickname = nickname.charAt(0) + "**";
            } else {
                nickname = nickname.charAt(0) + "**" + nickname.substring(nickname.length() - 1);
            }
            productReplyResponse.setNickname(nickname);
        }
        if (productReply.getUid() > 0) {
            User user = userService.getById(productReply.getUid());
            productReplyResponse.setIsLogoff(user.getIsLogoff());
        }
        response.setSumCount(sumCount);
        response.setReplyChance(replyChance);
        response.setProductReply(productReplyResponse);
        return response;
    }

    /**
     * 移动端商品评论列表
     *
     * @param proId            商品编号
     * @param type             评价等级|0=全部,1=好评,2=中评,3=差评
     * @param pageParamRequest 分页参数
     * @return PageInfo<ProductReplyResponse>
     */
    @Override
    public PageInfo<ProductReplyResponse> getH5List(Integer proId, Integer type, PageParamRequest pageParamRequest) {
        Page<ProductReply> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 ProductReply 类的多条件查询
        LambdaQueryWrapper<ProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductReply::getIsDel, false);
        lqw.eq(ProductReply::getProductId, proId);
        //评价等级|0=全部,1=好评,2=中评,3=差评
        switch (type) {
            case 1:
                lqw.eq(ProductReply::getStar, 5);
                break;
            case 2:
                lqw.apply(" star < 5 and star > 1");
                break;
            case 3:
                lqw.eq(ProductReply::getStar, 1);
                break;
            default:
                break;
        }
        lqw.orderByDesc(ProductReply::getId);
        List<ProductReply> replyList = dao.selectList(lqw);
        List<ProductReplyResponse> responseList = new ArrayList<>();
        if (CollUtil.isEmpty(replyList)) {
            return CommonPage.copyPageInfo(page, responseList);
        }
        List<Integer> uidList = replyList.stream().map(e -> e.getUid()).distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = userService.getUidMapList(uidList);
        for (ProductReply productReply : replyList) {
            ProductReplyResponse productReplyResponse = new ProductReplyResponse();
            BeanUtils.copyProperties(productReply, productReplyResponse);
            // 评价图
            if (StrUtil.isNotBlank(productReply.getPics())) {
                productReplyResponse.setPics(CrmebUtil.stringToArrayStr(productReply.getPics()));
            }
            // 昵称
            String nickname = productReply.getNickname();
            if (StrUtil.isNotBlank(nickname)) {
                if (nickname.length() == 1) {
                    nickname = nickname.concat("**");
                } else if (nickname.length() == 2) {
                    nickname = nickname.charAt(0) + "**";
                } else {
                    nickname = nickname.charAt(0) + "**" + nickname.substring(nickname.length() - 1);
                }
                productReplyResponse.setNickname(nickname);
            }
            if (productReply.getUid() > 0) {
                productReplyResponse.setIsLogoff(userMap.get(productReply.getUid()).getIsLogoff());
            }
            responseList.add(productReplyResponse);
        }
        return CommonPage.copyPageInfo(page, responseList);
    }

    /**
     * 删除评论
     *
     * @param id 评论id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaUpdateWrapper<ProductReply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(ProductReply::getIsDel, 1);
        wrapper.eq(ProductReply::getId, id);
        if (admin.getMerId() > 0) {
            wrapper.eq(ProductReply::getMerId, admin.getMerId());
        }
        return update(wrapper);
    }

    /**
     * 商品评论回复
     * @param request 回复参数
     */
    @Override
    public Boolean comment(ProductReplyCommentRequest request) {
        ProductReply reply = getById(request.getId());
        if (ObjectUtil.isNull(reply) || reply.getIsDel()) {
            throw new CrmebException(ProductResultCode.PRODUCT_REPLY_NOT_EXIST);
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(reply.getMerId())) {
            throw new CrmebException(ProductResultCode.PRODUCT_REPLY_NOT_EXIST);
        }
        if (reply.getIsReply()) {
            throw new CrmebException(ProductResultCode.PRODUCT_REPLY_ED);
        }
        LambdaUpdateWrapper<ProductReply> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ProductReply::getId, request.getId());
        wrapper.set(ProductReply::getMerchantReplyContent, request.getMerchantReplyContent());
        wrapper.set(ProductReply::getMerchantReplyTime, cn.hutool.core.date.DateUtil.date());
        wrapper.set(ProductReply::getIsReply, true);
        return update(wrapper);
    }

    /**
     * 商品星数
     *
     * @return Integer
     */
    private Integer getSumStar(Integer productId) {
        QueryWrapper<ProductReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(star),0) as star");
        queryWrapper.eq("is_del", 0);
        queryWrapper.eq("product_id", productId);
        ProductReply productReply = dao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(productReply)) {
            return 0;
        }
        if (productReply.getStar() == 0) {
            return 0;
        }
        return productReply.getStar();
    }

    /**
     * 获取统计数据（好评、中评、差评）
     */
    @Override
    public Integer getCountByScore(Integer productId, String type) {
        LambdaQueryWrapper<ProductReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ProductReply::getProductId, productId);
        lqw.eq(ProductReply::getIsDel, false);

        switch (type) {
            case ProductConstants.PRODUCT_REPLY_TYPE_ALL:
                break;
            case ProductConstants.PRODUCT_REPLY_TYPE_GOOD:
                lqw.eq(ProductReply::getStar, 5);
                break;
            case ProductConstants.PRODUCT_REPLY_TYPE_MEDIUM:
                lqw.and(i -> i.lt(ProductReply::getStar, 5).gt(ProductReply::getStar, 1));
                break;
            case ProductConstants.PRODUCT_REPLY_TYPE_POOR:
                lqw.eq(ProductReply::getStar, 1);
                break;
        }
        return dao.selectCount(lqw);
    }
}

