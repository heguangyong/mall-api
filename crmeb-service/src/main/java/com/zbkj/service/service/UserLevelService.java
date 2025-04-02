package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserLevel;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
 * UserLevelService 接口实现
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
public interface UserLevelService extends IService<UserLevel> {

    /**
     * 删除（通过系统等级id）
     * @param levelId 系统等级id
     * @return Boolean
     */
    Boolean deleteByLevelId(Integer levelId);

    /**
     * 删除用户等级
     * @param userId 用户ID
     */
    Boolean deleteByUserId(Integer userId);
}
