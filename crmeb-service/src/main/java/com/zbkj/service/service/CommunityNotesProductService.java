package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.community.CommunityNotesProduct;

import java.util.List;

/**
* CommunityNotesProduct 接口
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
public interface CommunityNotesProductService extends IService<CommunityNotesProduct> {

    /**
     * 获取笔记关联商品
     * @param noteId 笔记ID
     */
    List<CommunityNotesProduct> findListByNoteId(Integer noteId);

    /**
     * 通过笔记ID删除关联商品
     * @param noteId 笔记ID
     */
    void deleteByNoteId(Integer noteId);
}