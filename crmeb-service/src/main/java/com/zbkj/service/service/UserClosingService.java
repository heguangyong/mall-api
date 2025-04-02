package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.user.UserClosing;
import com.zbkj.common.request.*;

import java.util.List;

/**
 * UserExtractService 接口
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
public interface UserClosingService extends IService<UserClosing> {

    /**
     * 根据uid获取分页结算记录
     * @param uid 用户id
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserClosing> getPageByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 用户结算分页列表
     * @param request 搜索参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserClosing> getPlatformPage(UserClosingSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 用户结算申请审核
     * @param request 请求参数
     * @return Boolean
     */
    Boolean userClosingAudit(ClosingAuditRequest request);

    /**
     * 用户结算到账凭证
     */
    Boolean proof(ClosingProofRequest request);

    /**
     * 用户结算备注
     */
    Boolean remark(ClosingRemarkRequest request);

    /**
     * 获取用户对应的提现数据
     * @param userId 用户id
     * @return 提现数据
     */
    UserClosing getUserExtractByUserId(Integer userId);

    /**
     * 获取某一天的所有数据
     * @param date 日期：年-月-日
     * @return List
     */
    List<UserClosing> findByDate(String date);

//    List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest);
//
//    /**
//     * 提现总金额
//     */
//    BalanceResponse getBalance(String dateLimit);
//
//    /**
//     * 提现总金额
//
//     * @since 2020-05-11
//     * @return BalanceResponse
//     */
//    BigDecimal getWithdrawn(String startTime,String endTime);
//

//
//    /**
//     * 提现审核
//     * @param id    提现申请id
//     * @param status 审核状态 -1 未通过 0 审核中 1 已提现
//     * @param backMessage   驳回原因
//     * @return  审核结果
//     */
//    Boolean updateStatus(Integer id,Integer status,String backMessage);
//
//    /**
//     * 获取提现记录列表
//     * @param userId 用户uid
//     * @param pageParamRequest 分页参数
//     * @return PageInfo
//     */
//    PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest);
//
//    BigDecimal getExtractTotalMoney(Integer userId);
//
//    /**
//     * 提现申请
//     * @return Boolean
//     */
//    Boolean extractApply(UserExtractRequest request);
//
//    /**
//     * 修改提现申请
//     * @param id 申请id
//     * @param userExtractRequest 具体参数
//     */
//    Boolean updateExtract(Integer id, UserExtractRequest userExtractRequest);
//
//    /**
//     * 提现申请待审核数量
//     * @return Integer
//     */
//    Integer getNotAuditNum();
}
