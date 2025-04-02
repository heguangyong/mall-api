package com.zbkj.front.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.request.MerchantDeliveryPersonnelSearchRequest;
import com.zbkj.common.response.MerchantDeliveryPersonnelPageResponse;
import com.zbkj.front.service.EmployeeMerchantDeliveryPersonnelService;
import com.zbkj.service.service.MerchantDeliveryPersonnelService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2024/11/12
 */
@Service
public class EmployeeMerchantDeliveryPersonnelServiceImpl implements EmployeeMerchantDeliveryPersonnelService {

    @Autowired
    private MerchantDeliveryPersonnelService merchantDeliveryPersonnelService;
    @Autowired
    private UserService userService;

    /**
     * 商户配送人员分页列表
     */
    @Override
    public PageInfo<MerchantDeliveryPersonnelPageResponse> findPage(MerchantDeliveryPersonnelSearchRequest request) {
        SystemAdmin systemAdmin = userService.getSystemAdminByMerchantEmployee();
        return merchantDeliveryPersonnelService.findPageByEmployee(request, systemAdmin.getMerId());
    }
}
