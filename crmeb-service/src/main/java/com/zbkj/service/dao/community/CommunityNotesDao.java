package com.zbkj.service.dao.community;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.dto.CommunityNotePageDateDto;
import com.zbkj.common.model.community.CommunityNotes;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 社区笔记表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2023-03-07
 */
public interface CommunityNotesDao extends BaseMapper<CommunityNotes> {

    /**
     * 社区笔记分页列表
     */
    List<CommunityNotePageDateDto> findPageList(Map<String, Object> map);
}
