package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.OnePassApplicationInfoVo;
import com.zbkj.common.vo.OnePassLogisticsQueryVo;
import com.zbkj.common.vo.OnePassUserInfoVo;

/**
 * 一号通 OnePassService 接口
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
public interface OnePassService {

    /**
     * 一号通用户信息
     * @return OnePassUserInfoVo
     */
    OnePassUserInfoVo info();

    /**
     * 复制平台商品
     * @param url 商品链接
     */
    JSONObject copyGoods(String url);

    /**
     * 电子面单
     */
    MyRecord expressDump(MyRecord record);

    /**
     * 物流追踪
     * @param expressNo 快递单号
     * @param com   快递公司简写
     * @param phone  手机号,顺丰使用
     * @return OnePassLogisticsQueryVo
     */
    OnePassLogisticsQueryVo exprQuery(String expressNo, String com, String phone);

    /**
     * 保存一号通应用信息
     * @param request 一号通服务中申请的应用信息
     * @return 保存结果
     */
    Boolean saveApplicationInfo(OnePassApplicationInfoVo request);

    /**
     * 获取一号通应用信息
     * @return 一号通应用信息
     */
    OnePassApplicationInfoVo getApplicationInfo();

    /**
     * 获取一号通应用信息会抛出异常
     * @return 一号通应用信息
     */
    OnePassApplicationInfoVo getApplicationInfoException();
}
