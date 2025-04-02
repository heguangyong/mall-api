package com.zbkj.service.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.MerchantEmployee;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.request.*;
import com.zbkj.common.response.AdminProductListResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.ProductTabsHeaderResponse;
import com.zbkj.service.service.MerchantEmployeeService;
import com.zbkj.service.service.ProductEmployeeService;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/** 移动端商户管理 订单管理 service 调用业务
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
public class ProductEmployeeServiceImpl implements ProductEmployeeService {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantEmployeeService merchantEmployeeService;

    /**
     * 获取产品列表Admin
     *
     * @param request          筛选参数
     * @return PageInfo
     */
    @Override
    public PageInfo<AdminProductListResponse> getAdminList(MerProductSearchRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.getAdminList(request, admin);
    }

    /**
     * 商品详情（管理端）
     *
     * @param id 商品id
     * @return ProductInfoResponse
     */
    @Override
    public ProductInfoResponse getInfo(Integer id) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return  productService.getInfo(id, admin);
    }

    /**
     * 根据商品tabs获取对应类型的产品数量
     *
     * @return List
     */
    @Override
    public List<ProductTabsHeaderResponse> getTabsHeader(MerProductTabsHeaderRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.getTabsHeader(request, admin);
    }

    /**
     * 商品回收/删除
     *
     * @param request 删除参数
     * @return Boolean
     */
    @Override
    public Boolean deleteProduct(ProductDeleteRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
       return productService.deleteProduct(request, admin);
    }

    /**
     * 恢复已删除的商品
     *
     * @param productId 商品id
     * @return 恢复结果
     */
    @Override
    public Boolean restoreProduct(Integer productId) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.restoreProduct(productId, admin);
    }

    /**
     * 下架
     *
     * @param id 商品id
     */
    @Override
    public Boolean offShelf(Integer id) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.offShelf(id, admin);
    }

    /**
     * 上架
     *
     * @param id 商品id
     * @return Boolean
     */
    @Override
    public Boolean putOnShelf(Integer id) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.putOnShelf(id, admin);
    }

    /**
     * 商品提审
     */
    @Override
    public Boolean submitAudit(ProductSubmitAuditRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.submitAudit(request,admin);
    }

    /**
     * 快捷添加库存
     *
     * @param request 添加库存参数
     * @return Boolean
     */
    @Override
    public Boolean quickAddStock(ProductAddStockRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.quickAddStock(request, admin);
    }

    /**
     * 商品免审编辑
     *
     * @param request 商品免审编辑参数
     * @return Boolean
     */
    @Override
    public Boolean reviewFreeEdit(ProductReviewFreeEditRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.reviewFreeEdit(request, admin);
    }

    /**
     * 商品无状态判断编辑 谨慎调用
     *
     * @param request 待编辑商品对象
     * @return 编辑结果
     */
    @Override
    public Boolean merchantEmployeeAnyTimeEdit(ProductAnyTimeEditRequest request) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.merchantEmployeeAnyTimeEdit(request, admin);
    }

    /**
     * 批量上架商品
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchUp(List<Integer> idList) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.batchUp(idList, admin);
    }

    /**
     * 批量商品下架
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchDown(List<Integer> idList) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.batchDown(idList, admin);
    }

    /**
     * 商品批量加入回收站
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchRecycle(List<Integer> idList) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.batchRecycle(idList, admin);
    }

    /**
     * 批量删除商品
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchDelete(List<Integer> idList) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.batchDelete(idList, admin);
    }

    /**
     * 批量恢复回收站商品
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchRestore(List<Integer> idList) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.batchRestore(idList, admin);
    }

    /**
     * 批量提审商品
     * @param idList 商品ID列表
     */
    @Override
    public Boolean batchSubmitAudit(List<Integer> idList) {
        merchantEmployeeService.checkShopMangerRoleByUserId(MerchantEmployee.ROLE_PRODUCT.getRoleValue());
        SystemAdmin admin = userService.getSystemAdminByMerchantEmployee();
        return productService.batchSubmitAudit(idList, admin);
    }

}

