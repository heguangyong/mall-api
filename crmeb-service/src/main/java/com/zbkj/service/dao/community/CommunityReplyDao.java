package com.zbkj.service.dao.community;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.community.CommunityReply;
import com.zbkj.common.response.CommunityReplyPageDateResponse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社区评论表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
public interface CommunityReplyDao extends BaseMapper<CommunityReply> {

    /**
     * 社区评论分页列表
     */
    List<CommunityReplyPageDateResponse> findPageList(Map<String, Object> map);
}
