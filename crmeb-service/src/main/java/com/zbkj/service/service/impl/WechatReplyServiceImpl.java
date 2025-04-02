package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.wechat.WechatReply;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.WechatReplyRequest;
import com.zbkj.common.request.WechatReplySearchRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.WechatReplyDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.WechatReplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WechatReplyServiceImpl 接口实现
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
public class WechatReplyServiceImpl extends ServiceImpl<WechatReplyDao, WechatReply> implements WechatReplyService {

    @Resource
    private WechatReplyDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;


    /**
     * 列表
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     * @return List<WechatReply>
     */
    @Override
    public List<WechatReply> getList(WechatReplySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<WechatReply> lqw = new LambdaQueryWrapper<>();
        //类型
        if (StringUtils.isNotBlank(request.getType())) {
            lqw.eq(WechatReply::getType, request.getType());
        }
        //关键字
        if (StringUtils.isNotBlank(request.getKeywords())) {
            lqw.eq(WechatReply::getKeywords, URLUtil.decode(request.getKeywords()));
        }
        lqw.orderByDesc(WechatReply::getId);
        return dao.selectList(lqw);
    }

    /**
     * 新增微信关键字回复表
     *
     * @param wechatReplyRequest 新增参数
     */
    @Override
    public Boolean create(WechatReplyRequest wechatReplyRequest) {
        //检测重复
        WechatReply voByKeywords = getVoByKeywords(wechatReplyRequest.getKeywords());
        if (ObjectUtil.isNotNull(voByKeywords)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, wechatReplyRequest.getKeywords() + "关键字已经存在");
        }
        WechatReply wechatReply = new WechatReply();
        BeanUtils.copyProperties(wechatReplyRequest, wechatReply);
        wechatReply.setData(systemAttachmentService.clearPrefix(wechatReply.getData()));
        return save(wechatReply);
    }

    /**
     * 修改微信关键字回复表
     *
     * @param wechatReply 修改参数
     */
    private Boolean updateVo(WechatReply wechatReply) {
        //检测重复
        WechatReply voByKeywords = getVoByKeywords(wechatReply.getKeywords());
        if (ObjectUtil.isNotNull(voByKeywords) && !wechatReply.getId().equals(voByKeywords.getId())) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, wechatReply.getKeywords() + "关键字已经存在");
        }
        wechatReply.setData(systemAttachmentService.clearPrefix(wechatReply.getData()));
        return updateById(wechatReply);
    }


    /**
     * 根据关键字查询数据
     *
     * @param keywords 新增参数
     * @return WechatReply
     */
    @Override
    public WechatReply getVoByKeywords(String keywords) {
        //检测重复
        LambdaQueryWrapper<WechatReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(WechatReply::getKeywords, keywords);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 根据关键字查询数据
     *
     * @param id Integer id
     * @return WechatReply
     */
    private WechatReply getInfoException(Integer id) {
        //检测重复
        WechatReply info = getInfo(id);
        if (null == info) {
            throw new CrmebException("没有找到相关数据");
        }
        return info;
    }

    /**
     * 根据关键字查询数据
     *
     * @param id Integer id
     * @return WechatReply
     */
    @Override
    public WechatReply getInfo(Integer id) {
        return getById(id);
    }

    /**
     * 修改微信关键字回复表
     *
     * @param wechatReplyRequest 修改参数
     */
    @Override
    public Boolean updateReply(WechatReplyRequest wechatReplyRequest) {
        WechatReply wechatReply = getInfoException(wechatReplyRequest.getId());
        BeanUtils.copyProperties(wechatReplyRequest, wechatReply);
        return updateVo(wechatReply);
    }

    /**
     * 修改状态
     *
     * @param id integer id
     */
    @Override
    public Boolean updateStatus(Integer id) {
        WechatReply wechatReply = getInfoException(id);
        wechatReply.setStatus(!wechatReply.getStatus());
        return updateVo(wechatReply);
    }

    /**
     * 根据关键字查询数据
     *
     * @param keywords 新增参数
     * @return WechatReply
     */
    @Override
    public WechatReply keywordsInfo(String keywords) {
        if (StrUtil.isBlank(keywords)) {
            return null;
        }
        keywords = URLUtil.decode(keywords);
        return getVoByKeywords(keywords);
    }

}

