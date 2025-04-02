package com.zbkj.admin.controller.publicly;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存控制器
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
@Slf4j
@RestController
@RequestMapping("api/publicly/cache")
@Api(tags = "缓存控制器")
public class CacheController {

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "通过key删除缓存")
    @RequestMapping(value = "/delete/{key}", method = RequestMethod.GET)
    public CommonResult<String> deleteByKey(@PathVariable(name = "key") String key) {
        if (StrUtil.isBlank(key)) {
            throw new CrmebException("缓存key不能为空");
        }
        if (!redisUtil.exists(key)) {
            return CommonResult.success("删除缓存成功");
        }
        redisUtil.hmDelete(key);
        return CommonResult.success("删除缓存成功");
    }
}
