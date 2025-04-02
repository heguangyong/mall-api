package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.MenuConstants;
import com.zbkj.common.constants.RedisConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemMenu;
import com.zbkj.common.model.admin.SystemRoleMenu;
import com.zbkj.common.request.SystemMenuRequest;
import com.zbkj.common.request.SystemMenuSearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.vo.MenuCheckTree;
import com.zbkj.common.vo.MenuCheckVo;
import com.zbkj.service.dao.SystemMenuDao;
import com.zbkj.service.service.SystemMenuService;
import com.zbkj.service.service.SystemRoleMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SystemMenuServiceImpl 接口实现
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
public class SystemMenuServiceImpl extends ServiceImpl<SystemMenuDao, SystemMenu> implements SystemMenuService {

    @Resource
    private SystemMenuDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemRoleMenuService roleMenuService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 获取所有权限
     *
     * @param type 系统菜单类型:1-平台 2-商户
     * @return List
     */
    @Override
    public List<SystemMenu> getAllPermissions(Integer type) {
        LambdaQueryWrapper<SystemMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemMenu::getIsDelte, false);
        lqw.eq(SystemMenu::getType, type);
        lqw.ne(SystemMenu::getMenuType, MenuConstants.TYPE_M);
        return dao.selectList(lqw);
    }

    /**
     * 通过用户id获取权限
     *
     * @param userId 用户id
     * @return List
     */
    @Override
    public List<SystemMenu> findPermissionByUserId(Integer userId) {
        return dao.findPermissionByUserId(userId);
    }

    /**
     * 获取所有菜单
     *
     * @param type 系统菜单类型：platform,merchant
     * @return List<SystemMenu>
     */
    @Override
    public List<SystemMenu> findAllCatalogue(Integer type) {
        LambdaQueryWrapper<SystemMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemMenu::getIsDelte, false);
        lqw.eq(SystemMenu::getIsShow, true);
        lqw.eq(SystemMenu::getType, type);
        lqw.ne(SystemMenu::getMenuType, MenuConstants.TYPE_A);
        return dao.selectList(lqw);
    }

    /**
     * 获取用户路由
     *
     * @param userId 用户id
     * @return List
     */
    @Override
    public List<SystemMenu> getMenusByUserId(Integer userId) {
        return dao.getMenusByUserId(userId);
    }

    /**
     * 商户端菜单缓存树
     *
     * @return List
     */
    @Override
    public List<MenuCheckVo> getMenuCacheList() {
        List<SystemMenu> menuList = getCacheList(RoleEnum.MERCHANT_ADMIN.getValue());
        List<MenuCheckVo> voList = menuList.stream().map(e -> {
            MenuCheckVo menuCheckVo = new MenuCheckVo();
            BeanUtils.copyProperties(e, menuCheckVo);
            return menuCheckVo;
        }).collect(Collectors.toList());
        MenuCheckTree menuTree = new MenuCheckTree(voList);
        return menuTree.buildTree();
    }

    /**
     * 获取菜单缓存列表
     *
     * @param type 3-平台端，4-商户端
     * @return List
     */
    @Override
    public List<SystemMenu> getMenuCacheList(Integer type) {
        return getCacheList(type);
    }

    /**
     * 平台菜单列表
     *
     * @param request 请求参数
     * @return List
     */
    @Override
    public List<SystemMenu> getPlatformList(SystemMenuSearchRequest request) {
        return getAdminList(request, RoleEnum.PLATFORM_ADMIN.getValue());
    }

    /**
     * 商户菜单列表
     *
     * @param request 请求参数
     * @return List
     */
    @Override
    public List<SystemMenu> getMerchantList(SystemMenuSearchRequest request) {
        return getAdminList(request, RoleEnum.MERCHANT_ADMIN.getValue());
    }

    /**
     * 新增平台菜单
     *
     * @param systemMenuRequest 菜单参数
     * @return Boolean
     */
    @Override
    public Boolean addPlatformMenu(SystemMenuRequest systemMenuRequest) {
        return add(systemMenuRequest, RoleEnum.PLATFORM_ADMIN.getValue());
    }

    /**
     * 新增商户菜单
     *
     * @param systemMenuRequest 菜单参数
     * @return Boolean
     */
    @Override
    public Boolean addMerchantMenu(SystemMenuRequest systemMenuRequest) {
        return add(systemMenuRequest, RoleEnum.MERCHANT_ADMIN.getValue());
    }

    /**
     * 新增菜单
     *
     * @param request 菜单参数
     * @param type    系统菜单类型：3-平台,4-商户
     * @return Boolean
     */
    private Boolean add(SystemMenuRequest request, Integer type) {
        if (request.getMenuType().equals(MenuConstants.TYPE_C) && StrUtil.isEmpty(request.getComponent())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "菜单类型的组件路径不能为空");
        }
        if (request.getMenuType().equals(MenuConstants.TYPE_A) && StrUtil.isEmpty(request.getPerms())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "按钮类型的权限表示不能为空");
        }
        SystemMenu systemMenu = new SystemMenu();
        request.setId(null);
        BeanUtils.copyProperties(request, systemMenu);
        systemMenu.setType(type);
        Boolean execute = transactionTemplate.execute(e -> {
            save(systemMenu);
            SystemRoleMenu systemRoleMenu = new SystemRoleMenu();
            systemRoleMenu.setMenuId(systemMenu.getId());
            if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                systemRoleMenu.setRid(1);
            }
            // 如果是商户菜单，直接修改商户账号对应权限
            if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                systemRoleMenu.setRid(2);
            }
            roleMenuService.save(systemRoleMenu);
            return Boolean.TRUE;
        });
        if (execute) {
            if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                redisUtil.delete(RedisConstants.PLATFORM_MENU_CACHE_LIST_KEY);
            }
            if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                redisUtil.delete(RedisConstants.MERCHANT_MENU_CACHE_LIST_KEY);
            }
        }
        return execute;
    }

    /**
     * 删除平台端菜单
     *
     * @param id 菜单id
     * @return Boolean
     */
    @Override
    public Boolean deletePlatformMenu(Integer id) {
        return deleteById(id, RoleEnum.PLATFORM_ADMIN.getValue());
    }

    /**
     * 删除商户端菜单
     *
     * @param id 菜单id
     * @return Boolean
     */
    @Override
    public Boolean deleteMerchantMenu(Integer id) {
        return deleteById(id, RoleEnum.MERCHANT_ADMIN.getValue());
    }

    /**
     * 修改平台端菜单
     *
     * @param systemMenuRequest 菜单参数
     * @return Boolean
     */
    @Override
    public Boolean editPlatformMenu(SystemMenuRequest systemMenuRequest) {
        return edit(systemMenuRequest, RoleEnum.PLATFORM_ADMIN.getValue());
    }

    /**
     * 修改商户端菜单
     *
     * @param systemMenuRequest 菜单参数
     * @return Boolean
     */
    @Override
    public Boolean editMerchantMenu(SystemMenuRequest systemMenuRequest) {
        return edit(systemMenuRequest, RoleEnum.MERCHANT_ADMIN.getValue());
    }

    /**
     * 获取菜单详情
     *
     * @param id 菜单id
     * @return SystemMenu
     */
    @Override
    public SystemMenu getInfo(Integer id) {
        SystemMenu systemMenu = getInfoById(id);
        systemMenu.setCreateTime(null);
        return systemMenu;
    }

    /**
     * 修改平台端菜单显示状态
     *
     * @param id 菜单id
     * @return Boolean
     */
    @Override
    public Boolean updatePlatformShowStatus(Integer id) {
        return updateShowStatus(id, RoleEnum.PLATFORM_ADMIN.getValue());
    }

    /**
     * 修改商户端菜单显示状态
     *
     * @param id 菜单id
     * @return Boolean
     */
    @Override
    public Boolean updateMerchantShowStatus(Integer id) {
        return updateShowStatus(id, RoleEnum.MERCHANT_ADMIN.getValue());
    }

    /**
     * 平台端菜单缓存树
     *
     * @return List
     */
    @Override
    public List<MenuCheckVo> getPlatformMenuCacheTree() {
        List<SystemMenu> menuList = getCacheList(RoleEnum.PLATFORM_ADMIN.getValue());
        List<MenuCheckVo> voList = menuList.stream().map(e -> {
            MenuCheckVo menuCheckVo = new MenuCheckVo();
            BeanUtils.copyProperties(e, menuCheckVo);
            return menuCheckVo;
        }).collect(Collectors.toList());
        MenuCheckTree menuTree = new MenuCheckTree(voList);
        return menuTree.buildTree();
    }

    /**
     * 修改菜单显示状态
     *
     * @param id   菜单id
     * @param type 系统菜单类型：platform-平台,merchant-商户
     * @return Boolean
     */
    private Boolean updateShowStatus(Integer id, Integer type) {
        SystemMenu systemMenu = getInfoById(id);
        if (!systemMenu.getType().equals(type)) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        systemMenu.setIsShow(!systemMenu.getIsShow());
        boolean update = updateById(systemMenu);
        if (update) {
            if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                redisUtil.delete(RedisConstants.PLATFORM_MENU_CACHE_LIST_KEY);
            }
            if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                redisUtil.delete(RedisConstants.MERCHANT_MENU_CACHE_LIST_KEY);
            }
        }
        return update;
    }

    /**
     * 修改菜单
     *
     * @param request 菜单参数
     * @param type    系统菜单类型：platform-平台,merchant-商户
     * @return Boolean
     */
    private Boolean edit(SystemMenuRequest request, Integer type) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统菜单id不能为空");
        }
        if (request.getMenuType().equals(MenuConstants.TYPE_C) && StrUtil.isEmpty(request.getComponent())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "菜单类型的组件路径不能为空");
        }
        if (request.getMenuType().equals(MenuConstants.TYPE_A) && StrUtil.isEmpty(request.getPerms())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "按钮类型的权限表示不能为空");
        }
        SystemMenu oldMenu = getInfoById(request.getId());
        if (!oldMenu.getType().equals(type)) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        SystemMenu systemMenu = new SystemMenu();
        BeanUtils.copyProperties(request, systemMenu);
        boolean update = updateById(systemMenu);
        if (update) {
            if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                redisUtil.delete(RedisConstants.PLATFORM_MENU_CACHE_LIST_KEY);
            }
            if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                redisUtil.delete(RedisConstants.MERCHANT_MENU_CACHE_LIST_KEY);
            }
        }
        return update;
    }

    /**
     * 获取详细信息
     *
     * @param id 菜单id
     * @return SystemMenu
     */
    private SystemMenu getInfoById(Integer id) {
        SystemMenu systemMenu = getById(id);
        if (ObjectUtil.isNull(systemMenu) || systemMenu.getIsDelte()) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        return systemMenu;
    }

    /**
     * 根据id删除菜单
     *
     * @param id   菜单id
     * @param type 系统菜单类型：platform-平台,merchant-商户
     * @return Boolean
     */
    private Boolean deleteById(Integer id, Integer type) {
        SystemMenu systemMenu = getInfoById(id);
        if (!systemMenu.getType().equals(type)) {
            throw new CrmebException(SystemConfigResultCode.MENU_NOT_EXIST);
        }
        systemMenu.setIsDelte(true);
        String redisKey = type.equals(RoleEnum.PLATFORM_ADMIN.getValue()) ? RedisConstants.PLATFORM_MENU_CACHE_LIST_KEY : RedisConstants.MERCHANT_MENU_CACHE_LIST_KEY;
        if (systemMenu.getMenuType().equals(MenuConstants.TYPE_A)) {
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(systemMenu);
                if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                    roleMenuService.deleteByRidAndMenuId(1, systemMenu.getId());
                }
                // 如果是商户菜单，直接删除商户角色对应权限
                if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                    roleMenuService.deleteByRidAndMenuId(2, systemMenu.getId());
                }
                return Boolean.TRUE;
            });
            if (execute) {
                redisUtil.delete(redisKey);
            }
            return execute;
        }
        List<SystemMenu> childList = findAllChildListByPid(id, systemMenu.getMenuType());
        if (CollUtil.isEmpty(childList)) {
            Boolean execute = transactionTemplate.execute(e -> {
                updateById(systemMenu);
                if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                    roleMenuService.deleteByRidAndMenuId(1, systemMenu.getId());
                }
                // 如果是商户菜单，直接删除商户角色对应权限
                if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                    roleMenuService.deleteByRidAndMenuId(2, systemMenu.getId());
                }
                return Boolean.TRUE;
            });
            if (execute) {
                redisUtil.delete(redisKey);
            }
            return execute;
        }
        childList.forEach(e -> e.setIsDelte(true));
        childList.add(systemMenu);
        Boolean execute = transactionTemplate.execute(e -> {
            updateBatchById(childList);
            // 如果是商户菜单，直接删除商户角色对应权限
            List<Integer> menuIdList = childList.stream().map(SystemMenu::getId).collect(Collectors.toList());
            if (type.equals(RoleEnum.PLATFORM_ADMIN.getValue())) {
                roleMenuService.deleteByRidAndMenuIdList(1, menuIdList);
            }
            if (type.equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
                roleMenuService.deleteByRidAndMenuIdList(2, menuIdList);
            }
            return Boolean.TRUE;
        });
        if (execute) {
            redisUtil.delete(redisKey);
        }
        return execute;
    }

    /**
     * 根据菜单id获取所有下级对象
     *
     * @param pid      菜单id
     * @param menuType 类型，M-目录，C-菜单
     * @return List<SystemMenu>
     */
    private List<SystemMenu> findAllChildListByPid(Integer pid, String menuType) {
        LambdaQueryWrapper<SystemMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemMenu::getPid, pid);
        lqw.eq(SystemMenu::getIsDelte, false);
        if (menuType.equals(MenuConstants.TYPE_C)) {
            return dao.selectList(lqw);
        }
        List<SystemMenu> menuList = dao.selectList(lqw);
        if (CollUtil.isEmpty(menuList)) {
            return menuList;
        }
        List<Integer> pidList = menuList.stream().map(SystemMenu::getId).collect(Collectors.toList());
        lqw.clear();
        lqw.in(SystemMenu::getPid, pidList);
        lqw.eq(SystemMenu::getIsDelte, false);
        List<SystemMenu> childMenuList = dao.selectList(lqw);
        menuList.addAll(childMenuList);
        return menuList;
    }

    /**
     * 菜单列表
     *
     * @param request 请求参数
     * @param type    系统菜单类型：3-平台,4-商户
     */
    private List<SystemMenu> getAdminList(SystemMenuSearchRequest request, Integer type) {
        LambdaQueryWrapper<SystemMenu> lqw = Wrappers.lambdaQuery();
        if (StrUtil.isNotBlank(request.getName())) {
            String decode = URLUtil.decode(request.getName());
            lqw.like(SystemMenu::getName, decode);
        }
        if (StrUtil.isNotBlank(request.getMenuType())) {
            lqw.eq(SystemMenu::getMenuType, request.getMenuType());
        }
        lqw.eq(SystemMenu::getIsDelte, false);
        lqw.eq(SystemMenu::getType, type);
        lqw.orderByDesc(SystemMenu::getSort);
        lqw.orderByAsc(SystemMenu::getId);
        return dao.selectList(lqw);
    }

    /**
     * 获取菜单缓存列表
     *
     * @param type 系统菜单类型：3-平台,4-商户
     */
    private List<SystemMenu> getCacheList(Integer type) {
        String redisKey = type.equals(RoleEnum.PLATFORM_ADMIN.getValue()) ? RedisConstants.PLATFORM_MENU_CACHE_LIST_KEY : RedisConstants.MERCHANT_MENU_CACHE_LIST_KEY;
        if (redisUtil.exists(redisKey)) {
            return redisUtil.get(redisKey);
        }
        LambdaQueryWrapper<SystemMenu> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemMenu::getIsDelte, false);
        lqw.eq(SystemMenu::getType, type);
        List<SystemMenu> systemMenuList = dao.selectList(lqw);
        redisUtil.set(redisKey, systemMenuList);
        return systemMenuList;
    }
}

