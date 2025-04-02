package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.admin.SystemRole;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemRoleRequest;
import com.zbkj.common.request.SystemRoleSearchRequest;
import com.zbkj.common.request.SystemRoleStatusRequest;
import com.zbkj.common.response.RoleInfoResponse;

import java.util.List;

/**
 * SystemRoleService 接口
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
public interface SystemRoleService extends IService<SystemRole> {

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return List<SystemRole>
     */
    List<SystemRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 修改身份状态
     */
    Boolean updateStatus(SystemRoleStatusRequest request);

    /**
     * 添加身份
     * @param systemRole 身份参数
     * @return Boolean
     */
    Boolean add(SystemRoleRequest systemRole);

    /**
     * 修改身份管理表
     * @param systemRoleRequest 修改参数
     */
    Boolean edit(SystemRoleRequest systemRoleRequest);

    /**
     * 删除角色
     * @param id 角色id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 获取角色详情
     * @param id 角色id
     * @return RoleInfoResponse
     */
    RoleInfoResponse getInfo(Integer id);

    /**
     * 根据当前登录人判断非超管的权限 一般使用到区分平台和平台的位置
     * @return 当前登录人的所属权限
     */
    Integer getRoleTypeByCurrentAdmin();

    /**
     * 根据当前登录人获取素材资源归属人 平台的是权限 其他的都是商户id
     * @return 资源归属方
     */
    Integer getOwnerByCurrentAdmin();

    /**
     * 获取所有角色
     * @param merId 平台-0，商户id
     * @return List
     */
    List<SystemRole> getListByMerId(Integer merId);
}
