package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.sgin.SignConfig;
import com.zbkj.common.request.SignConfigRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.result.SystemConfigResultCode;
import com.zbkj.service.dao.SignConfigDao;
import com.zbkj.service.service.SignConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
*  SignConfigServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class SignConfigServiceImpl extends ServiceImpl<SignConfigDao, SignConfig> implements SignConfigService {

    @Resource
    private SignConfigDao dao;

    /**
     * 获取全部配置列表
     * @return List
     */
    @Override
    public List<SignConfig> findList() {
        LambdaQueryWrapper<SignConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SignConfig::getIsDel, false);
        lqw.orderByDesc(SignConfig::getDay);
        return dao.selectList(lqw);
    }

    /**
     * 新增连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean add(SignConfigRequest request) {
        if (isExistDay(request.getDay(), null)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "存在相同的连续签到天数");
        }
        SignConfig signConfig = new SignConfig();
        BeanUtils.copyProperties(request, signConfig);
        signConfig.setMark(getMark(signConfig));
        return save(signConfig);
    }

    /**
     * 获取备注
     */
    private String getMark(SignConfig signConfig) {
        String mark = "";
        if (signConfig.getIsIntegral() && signConfig.getIsExperience()) {
            mark = StrUtil.format("赠送{}积分，赠送{}经验", signConfig.getIntegral(), signConfig.getExperience());
        } else if (signConfig.getIsIntegral()) {
            mark = StrUtil.format("赠送{}积分", signConfig.getIntegral());
        } else if (signConfig.getIsExperience()) {
            mark = StrUtil.format("赠送{}经验", signConfig.getExperience());
        }
        return mark;
    }

    /**
     * 删除连续签到配置
     * @param id 签到配置id
     * @return Boolean
     */
    @Override
    public Boolean delete(Integer id) {
        SignConfig config = getById(id);
        if (ObjectUtil.isNull(config) || config.getIsDel()) {
            return true;
        }
        if (config.getDay().equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "不能删除基础签到配置");
        }
        config.setIsDel(true);
        return updateById(config);
    }

    /**
     * 编辑基础签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean editBaseConfig(SignConfigRequest request) {
        SignConfig signConfig = getByDay(0);
        signConfig.setIsIntegral(request.getIsIntegral());
        signConfig.setIntegral(request.getIntegral());
        signConfig.setIsExperience(request.getIsExperience());
        signConfig.setExperience(request.getExperience());
        return updateById(signConfig);
    }

    /**
     * 编辑连续签到配置
     * @param request 配置参数
     * @return Boolean
     */
    @Override
    public Boolean editAwardConfig(SignConfigRequest request) {
        SignConfig signConfig = getByIdException(request.getId());
        if (!signConfig.getDay().equals(request.getDay())) {
            if (isExistDay(request.getDay(), null)) {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "存在相同的连续签到天数");
            }
        }
        BeanUtils.copyProperties(request, signConfig);
        signConfig.setMark(getMark(signConfig));
        return updateById(signConfig);
    }

    private SignConfig getByIdException(Integer id) {
        SignConfig signConfig = getById(id);
        if (ObjectUtil.isNull(signConfig) || signConfig.getIsDel()) {
            throw new CrmebException(SystemConfigResultCode.SIGN_COFNIG_NOT_EXIST);
        }
        return signConfig;
    }

    /**
     * 通过连续签到天数获取
     * @param day 连续签到天数
     */
    private SignConfig getByDay(Integer day) {
        LambdaQueryWrapper<SignConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SignConfig::getDay, day);
        lqw.eq(SignConfig::getIsDel, false);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 校验天数
     * @param day 连续签到天数
     * @param id 配置id
     */
    private Boolean isExistDay(Integer day, Integer id) {
        LambdaQueryWrapper<SignConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(SignConfig::getDay, day);
        if (ObjectUtil.isNotNull(id)) {
            lqw.ne(SignConfig::getId, id);
        }
        lqw.eq(SignConfig::getIsDel, false);
        lqw.last(" limit 1");
        SignConfig signConfig = dao.selectOne(lqw);
        return ObjectUtil.isNotNull(signConfig);
    }
}

