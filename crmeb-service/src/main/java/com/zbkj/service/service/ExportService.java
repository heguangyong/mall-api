package com.zbkj.service.service;


import com.zbkj.common.request.OrderSearchRequest;

/**
* StoreProductService 接口
*  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
public interface ExportService {

    /**
     * 订单导出
     * @param request 查询条件
     * @return 文件名称
     */
    String exportOrder(OrderSearchRequest request);
}
