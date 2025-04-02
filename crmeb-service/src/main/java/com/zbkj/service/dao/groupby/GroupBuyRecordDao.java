package com.zbkj.service.dao.groupby;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.groupbuy.GroupBuyRecord;
import com.zbkj.common.response.groupbuy.GroupBuyActivityRecordAdminListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 拼团活动记录表 Mapper 接口
 * </p>
 *
 * @author dazongzi
 * @since 2024-08-13
 */
public interface GroupBuyRecordDao extends BaseMapper<GroupBuyRecord> {

    List<GroupBuyActivityRecordAdminListResponse> findAdminList(HashMap<String, Object> map);

    Integer getAdminListHeaderCount(HashMap<String, Object> map);

    /**
     *  根据条件查询当前用户在当前活动中还可以参与的拼团活动
     * @param groupActivityId
     * @param userId
     * @param productId
     * @param limit
     * @return
     */
    List<GroupBuyRecord> getGroupRecordListForNotExist(
            @Param("groupActivityId") Integer groupActivityId,
            @Param("userId") Integer userId,
            @Param("productId") Integer productId,
            @Param("limit")int limit);
}
