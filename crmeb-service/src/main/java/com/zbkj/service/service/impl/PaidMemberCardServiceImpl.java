package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.member.PaidMemberCard;
import com.zbkj.common.request.PaidMemberCardSaveRequest;
import com.zbkj.common.request.PaidMemberCardSearchRequest;
import com.zbkj.common.response.PaidMemberCardResponse;
import com.zbkj.common.response.SvipCardResponse;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.MemberResultCode;
import com.zbkj.service.dao.PaidMemberCardDao;
import com.zbkj.service.service.PaidMemberCardService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author hzw
* @description PaidMemberCardServiceImpl 接口实现
* @date 2024-05-10
*/
@Service
public class PaidMemberCardServiceImpl extends ServiceImpl<PaidMemberCardDao, PaidMemberCard> implements PaidMemberCardService {

    @Resource
    private PaidMemberCardDao dao;

    /**
     * 添加付费会员卡
     */
    @Override
    public Boolean add(PaidMemberCardSaveRequest request) {
        if (request.getType() <= 1) {
            if (ObjectUtil.isNull(request.getDeadlineDay()) || request.getDeadlineDay() < 1 || request.getDeadlineDay() > 9999) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "期限天数范围为：1~9999");
            }
        }
        PaidMemberCard card = new PaidMemberCard();
        card.setName(request.getName());
        if (StrUtil.isNotBlank(request.getLabel())) {
            card.setLabel(request.getLabel());
        }
        card.setType(request.getType());
        card.setDeadlineDay(request.getType() > 1 ? 0 : request.getDeadlineDay());
        card.setOriginalPrice(request.getOriginalPrice());
        card.setPrice(request.getPrice());
        card.setIsFirstChargeGive(request.getIsFirstChargeGive());
        card.setGiftBalance(request.getGiftBalance());
        card.setSort(request.getSort());
        card.setStatus(request.getStatus());
        return save(card);
    }

    /**
     * 编辑付费会员卡
     */
    @Override
    public Boolean edit(PaidMemberCardSaveRequest request) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "会员卡ID不能为空");
        }
        PaidMemberCard card = getByIdException(request.getId());
        if (card.getType() <= 1) {
            if (ObjectUtil.isNull(request.getDeadlineDay()) || request.getDeadlineDay() < 1 || request.getDeadlineDay() > 9999) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "期限天数范围为：1~9999");
            }
        }
        card.setName(request.getName());
        card.setLabel(StrUtil.isNotBlank(request.getLabel()) ? request.getLabel() : "");
        card.setDeadlineDay(card.getType() > 1 ? 0 : request.getDeadlineDay());
        card.setOriginalPrice(request.getOriginalPrice());
        card.setPrice(request.getPrice());
        card.setIsFirstChargeGive(request.getIsFirstChargeGive());
        card.setGiftBalance(request.getGiftBalance());
        card.setSort(request.getSort());
        card.setStatus(request.getStatus());
        return updateById(card);
    }

    /**
     * 付费会员卡开关
     */
    @Override
    public Boolean editSwitch(Integer id) {
        PaidMemberCard card = getByIdException(id);
        LambdaUpdateWrapper<PaidMemberCard> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(PaidMemberCard::getStatus, !card.getStatus());
        wrapper.eq(PaidMemberCard::getId, id);
        return update(wrapper);
    }

    /**
     * 删除付费会员卡
     */
    @Override
    public Boolean deleteCard(Integer id) {
        PaidMemberCard card = getByIdException(id);
        card.setIsDelete(true);
        return updateById(card);
    }

    /**
     * 付费会员卡列表
     */
    @Override
    public List<PaidMemberCardResponse> findPlatList(PaidMemberCardSearchRequest request) {
        String name = "";
        if (StrUtil.isNotBlank(request.getName())) {
            name = URLUtil.decode(request.getName());
        }
        List<PaidMemberCard> list = findList(name, ObjectUtil.isNotNull(request.getType()) ? request.getType() : null, ObjectUtil.isNotNull(request.getStatus()) ? request.getStatus() : null);
        List<PaidMemberCardResponse> responseList = new ArrayList<>();
        for (PaidMemberCard card : list) {
            PaidMemberCardResponse response = new PaidMemberCardResponse();
            BeanUtils.copyProperties(card, response);
            responseList.add(response);
        }
        return responseList;
    }

    private List<PaidMemberCard> findList(String name, Integer type, Boolean status) {
        LambdaQueryWrapper<PaidMemberCard> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberCard::getIsDelete, 0);
        if (StrUtil.isNotBlank(name)) {
            lqw.like(PaidMemberCard::getName, name);
        }
        if (ObjectUtil.isNotNull(type)) {
            lqw.eq(PaidMemberCard::getType, type);
        }
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(PaidMemberCard::getStatus, status);
        }
        lqw.orderByDesc(PaidMemberCard::getSort, PaidMemberCard::getId);
        return dao.selectList(lqw);
    }

    @Override
    public PaidMemberCard getByIdException(Integer id) {
        PaidMemberCard paidMemberCard = getById(id);
        if (ObjectUtil.isNull(paidMemberCard) || paidMemberCard.getIsDelete()) {
            throw new CrmebException(MemberResultCode.PAID_MEMBER_CARD_NOT_EXIST);
        }
        return paidMemberCard;
    }

    /**
     * 付费会员卡列表(移动端)
     */
    @Override
    public List<SvipCardResponse> findFrontList(Boolean isPayTrial) {
        LambdaQueryWrapper<PaidMemberCard> lqw = Wrappers.lambdaQuery();
        lqw.eq(PaidMemberCard::getIsDelete, 0);
        if (isPayTrial) {
            lqw.in(PaidMemberCard::getType, 1, 2);
        }
        lqw.eq(PaidMemberCard::getStatus, 1);
        lqw.orderByDesc(PaidMemberCard::getSort, PaidMemberCard::getId);
        List<PaidMemberCard> list = dao.selectList(lqw);
        List<SvipCardResponse> responseList = new ArrayList<>();
        for (PaidMemberCard card : list) {
            SvipCardResponse response = new SvipCardResponse();
            BeanUtils.copyProperties(card, response);
            responseList.add(response);
        }
        return responseList;
    }
}

