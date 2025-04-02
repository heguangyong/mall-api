package com.zbkj.front.service.impl;

import com.zbkj.common.enums.MerchantEmployee;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.response.HomeOperatingMerDataResponse;
import com.zbkj.common.response.HomeRateResponse;
import com.zbkj.common.response.ProductRankingResponse;
import com.zbkj.front.service.EmployeeHomeService;
import com.zbkj.service.service.HomeService;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 移动端商家管理
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
public class EmployeeHomeServiceImpl implements EmployeeHomeService {

    @Autowired
    private HomeService homeService;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;

    /**
     * 首页数据
     * @return HomeRateResponse
     */
    @Override
    public HomeRateResponse indexMerchantDate() {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_STATISTICS.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return homeService.indexMerchantDate(systemAdmin);
    }

    /**
     * 经营数据：
     * @return HomeOperatingMerDataResponse
     */
    @Override
    public HomeOperatingMerDataResponse operatingMerchantData() {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_STATISTICS.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return homeService.operatingMerchantData(systemAdmin);
    }

    /**
     * 商户端商品支付排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPayRanking() {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_STATISTICS.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return homeService.merchantProductPayRanking(systemAdmin);
    }

    /**
     * 商品浏览量排行榜
     */
    @Override
    public List<ProductRankingResponse> merchantProductPageviewRanking() {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_STATISTICS.getRoleValue());
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return homeService.merchantProductPageviewRanking(systemAdmin);
    }

}
