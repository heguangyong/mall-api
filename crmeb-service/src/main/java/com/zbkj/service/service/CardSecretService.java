package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.cdkey.CardSecret;
import com.zbkj.common.request.BatchOperationCommonRequest;
import com.zbkj.common.request.CardSecretAddRequest;
import com.zbkj.common.request.CardSecretSaveRequest;
import com.zbkj.common.request.CardSecretSearchRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * CardSecretService 接口
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
public interface CardSecretService extends IService<CardSecret> {

    /**
     * 卡密分页列表
     */
    PageInfo<CardSecret> findPageList(CardSecretSearchRequest request);

    /**
     * 根据卡密库ID删除
     * @param libraryId 卡密库ID
     */
    Boolean deleteByLibraryId(Integer libraryId);

    /**
     * 新增卡密
     * @param request 新增参数
     * @return 新增结果
     */
    Boolean add(CardSecretAddRequest request);

    /**
     * 删除卡密
     * @param id 卡密ID
     * @return 删除结果
     */
    Boolean delete(Integer id);

    /**
     * 编辑卡密
     * @param request 编辑参数
     * @return 结果
     */
    Boolean edit(CardSecretSaveRequest request);

    /**
     * 导入卡密
     * @param file 导入文件
     * @return 导入结果
     */
    Boolean addImportExcel(MultipartFile file, Integer libraryId);

    /**
     * 批量删除卡密
     */
    Boolean batchDelete(BatchOperationCommonRequest request);

    /**
     * 消费一条卡密，并返回卡密对象
     * @param libraryId 卡密库ID
     */
    CardSecret consume(Integer libraryId);

    /**
     * 取消消费
     * @param cardSecretIdList 卡密ID列表
     */
    Boolean cancelConsume(List<Integer> cardSecretIdList);

    /**
     * 获取卡密列表根据ID列表
     * @param cardSecretIdList 卡密ID列表
     */
    List<CardSecret> findByIds(List<Integer> cardSecretIdList);
}