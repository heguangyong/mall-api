package com.zbkj.service.service.groupbuy;


import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.groupbuy.GroupBuyActivity;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivityRequest;
import com.zbkj.common.request.groupbuy.GroupBuyActivitySearchRequest;
import com.zbkj.common.request.groupbuy.PatGroupBuyActivitySearchRequest;
import com.zbkj.common.response.groupbuy.GroupBuyActivityFrontResponse;
import com.zbkj.common.response.groupbuy.GroupBuyActivityListHeaderCount;
import com.zbkj.common.response.groupbuy.GroupBuyActivityResponse;
import com.zbkj.common.vo.MyRecord;

import java.util.List;

/**
* @author dazongzi
* @description GroupBuyActivityService 接口
* @date 2024-08-13
*/
public interface GroupBuyActivityService extends IService<GroupBuyActivity> {

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @author dazongzi
     * @since 2024-08-13
     * @return List<GroupBuyActivity>
     */
    PageInfo<GroupBuyActivityResponse> getList(PatGroupBuyActivitySearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 获取拼团头部 数据
     * @return 对应状态的数量
     */
    List<GroupBuyActivityListHeaderCount> getListHeaderCount(GroupBuyActivitySearchRequest request, SystemAdmin systemAdmin);

    /**
     *  新增拼团
     * @param request 拼团原始对象
     */
    Boolean addGroupBuyActivity(GroupBuyActivityRequest request);

    /**
     * 修改拼团
     * @param request 拼团待修改对象
     * @return Boolean
     */
    Boolean updateGroupBuyActivity(GroupBuyActivityRequest request);


    /**
     * 获取拼团详情
     * @param id 拼团活动id
     * @return 拼团活动详情
     */
    GroupBuyActivityResponse getGroupBuyActivity(Integer id);

    /**
     *  拼团活动详情 针对移动端
     * @param id 拼团活动id
     * @return 拼团活动详情
     */
    GroupBuyActivityResponse getGroupBuyActivityForFront(Integer id);

    /**
     * 拼团活动状态修改
     * @param groupBuyActivityId 拼团活动id
     * @param status 活动状态
     * @return Boolean
     */
    Boolean groupBuyActivityStatusOnOrOff(Integer groupBuyActivityId, Integer status);

    /**
     * 拼团状态审核
     * @param groupBuyActivityId 拼团活动id
     * @param groupStatus 拼团状态枚举值
     * @param reason 审核决绝原因
     * @return Boolean 结果
     */
    Boolean groupBuyGroupStatusProgress(Integer groupBuyActivityId, Integer groupStatus, String reason);

    /**
     * 平台强制关闭
     * @param groupBuyActivityId 拼团活动id
     * @param reason 关闭原因
     * @return  关闭结果
     */
    Boolean groupBuyGroupStatusProgressClose(Integer groupBuyActivityId, String reason);

    /**
     * 删除拼团活动
     * @param id 活动id
     * @return 删除结果
     */
    Boolean deleteGroupBuyActivity(Integer id);

    //////////////////////////////////////////////////////////////////////////////////////// 商城端业务

    /**
     * 拼团活动首页数据 移动端商城首页
     * @return GroupBuyActivityFrontResponse
     */
    GroupBuyActivityFrontResponse getGroupBuyActivityFrontIndex(Integer limit);

    /**
     * 商户首页拼团卡片数据获取
     * @param merId 商户ID
     * @param limit 商品条数
     */
    GroupBuyActivityFrontResponse getGroupBuyActivityMerchantFrontIndex(Integer merId, Integer limit);
}
