package com.zbkj.service.service;

import com.zbkj.common.model.order.RechargeOrder;
import com.zbkj.common.model.user.User;

import java.util.List;

/**
 * 异步调用服务
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
public interface AsyncService {

    /**
     * 商品详情统计
     * @param proId 商品id
     * @param uid 用户uid
     */
    void productDetailStatistics(Integer proId, Integer uid);

    /**
     * 保存用户访问记录
     * @param userId 用户id
     * @param visitType 访问类型
     */
    void saveUserVisit(Integer userId, Integer visitType);

    /**
     * 订单支付成功拆单处理
     * @param orderNo 订单号
     */
    void orderPaySuccessSplit(String orderNo);

    /**
     * 访问用户个人中心记录
     * @param uid 用户id
     */
    void visitUserCenter(Integer uid);

    /**
     * 安装统计
     */
    void installStatistics();

    /**
     * 发送充值成功通知
     * @param rechargeOrder 充值订单
     * @param user 用户
     */
    void sendRechargeSuccessNotification(RechargeOrder rechargeOrder, User user);

    /**
     * 社区笔记用户添加经验
     * @param userId 用户ID
     * @param noteId 文章ID
     */
    void noteUpExp(Integer userId, Integer noteId);

    /**
     * 社区笔记点赞与取消
     * @param noteId 笔记ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    void communityNoteLikeOrClean(Integer noteId, String operationType);

    /**
     * 社区笔记评论点赞与取消
     * @param replyId 评论ID
     * @param operationType 操作类型：add-点赞，sub-取消
     */
    void communityReplyLikeOrClean(Integer replyId, String operationType);

    /**
     * 社区笔记添加评论后置处理
     * @param noteId 笔记ID
     * @param parentId 一级评论ID，0-没有
     * @param replyId 评论ID
     */
    void noteAddReplyAfter(Integer noteId, Integer parentId, Integer replyId);

    /**
     * 社区评论删除后置处理
     * @param noteId 笔记ID
     * @param firstReplyId 一级笔记评论ID
     * @param replyId 评论ID
     * @param countReply 评论回复数量
     */
    void communityReplyDeleteAfter(Integer noteId, Integer firstReplyId, Integer replyId, Integer countReply);

    /**
     * 用户升级
     * @param userId 用户ID
     * @param userLevel 用户等级
     * @param exp 当前经验
     */
    void userLevelUp(Integer userId, Integer userLevel, Integer exp);

    /**
     * 订单支付成功后冻结处理
     * @param orderNoList 订单编号列表
     */
    void orderPayAfterFreezingOperation(List<String> orderNoList);

    /**
     * 订单完成后冻结处理
     * @param orderNoList 订单编号列表
     */
    void orderCompleteAfterFreezingOperation(List<String> orderNoList);

    /**
     * 微信小程序发货上传发货管理
     * @param orderNo 订单编号
     */
    void wechatSendUploadShipping(String orderNo);

    /**
     * 核销订单微信小程序发货上传发货管理
     * @param orderNo 订单编号
     */
    void verifyOrderWechatSendUploadShipping(String orderNo);
}
