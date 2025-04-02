package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.RegularConstants;
import com.zbkj.common.constants.SmsConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.admin.SystemRole;
import com.zbkj.common.request.*;
import com.zbkj.common.response.SystemAdminResponse;
import com.zbkj.common.result.AdminResultCode;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.SystemAdminDao;
import com.zbkj.service.service.SystemAdminService;
import com.zbkj.service.service.SystemRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SystemAdminServiceImpl 接口实现
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
public class SystemAdminServiceImpl extends ServiceImpl<SystemAdminDao, SystemAdmin> implements SystemAdminService {

    @Resource
    private SystemAdminDao dao;

    @Autowired
    private SystemRoleService systemRoleService;
    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @param type     用户类型
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin selectUserByUserNameAndType(String username, Integer type) {
        List<Integer> types = addSuperRoleType(type);
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getAccount, username);
        lqw.in(SystemAdmin::getType, types);
        lqw.eq(SystemAdmin::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 后台管理员列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带SystemAdminRequest类的多条件查询
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(request.getStatus())) {
            lqw.eq(SystemAdmin::getStatus, request.getStatus());
        }
        if (StrUtil.isNotBlank(request.getRealName())) {
            String decode = URLUtil.decode(request.getRealName());
            lqw.and(i -> i.like(SystemAdmin::getRealName, decode)
                    .or().like(SystemAdmin::getAccount, decode));
        }
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        lqw.eq(SystemAdmin::getMerId, currentAdmin.getMerId());
        // 超级管理员查看平台账户 商户超级管理员查看自己和商户管理员
        // 1=1，2,3 2=2，4
        List<Integer> types = new ArrayList<>();
        if (currentAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            types.add(RoleEnum.MERCHANT_ADMIN.getValue());
            types.add(RoleEnum.SUPER_MERCHANT.getValue());
            lqw.in(SystemAdmin::getType, types);
        } else if (currentAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue())) {
            types.add(RoleEnum.PLATFORM_ADMIN.getValue());
            types.add(RoleEnum.SUPER_ADMIN.getValue());
            lqw.in(SystemAdmin::getType, types);
        } else {
            lqw.eq(SystemAdmin::getType, systemRoleService.getRoleTypeByCurrentAdmin());
        }
        if (ObjectUtil.isNotNull(request.getRoles()) && request.getRoles() > 0) {
            lqw.apply(" find_in_set({0}, roles)", request.getRoles());
//            lqw.apply(StrUtil.format(" find_in_set('{}', roles)", request.getRoles()));
        }
        List<SystemAdmin> systemAdmins = dao.selectList(lqw);
        if (CollUtil.isEmpty(systemAdmins)) {
            return CollUtil.newArrayList();
        }
        List<SystemAdminResponse> adminResponseList = new ArrayList<>();
        List<SystemRole> roleList = systemRoleService.getListByMerId(currentAdmin.getMerId());
        for (SystemAdmin admin : systemAdmins) {
            SystemAdminResponse sar = new SystemAdminResponse();
            BeanUtils.copyProperties(admin, sar);
            sar.setLastTime(admin.getUpdateTime());
            List<Integer> roleIds = CrmebUtil.stringToArrayInt(admin.getRoles());
            List<String> roleNames = new ArrayList<>();
            for (Integer roleId : roleIds) {
                if (1 == roleId) {
                    roleNames.add("超级管理员");
                    continue;
                }
                if (2 == roleId) {
                    roleNames.add("商户超管");
                    continue;
                }
                for (SystemRole role : roleList) {
                    if (role.getId().equals(roleId)) {
                        roleNames.add(role.getRoleName());
                    }
                }
            }
            sar.setRoleNames(StringUtils.join(roleNames, ","));

            if (crmebConfig.getPhoneMaskSwitch() && StrUtil.isNotBlank(sar.getPhone())) {
                sar.setPhone(CrmebUtil.maskMobile(sar.getPhone()));
            }
            adminResponseList.add(sar);
        }
        return adminResponseList;
    }

    /**
     * 是否存在角色
     *
     * @param roleId 角色id
     * @return Boolean
     */
    @Override
    public Boolean isExistRole(Integer roleId) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemAdmin::getId);
        lqw.apply(StrUtil.format(" find_in_set('{}', roles)", roleId));
        lqw.last(" limit 1");
        SystemAdmin systemAdmin = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(systemAdmin);
    }

    /**
     * 新增管理员
     * @param systemAdminAddRequest 新增参数
     * @return Boolean
     */
    @Override
    public Boolean saveAdmin(SystemAdminAddRequest systemAdminAddRequest) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        // 管理员名称唯一校验
        if (checkAccount(systemAdminAddRequest.getAccount())) {
            throw new CrmebException(AdminResultCode.ADMIN_EXIST);
        }

        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminAddRequest, systemAdmin);

        String pwd = CrmebUtil.encryptPassword(systemAdmin.getPwd(), systemAdmin.getAccount());
        systemAdmin.setPwd(pwd);
        systemAdmin.setMerId(currentUser.getMerId());
        systemAdmin.setType(systemRoleService.getRoleTypeByCurrentAdmin());
        return save(systemAdmin);
    }

    /**
     * 管理员名称唯一校验
     * @param account 管理员账号
     * @return Boolean
     */
    @Override
    public Boolean checkAccount(String account) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getAccount, account);
        lqw.eq(SystemAdmin::getIsDel, false);
        return dao.selectCount(lqw) > 0;
    }

    /**
     * 更新管理员
     */
    @Override
    public Boolean updateAdmin(SystemAdminUpdateRequest systemAdminRequest) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        SystemAdmin adminDetail = getDetail(systemAdminRequest.getId());
        // 超级管理员不允许编辑
        if (adminDetail.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || adminDetail.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统内置权限，不允许编辑");
        }
        if (!currentUser.getMerId().equals(adminDetail.getMerId())) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        verifyAccount(systemAdminRequest.getId(), systemAdminRequest.getAccount());
        SystemAdmin systemAdmin = new SystemAdmin();
        systemAdmin.setId(systemAdminRequest.getId());
        systemAdmin.setAccount(systemAdminRequest.getAccount());
        systemAdmin.setRealName(systemAdminRequest.getRealName());
        systemAdmin.setRoles(systemAdminRequest.getRoles());
        systemAdmin.setStatus(systemAdminRequest.getStatus());
        if (StrUtil.isNotBlank(systemAdminRequest.getPhone())) {
            systemAdmin.setPhone(systemAdminRequest.getPhone());
        }
        systemAdmin.setMerId(currentUser.getMerId());
        return updateById(systemAdmin);
    }

    /**
     * 删除管理员
     * @param id 管理员id
     * @return 删除结果
     */
    @Override
    public Boolean removeAdmin(Integer id) {
        SystemAdmin perDelAdmin = getDetail(id);
        if (perDelAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || perDelAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统内置权限，不允许删除");
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (!admin.getMerId().equals(perDelAdmin.getMerId())) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        return removeById(id);
    }

    /**
     * 修改后台管理员状态
     * @param id 管理员id
     * @param status 状态
     * @return Boolean
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();
        // 超级管理员不允许编辑
        SystemAdmin systemAdmin = getDetail(id);
        if (systemAdmin.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || systemAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统内置权限，不允许编辑");
        }
        if (!currentUser.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        if (systemAdmin.getStatus().equals(status)) {
            return true;
        }
        systemAdmin.setStatus(status);
        return updateById(systemAdmin);
    }

    /**
     * 管理员详情
     * @param id 管理员id
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getDetail(Integer id) {
        SystemAdmin systemAdmin = getById(id);
        if (ObjectUtil.isNull(systemAdmin) || systemAdmin.getIsDel()) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        if (admin.getMerId() > 0) {
            if (!admin.getMerId().equals(systemAdmin.getMerId())) {
                throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
            }
        }
        return systemAdmin;
    }

    /**
     * 获取管理员名称map
     * @param idList id列表
     * @return Map
     */
    @Override
    public Map<Integer, String> getNameMapByIdList(List<Integer> idList) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemAdmin::getId, SystemAdmin::getRealName);
        lqw.in(SystemAdmin::getId, idList);
        List<SystemAdmin> adminList = dao.selectList(lqw);
        Map<Integer, String> map = CollUtil.newHashMap();
        adminList.forEach(admin -> {
            map.put(admin.getId(), admin.getRealName());
        });
        return map;
    }

    /**
     * 修改后台管理员手机号
     */
    @Override
    public Boolean updatePassword(SystemAdminUpdatePwdRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "两次密码输入不一致");
        }
        SystemAdmin currentUser = SecurityUtil.getLoginUserVo().getUser();

        SystemAdmin adminDetail = getDetail(request.getId());
        // 超级管理员不允许编辑
        if (adminDetail.getType().equals(RoleEnum.SUPER_ADMIN.getValue())
                || adminDetail.getType().equals(RoleEnum.SUPER_MERCHANT.getValue())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统内置权限，不允许编辑");
        }
        if (!currentUser.getMerId().equals(adminDetail.getMerId())) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        SystemAdmin newAdmin = new SystemAdmin();
        newAdmin.setId(request.getId());
        newAdmin.setPwd(CrmebUtil.encryptPassword(request.getPassword(), adminDetail.getAccount()));
        return updateById(newAdmin);
    }

    /**
     * 商户后台管理员短信开关
     * @param adminId 商户管理员ID
     */
    @Override
    public Boolean updateReceiveSms(Integer adminId) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
//        if (admin.getMerId().equals(0)) {
//            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "平台管理员无接口短信开关");
//        }
        SystemAdmin systemAdmin = getById(adminId);
        if (!admin.getMerId().equals(systemAdmin.getMerId())) {
            throw new CrmebException(AdminResultCode.ADMIN_NOT_EXIST);
        }
        if (!systemAdmin.getIsSms() && StrUtil.isBlank(systemAdmin.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先为管理员设置手机号");
        }
        if (!ReUtil.isMatch(RegularConstants.PHONE_TWO, systemAdmin.getPhone())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先为管理员设置手机号");
        }
        systemAdmin.setIsSms(!systemAdmin.getIsSms());
        return updateById(systemAdmin);
    }

    /**
     * 获取接收短信管理员列表
     *
     * @param merId 商户ID
     */
    @Override
    public List<SystemAdmin> findReceiveSmsAdminListByMerId(Integer merId) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemAdmin::getId, SystemAdmin::getPhone, SystemAdmin::getRealName);
        lqw.eq(SystemAdmin::getMerId, merId);
        lqw.eq(SystemAdmin::getIsSms, 1);
        lqw.eq(SystemAdmin::getStatus, 1);
        lqw.eq(SystemAdmin::getIsDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 越权登录获取商户超管账户
     * @param merId 商户ID
     */
    @Override
    public SystemAdmin getSuperAdminByPlat(Integer merId) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getMerId, merId);
        lqw.eq(SystemAdmin::getType, RoleEnum.SUPER_MERCHANT.getValue());
        lqw.eq(SystemAdmin::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 校验账号唯一性（管理员更新时）
     * @param id 管理员id
     * @param account 管理员账号
     */
    private void verifyAccount(Integer id, String account) {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.ne(SystemAdmin::getId, id);
        lqw.eq(SystemAdmin::getAccount, account);
        SystemAdmin systemAdmin = dao.selectOne(lqw);
        if (ObjectUtil.isNotNull(systemAdmin)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "账号已存在");
        }
    }

    /**
     * 登录之前查询权限带上超管标识
     *
     * @param type 用户类型
     */
    private List<Integer> addSuperRoleType(Integer type) {
        List<Integer> types = new ArrayList<>();
        if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
            types.add(RoleEnum.SUPER_ADMIN.getValue());
            types.add(RoleEnum.PLATFORM_ADMIN.getValue());
        }
        if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            types.add(RoleEnum.SUPER_MERCHANT.getValue());
            types.add(RoleEnum.MERCHANT_ADMIN.getValue());
        }
        return types;
    }

}

