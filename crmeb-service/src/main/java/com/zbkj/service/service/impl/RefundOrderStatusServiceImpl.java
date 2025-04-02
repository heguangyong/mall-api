package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.constants.RefundOrderConstants;
import com.zbkj.common.model.order.RefundOrderStatus;
import com.zbkj.service.dao.RefundOrderStatusDao;
import com.zbkj.service.service.RefundOrderStatusService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
*  RefundOrderStatusServiceImpl 接口实现
*  +----------------------------------------------------------------------
*  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
*  +----------------------------------------------------------------------
*  | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
*  +----------------------------------------------------------------------
*  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
*  +----------------------------------------------------------------------
*  | Author: CRMEB Team <admin@crmeb.com>
*  +----------------------------------------------------------------------
*/
@Service
public class RefundOrderStatusServiceImpl extends ServiceImpl<RefundOrderStatusDao, RefundOrderStatus> implements RefundOrderStatusService {

    @Resource
    private RefundOrderStatusDao dao;

    /**
     * 添加退款单日志记录
     * @param refundOrderNo 退款单号
     * @param changeType 操作类型
     */
    @Override
    public void add(String refundOrderNo, String changeType, String message) {
        RefundOrderStatus refundOrderStatus = new RefundOrderStatus();
        refundOrderStatus.setOrderNo(refundOrderNo);
        refundOrderStatus.setChangeType(changeType);
        refundOrderStatus.setChangeMessage(message);
        refundOrderStatus.setCreateTime(DateUtil.date());
        save(refundOrderStatus);
    }

    /**
     * 获取退款单记录列表
     * @param refundOrderNo 退款单号
     */
    @Override
    public List<RefundOrderStatus> findListByRefundOrderNo(String refundOrderNo) {
        LambdaQueryWrapper<RefundOrderStatus> lqw = Wrappers.lambdaQuery();
        lqw.eq(RefundOrderStatus::getOrderNo, refundOrderNo);
        return dao.selectList(lqw);
    }
}

