package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.closing.MerchantClosingRecord;
import com.zbkj.common.response.MerchantClosingPlatformPageResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商家结算记录表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface MerchantClosingRecordDao extends BaseMapper<MerchantClosingRecord> {

    /**
     * 商户结算分页列表(平台)
     */
    List<MerchantClosingPlatformPageResponse> getMerchantClosingPageListByPlatform(Map<String, Object> map);
}
