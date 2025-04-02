package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemFormAddRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.dao.SystemFormDao;
import com.zbkj.service.service.ProductService;
import com.zbkj.service.service.SystemFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description SystemFormServiceImpl 接口实现
 * @date 2024-08-01
 */
@Service
public class SystemFormServiceImpl extends ServiceImpl<SystemFormDao, SystemForm> implements SystemFormService {

    @Resource
    private SystemFormDao dao;

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ProductService productService;

    /**
     * 获取商户侧系统表单分页列表
     *
     * @param pageRequest 分页参数
     */
    @Override
    public PageInfo<SystemForm> getMerchantPage(PageParamRequest pageRequest) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        LambdaQueryWrapper<SystemForm> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemForm::getId, SystemForm::getFormName, SystemForm::getCreateTime, SystemForm::getUpdateTime);
        lqw.eq(SystemForm::getIsDelete, 0);
        lqw.eq(SystemForm::getMerId, admin.getMerId());
        lqw.orderByDesc(SystemForm::getId);
        Page<SystemForm> page = PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        List<SystemForm> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 添加系统表单
     */
    @Override
    public MyRecord addForm(SystemFormAddRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemForm systemForm = new SystemForm();
        systemForm.setFormName(request.getFormName());
        systemForm.setAllKeys(StrUtil.isNotBlank(request.getAllKeys()) ? request.getAllKeys() : "");
        if (StrUtil.isNotBlank(request.getFormValue())) {
            systemForm.setFormValue(JSONObject.parseObject(request.getFormValue()).toJSONString());
        } else {
            systemForm.setFormValue("");
        }
        systemForm.setMerId(admin.getMerId());
        boolean save = save(systemForm);
        if (!save) {
            throw new CrmebException(CommonResultCode.ERROR, "保存系统表单异常");
        }
        MyRecord record = new MyRecord();
        record.set("id", systemForm.getId());
        return record;
    }

    /**
     * 修改系统表单
     */
    @Override
    public Boolean updateForm(SystemFormAddRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemForm systemForm = getByIdAndMerValidator(request.getId(), admin.getMerId());
        systemForm.setFormName(request.getFormName());
        systemForm.setAllKeys(StrUtil.isNotBlank(request.getAllKeys()) ? request.getAllKeys() : "");
        if (StrUtil.isNotBlank(request.getFormValue())) {
            systemForm.setFormValue(JSONObject.parseObject(request.getFormValue()).toJSONString());
        } else {
            systemForm.setFormValue("");
        }
        systemForm.setUpdateTime(DateUtil.date());
        return updateById(systemForm);
    }

    /**
     * 删除系统表单
     */
    @Override
    public Boolean deleteForm(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        SystemForm systemForm = getByIdAndMerValidator(id, admin.getMerId());
        systemForm.setIsDelete(true);
        systemForm.setUpdateTime(DateUtil.date());
        return transactionTemplate.execute(e -> {
            updateById(systemForm);
            productService.clearSystemFormByFormId(id);
            return Boolean.TRUE;
        });
    }

    /**
     * 获取系统表单详情
     */
    @Override
    public SystemForm getDetail(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return getByIdAndMerValidator(id, admin.getMerId());
    }

    /**
     * 是否存在系统表单
     *
     * @param id    表单ID
     * @param merId 门店ID
     */
    @Override
    public Boolean isExist(Integer id, Integer merId) {
        LambdaQueryWrapper<SystemForm> lqw = Wrappers.lambdaQuery();
        lqw.select(SystemForm::getId);
        lqw.eq(SystemForm::getId, id);
        lqw.eq(SystemForm::getMerId, merId);
        lqw.eq(SystemForm::getIsDelete, 0);
        return dao.selectCount(lqw) > 0;
    }

    /**
     * 移动端获取系统表单详情
     */
    @Override
    public SystemForm getFrontDetail(Integer id) {
        return getByIdException(id);
    }

    private SystemForm getByIdException(Integer id) {
        SystemForm systemForm = getById(id);
        if (ObjectUtil.isNull(systemForm) || systemForm.getIsDelete()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统表单不存在");
        }
        return systemForm;
    }

    private SystemForm getByIdAndMerValidator(Integer id, Integer merId) {
        SystemForm systemForm = getByIdException(id);
        if (!systemForm.getMerId().equals(merId)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "系统表单不存在");
        }
        return systemForm;
    }
}

