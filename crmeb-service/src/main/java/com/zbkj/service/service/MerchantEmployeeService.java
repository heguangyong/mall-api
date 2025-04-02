package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.merchant.MerchantEmployee;
import com.zbkj.common.request.merchant.manage.MerchantEmployeeRequest;
import com.zbkj.common.request.merchant.manage.MerchantEmployeeSearchRequest;
import com.zbkj.common.response.employee.EmployeeMerchantActiveResponse;
import com.zbkj.common.response.employee.FrontMerchantEmployeeResponse;

import java.util.List;

/**
* @author dazongzi
* @description MerchantEmployeeService 接口
* @date 2024-05-24
*/
public interface MerchantEmployeeService extends IService<MerchantEmployee> {

    /**
     * 列表
     * @param request 请求参数
     * @author dazongzi
     * @since 2024-05-24
     * @return List<MerchantEmployee>
     */
    List<MerchantEmployee> getList(MerchantEmployeeSearchRequest request);

    /**
     * 新增店铺管理员
     * @param request 当前新增的店铺管理员
     * @return 新增结果
     */
    Boolean addMerchantEmployee(MerchantEmployeeRequest request);


    /**
     * 更新店铺管理员
     * @param request 当前新增的店铺管理员
     * @return 更新结果
     */
    Boolean editMerchantEmployee(MerchantEmployeeRequest request);

    /**
     * 根据用户id 查询所在店铺管理员列表
     * @param uid 用户id
     * @return 当前用户开启的管理店铺集合
     */
    List<MerchantEmployee> getShopMangerByUserId(Integer uid);

    /**
     * 检测移动端商户管理权限
     * @param roleNo 权限编号
     * @return 是否拥有
     */
    Boolean checkShopMangerRoleByUserId(Integer roleNo);

    /**
     * 获取归属商户列表
     */
    List<FrontMerchantEmployeeResponse> findBelongList();

    /**
     * 激活商户获取token
     *
     * @param merId 商户ID
     */
    EmployeeMerchantActiveResponse activeMerchant(Integer merId);

    Boolean deleteById(Integer id);

    Boolean deleteByUserId(Integer userId);

    Boolean isExist(Integer merId, Integer uid);
}
