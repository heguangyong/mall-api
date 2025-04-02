package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.GroupConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.GroupConfig;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.IntegralIntervalProductRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.response.IntegralIntervalResponse;
import com.zbkj.common.response.IntegralProductFrontResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.GroupConfigService;
import com.zbkj.service.service.IntegralShoppingService;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 积分商城服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/8/20
 */
@Service
public class IntegralShoppingServiceImpl implements IntegralShoppingService {

    @Autowired
    private UserService userService;
    @Autowired
    private GroupConfigService groupConfigService;
    @Autowired
    private ProductService productService;

    /**
     * 获取用户积分数据
     */
    @Override
    public MyRecord getUserIntegralInfo() {
        User user = userService.getInfo();
        MyRecord record = new MyRecord();
        record.set("integral", user.getIntegral());
        return record;
    }

    /**
     * 积分商品热门推荐分页列表
     */
    @Override
    public PageInfo<IntegralProductFrontResponse> findProductHotPage(PageParamRequest request) {
        return productService.findIntegralProductHotPage(request);
    }

    /**
     * 获取积分区间列表
     */
    @Override
    public List<IntegralIntervalResponse> getIntegralIntervalList() {
        List<GroupConfig> configList = groupConfigService.findByTag(GroupConfigConstants.TAG_INTEGRAL_INTERVAL, Constants.SORT_DESC, Boolean.TRUE);
        if (CollUtil.isEmpty(configList)) {
            return new ArrayList<>();
        }
        List<IntegralIntervalResponse> responseList = configList.stream().map(config -> {
            IntegralIntervalResponse response = new IntegralIntervalResponse();
            BeanUtils.copyProperties(config, response);
            return response;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * 积分商品分页列表(积分区间)
     */
    @Override
    public PageInfo<IntegralProductFrontResponse> findIntegralIntervalProductPage(IntegralIntervalProductRequest request) {
        int startIntegral = 0;
        int endIntegral = 0;
        if (request.getIntervalId() > 0) {
            GroupConfig groupConfig = groupConfigService.getByIdException(request.getIntervalId());
            if (!groupConfig.getTag().equals(GroupConfigConstants.TAG_INTEGRAL_INTERVAL)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "积分区间不存在");
            }
            String[] split = groupConfig.getValue().split("-");
            startIntegral = Integer.parseInt(split[0]);
            endIntegral = Integer.parseInt(split[1]);
        }
        return productService.findIntegralIntervalProductPage(startIntegral, endIntegral, request.getPage(), request.getLimit());
    }
}
