package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantElect;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.MerchantElectDao;
import com.zbkj.service.service.MerchantElectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Wrapper;

/**
* @author dazongzi
* @description MerchantElectServiceImpl 接口实现
* @date 2025-02-20
*/
@Service
public class MerchantElectServiceImpl extends ServiceImpl<MerchantElectDao, MerchantElect> implements MerchantElectService {

    @Resource
    private MerchantElectDao dao;


    /**
     * 创建电子面单主信息配置
     *
     * @param merchantElect
     * @return
     */
    @Override
    public Boolean saveMerchantElect(MerchantElect merchantElect) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        merchantElect.setMerId(admin.getMerId());
        return save(merchantElect);
    }

    /**
     * 更新电子面单主信息配置
     *
     * @param merchantElect
     * @return
     */
    @Override
    public Boolean updateMerchantElect(MerchantElect merchantElect) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        merchantElect.setMerId(admin.getMerId());
        // 这里需要根据商户id 唯一判断，新增前判断数据库有没有，有则更新，没有则新增
        LambdaQueryWrapper<MerchantElect> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantElect::getMerId, admin.getMerId());
        MerchantElect merchantElect1 = dao.selectOne(lambdaQueryWrapper);
        if(ObjectUtil.isNull(merchantElect1)){
            merchantElect.setMerId(admin.getMerId());
            return save(merchantElect);
        }
        if(merchantElect.getId() <= 0) throw new CrmebException("id 不能为空");
        return updateById(merchantElect);
    }

    /**
     * 删除电子面单主信息配置
     *
     * @param id
     * @return
     */
    @Override
    public Boolean deleteMerchantElect(Integer id) {
        if(ObjectUtil.isNull(id) || id < 0){
            throw new CrmebException("id 不能为空");
        }
        return removeById(id);
    }

    /**
     * 获取电子面单主信息配置
     *
     * @return
     */
    @Override
    public MerchantElect getMerchantElect(SystemAdmin admin) {
        LambdaQueryWrapper<MerchantElect> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MerchantElect::getMerId, admin.getMerId());
        return dao.selectOne(lambdaQueryWrapper);
    }
}

