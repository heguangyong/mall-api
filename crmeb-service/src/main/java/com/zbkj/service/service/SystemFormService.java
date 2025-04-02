package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.system.SystemForm;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemFormAddRequest;
import com.zbkj.common.vo.MyRecord;

/**
 * @author HZW
 * @description SystemFormService 接口
 * @date 2024-08-01
 */
public interface SystemFormService extends IService<SystemForm> {

    /**
     * 获取商户侧系统表单分页列表
     *
     * @param pageRequest 分页参数
     */
    PageInfo<SystemForm> getMerchantPage(PageParamRequest pageRequest);

    /**
     * 添加系统表单
     */
    MyRecord addForm(SystemFormAddRequest request);

    /**
     * 修改系统表单
     */
    Boolean updateForm(SystemFormAddRequest request);

    /**
     * 删除系统表单
     */
    Boolean deleteForm(Integer id);

    /**
     * 获取系统表单详情
     */
    SystemForm getDetail(Integer id);

    /**
     * 是否存在系统表单
     * @param id 表单ID
     * @param merId 门店ID
     */
    Boolean isExist(Integer id, Integer merId);

    /**
     * 移动端获取系统表单详情
     */
    SystemForm getFrontDetail(Integer id);
}