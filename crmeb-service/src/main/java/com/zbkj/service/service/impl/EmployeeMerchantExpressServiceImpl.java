package com.zbkj.service.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.express.MerchantExpress;
import com.zbkj.common.request.MerchantExpressSearchRequest;
import com.zbkj.service.dao.MerchantExpressDao;
import com.zbkj.service.service.EmployeeMerchantExpressService;
import com.zbkj.service.service.MerchantExpressManagerService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author HZW
* @description MerchantExpressServiceImpl 接口实现
* @date 2024-05-08
*/
@Service
public class EmployeeMerchantExpressServiceImpl extends ServiceImpl<MerchantExpressDao, MerchantExpress> implements EmployeeMerchantExpressService {

    @Autowired
    private MerchantExpressManagerService merchantExpressManagerService;

    @Autowired
    private UserService userService;
    /**
     * 商户物流公司分页列表
     */
    @Override
    public PageInfo<MerchantExpress> searchPage(MerchantExpressSearchRequest request) {
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return merchantExpressManagerService.searchPage(request, admin);
    }

    /**
     * 查询物流公司面单模板
     *
     * @param com 快递公司编号
     */
    @Override
    public JSONObject template(String com) {
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return merchantExpressManagerService.template(com, admin);
    }
}

