package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.member.PaidMemberCard;
import com.zbkj.common.request.PaidMemberCardSaveRequest;
import com.zbkj.common.request.PaidMemberCardSearchRequest;
import com.zbkj.common.response.PaidMemberCardResponse;
import com.zbkj.common.response.SvipCardResponse;

import java.util.List;

/**
 * @author hzw
 * @description PaidMemberCardService 接口
 * @date 2024-05-10
 */
public interface PaidMemberCardService extends IService<PaidMemberCard> {

    /**
     * 添加付费会员卡
     */
    Boolean add(PaidMemberCardSaveRequest request);

    /**
     * 编辑付费会员卡
     */
    Boolean edit(PaidMemberCardSaveRequest request);

    /**
     * 付费会员卡开关
     */
    Boolean editSwitch(Integer id);

    /**
     * 删除付费会员卡
     */
    Boolean deleteCard(Integer id);

    /**
     * 付费会员卡列表
     */
    List<PaidMemberCardResponse> findPlatList(PaidMemberCardSearchRequest request);

    PaidMemberCard getByIdException(Integer id);

    /**
     * 付费会员卡列表(移动端)
     */
    List<SvipCardResponse> findFrontList(Boolean isPayTrial);
}