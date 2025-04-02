package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.MerchantElect;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantElectSearchRequest;

import java.util.List;

/**
* @author dazongzi
* @description MerchantElectService 接口
* @date 2025-02-20
*/
public interface MerchantElectService extends IService<MerchantElect> {


    /**
     * 创建电子面单主信息配置
     * @param merchantElect
     * @return
     */
    Boolean saveMerchantElect(MerchantElect merchantElect);

    /**
     * 更新电子面单主信息配置
     * @param merchantElect
     * @return
     */
    Boolean updateMerchantElect(MerchantElect merchantElect);

    /**
     * 删除电子面单主信息配置
     * @param id
     * @return
     */
    Boolean deleteMerchantElect(Integer id);

    /**
     * 获取电子面单主信息配置
     * @return
     */
    MerchantElect getMerchantElect(SystemAdmin admin);
}