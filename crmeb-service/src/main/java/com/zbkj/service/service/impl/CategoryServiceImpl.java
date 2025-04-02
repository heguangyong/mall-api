package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.constants.CategoryConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.category.Category;
import com.zbkj.common.request.CategoryRequest;
import com.zbkj.common.request.CategorySearchRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.CategoryTreeVo;
import com.zbkj.service.dao.CategoryDao;
import com.zbkj.service.service.CategoryService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CategoryServiceImpl 接口实现
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
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Resource
    private CategoryDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SystemRoleService systemRoleService;


    /**
     * 获取分类下子类的数量
     *
     * @param request          请求参数
     * @param pageParamRequest 分页参数
     * @return List<Category>
     */
    @Override
    public List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getPid())) {
            lqw.eq(Category::getPid, request.getPid());
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            lqw.eq(Category::getType, request.getType());
        }
        if (ObjectUtil.isNotNull(request.getStatus()) && request.getStatus() >= 0) {
            lqw.eq(Category::getStatus, request.getStatus().equals(CategoryConstants.CATEGORY_STATUS_NORMAL));
        }
        if (StrUtil.isNotBlank(request.getName())) {
            String decode = URLUtil.decode(request.getName());
            lqw.like(Category::getName, decode);
        }
        lqw.eq(Category::getOwner, systemRoleService.getOwnerByCurrentAdmin());
        lqw.orderByDesc(Category::getSort).orderByDesc(Category::getId);
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(lqw);
    }

    /**
     * 通过id集合获取列表
     *
     * @param idList List<Integer> id集合
     * @return List<Category>
     */
    @Override
    public List<Category> getByIds(List<Integer> idList) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Category::getId, idList);
        lambdaQueryWrapper.in(Category::getOwner, systemRoleService.getOwnerByCurrentAdmin());
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 修改
     *
     * @param request CategoryRequest
     * @param id      Integer
     * @return bool
     */
    @Override
    public Boolean update(CategoryRequest request, Integer id) {
        if (id <= 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "id 参数不合法");
        }
        Category oldCategory = getById(id);
        if (ObjectUtil.isNull(oldCategory)) {
            throw new CrmebException(SystemConfigResultCode.CATEGORY_NOT_EXIST);
        }
        if (oldCategory.getId().equals(request.getPid())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类的父级不能为自己");
        }
        Integer ownerId = systemRoleService.getOwnerByCurrentAdmin();
        if (!oldCategory.getOwner().equals(ownerId)) {
            throw new CrmebException(SystemConfigResultCode.CATEGORY_NOT_EXIST);
        }
        if (!oldCategory.getName().equals(request.getName())) {
            if (checkName(request.getName(), request.getType(), ownerId, oldCategory.getId())) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "分类名称重复");
            }
        }
        //修改分类信息
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        category.setId(id);
        category.setPath(getPathByPId(category.getPid()));
        if (StrUtil.isNotBlank(request.getExtra())) {
            category.setExtra(systemAttachmentService.clearPrefix(request.getExtra()));
        }
        category.setOwner(ownerId);
        return transactionTemplate.execute(e -> {
            updateById(category);
            //如状态为关闭，那么所以子集的状态都关闭
            if (!request.getStatus()) {
                updateStatusByPid(id, false);
            } else {
                //如是开启，则父类的状态为开启
                updatePidStatusById(id);
            }
            return Boolean.TRUE;
        });
    }

    /**
     * 开启父级状态
     *
     * @param id Integer
     * @return Boolean
     */
    private Boolean updatePidStatusById(Integer id) {
        Category category = getById(id);
        if (StrUtil.isBlank(category.getPath())) {
            return Boolean.TRUE;
        }
        List<Integer> categoryIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
        categoryIdList.removeIf(i -> i.equals(0));
        if (CollUtil.isEmpty(categoryIdList)) {
            return Boolean.FALSE;
        }
        LambdaUpdateWrapper<Category> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Category::getStatus, true);
        wrapper.in(Category::getId, categoryIdList);
        return update(wrapper);
    }

    /**
     * 获取分类下子类的数量
     *
     * @param pid Integer
     * @return bool
     */
    private int getChildCountByPid(Integer pid) {
        //查看是否有子类
        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/" + pid + "/");
        return dao.selectCount(objectQueryWrapper);
    }

    /**
     * 修改分类以及子类的状态
     *
     * @param pid Integer
     * @return bool
     */
    private Boolean updateStatusByPid(Integer pid, Boolean status) {
        //查看是否有子类
        LambdaUpdateWrapper<Category> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(Category::getStatus, status);
        wrapper.like(Category::getPath, "/" + pid + "/");
        return update(wrapper);
    }

    private String getPathByPId(Integer pid) {
        Category category = getById(pid);
        if (ObjectUtil.isNotNull(category)) {
            return category.getPath() + pid + "/";
        }
        return "";
    }

    /**
     * 带结构的无线级分类
     */
    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, String name) {
        name = URLUtil.decode(name);
        return getTree(type, status, name, null);
    }

    /**
     * 带结构的无线级分类
     */
    private List<CategoryTreeVo> getTree(Integer type, Integer status, String name, List<Integer> categoryIdList) {
        //循环数据，把数据对象变成带list结构的vo
        List<CategoryTreeVo> treeList = new ArrayList<>();

        LambdaQueryWrapper<Category> lqw = Wrappers.lambdaQuery();
        lqw.eq(Category::getType, type);
        if (CollUtil.isNotEmpty(categoryIdList)) {
            lqw.in(Category::getId, categoryIdList);
        }
        if (status >= 0) {
            lqw.eq(Category::getStatus, status);
        }
        if (StrUtil.isNotBlank(name)) { // 根据名称模糊搜索
            lqw.like(Category::getName, name);
        }
        lqw.eq(Category::getOwner, systemRoleService.getOwnerByCurrentAdmin());
        lqw.orderByDesc(Category::getSort);
        lqw.orderByAsc(Category::getId);
        List<Category> allTree = dao.selectList(lqw);
        if (CollUtil.isEmpty(allTree)) {
            return CollUtil.newArrayList();
        }
        // 根据名称搜索特殊处理 这里仅仅处理两层搜索后有子父级关系的数据
        if (StrUtil.isNotBlank(name) && CollUtil.isNotEmpty(allTree)) {
            List<Category> searchCategory = new ArrayList<>();
            List<Integer> categoryIds = allTree.stream().map(Category::getId).collect(Collectors.toList());

            List<Integer> pidList = allTree.stream().filter(c -> c.getPid() > 0 && !categoryIds.contains(c.getPid()))
                    .map(Category::getPid).distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(pidList)) {
                pidList.forEach(pid -> {
                    searchCategory.add(dao.selectById(pid));
                });
            }
            allTree.addAll(searchCategory);
        }

        for (Category category : allTree) {
            CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
            BeanUtils.copyProperties(category, categoryTreeVo);
            treeList.add(categoryTreeVo);
        }

        //返回
        Map<Integer, CategoryTreeVo> map = new HashMap<>();
        //ID 为 key 存储到map 中
        for (CategoryTreeVo categoryTreeVo1 : treeList) {
            map.put(categoryTreeVo1.getId(), categoryTreeVo1);
        }

        List<CategoryTreeVo> list = new ArrayList<>();
        for (CategoryTreeVo tree : treeList) {
            //子集ID返回对象，有则添加。
            CategoryTreeVo tree1 = map.get(tree.getPid());
            if (ObjectUtil.isNotNull(tree1)) {
                tree1.getChild().add(tree);
            } else {
                list.add(tree);
            }
        }
        System.out.println("无限极分类 : getTree:" + JSON.toJSONString(list));
        return list;
    }

    /**
     * 删除分类表
     *
     * @param id Integer
     */
    @Override
    public int delete(Integer id) {
        //查看是否有子类, 物理删除
        if (getChildCountByPid(id) > 0) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "当前分类下有子类，请先删除子类！");
        }
        Category category = getByIdException(id);
        Integer owner = systemRoleService.getOwnerByCurrentAdmin();
        if (!owner.equals(category.getOwner())) {
            throw new CrmebException(SystemConfigResultCode.CATEGORY_NOT_EXIST);
        }
        return dao.deleteById(id);
    }

    /**
     * 检测分类名称是否存在
     *
     * @param name String 分类名
     * @param type int 类型
     * @return int
     */
    private Boolean checkName(String name, Integer type, Integer owner, Integer id) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getName, name);
        lambdaQueryWrapper.eq(Category::getOwner, owner);
        if (ObjectUtil.isNotNull(type)) {
            lambdaQueryWrapper.eq(Category::getType, type);
        }
        if (id > 0) {
            lambdaQueryWrapper.ne(Category::getId, id);
        }
        return dao.selectCount(lambdaQueryWrapper) > 0;
    }

    /**
     * 更改基础分类状态
     */
    @Override
    public Boolean updateStatus(Integer id) {
        Category category = getById(id);
        Integer owner = systemRoleService.getOwnerByCurrentAdmin();
        if (!owner.equals(category.getOwner())) {
            throw new CrmebException(SystemConfigResultCode.CATEGORY_NOT_EXIST);
        }
        category.setStatus(!category.getStatus());
        return updateById(category);
    }

    /**
     * 新增分类
     */
    @Override
    public Boolean create(CategoryRequest categoryRequest) {
        Integer ownerId = systemRoleService.getOwnerByCurrentAdmin();
        //检测标题是否存在
        if (checkName(categoryRequest.getName(), categoryRequest.getType(), ownerId, 0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "此分类已存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        category.setPath(getPathByPId(category.getPid()));
        category.setExtra(systemAttachmentService.clearPrefix(category.getExtra()));
        category.setOwner(ownerId);
        return save(category);
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return Category
     */
    @Override
    public Category getByIdException(Integer id) {
        Category category = getById(id);
        if (ObjectUtil.isNull(category)) {
            throw new CrmebException(SystemConfigResultCode.CATEGORY_NOT_EXIST);
        }
        return category;
    }

    /**
     * 获取基础分类信息
     * @param id 分类ID
     * @return Category
     */
    @Override
    public Category getBaseCategoryInfo(Integer id) {
        Category category = getByIdException(id);
        Integer owner = systemRoleService.getOwnerByCurrentAdmin();
        if (!owner.equals(category.getOwner())) {
            throw new CrmebException(SystemConfigResultCode.CATEGORY_NOT_EXIST);
        }
        return category;
    }

}

