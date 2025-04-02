package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.cdkey.CdkeyLibrary;
import com.zbkj.common.request.CdkeyLibrarySaveRequest;
import com.zbkj.common.request.CdkeyLibrarySearchRequest;
import com.zbkj.common.response.CdkeyLibraryPageResponse;
import com.zbkj.common.response.CdkeyLibrarySimpleResponse;

import java.util.List;

/**
 * CdkeyLibraryService 接口
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
public interface CdkeyLibraryService extends IService<CdkeyLibrary> {

    /**
     * 分页列表
     *
     * @param request 搜索参数
     */
    PageInfo<CdkeyLibraryPageResponse> findPageList(CdkeyLibrarySearchRequest request);

    /**
     * 新增卡密库
     * @param request 新增参数
     * @return 新增结果
     */
    Boolean add(CdkeyLibrarySaveRequest request);

    /**
     * 删除卡密库
     * @param id 卡密库ID
     * @return 删除结果
     */
    Boolean delete(Integer id);

    /**
     * 修改卡密库
     * @param request 修改参数
     * @return 修改结果
     */
    Boolean updateLibrary(CdkeyLibrarySaveRequest request);

    /**
     * 未关联卡密库列表
     */
    List<CdkeyLibrarySimpleResponse> findUnrelatedList();

    CdkeyLibrary getByIdException(Integer id);

    /**
     * 添加/扣减库存
     * @param id 卡密库ID
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationTotalNum(Integer id, Integer num, String type);

    /**
     * 清除商品关联
     * @param proId 商品ID
     */
    Boolean clearAssociationProduct(Integer proId);

    /**
     * 清除商品关联
     * @param idList 卡密库ID列表
     */
    Boolean clearAssociationByIds(List<Integer> idList);

    List<CdkeyLibrary> findByIdList(List<Integer> cdkIdList);

    /**
     * 操作使用数量
     * @param id 卡密库ID
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationUseNum(Integer id, Integer num, String type);
}