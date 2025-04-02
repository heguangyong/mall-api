package com.zbkj.service.service;

import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.wechat.live.WechatLiveGoods;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsAddRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsEditRequest;
import com.zbkj.common.request.wxmplive.goods.WechatLiveGoodsSearchRequest;

import java.util.List;

/**
 * @Auther: 大粽子
 * @Date: 2023/3/21 15:41
 * @Description: 微信小程序直播 商品Service
 */
public interface WechatLiveGoodsService extends IService<WechatLiveGoods> {
    List<WechatLiveGoods> getList(WechatLiveGoodsSearchRequest request, PageParamRequest pageParamRequest, Boolean isMer);
    /**
     * 小程序直播添加并提审商品
     * @param id 待提审商品id
     * @param reviewStatus 审核状态 0=商户创建平台待审核，1=平台审核通过，2=平台审核失败,3=微信审核成功，4=微信审核失败
     * @param reviewReason 审核意见
     * @return 提审结果
     */
    Boolean platReview(Integer id, Integer reviewStatus, String reviewReason);

    /**
     * 根据微信审核id获取直播商品
     * @param auditId 微信审核id
     * @return 商品结果
     */
    WechatLiveGoods getByAuditId(Integer auditId);

    /**
     * 根据微信直播商品id 获取直播商品集合
     * @param goodsIds 直播商品id集合
     * @return 直播商品集合
     */
    List<WechatLiveGoods> getByGoodsId(List<Integer> goodsIds);

    /**
     * 商户重新提交商品审核
     * @param id 直播商品id
     * @return 提交结果
     */
    String merchantAuditGoods(Integer id);

    /**商户撤回商品审核
     * 审核状态 0=商户创建平台待审核，1=平台审核通过，2=平台审核失败,3=微信审核成功，4=微信审核失败
     * 撤回审核是微信提审状态下的操作，需要reviewStatus在大于2的前提下调用 或者是auditId有值的时候可以调用
     * @param id 商品id
     * @return 撤回结果
     */
    Boolean merchantResetAudit(Integer id);

    /**
     * 获取商品的信息与审核状态
     *
     */
    void getGoodsWareHouse();

    /**
     * 新增直播商品
     * @param request 新增商品对象
     * @return 创建结果
     */
    Boolean addGoods(List<WechatLiveGoodsAddRequest> request);

    /**
     * 更新商品
     * @param goods 待更新商品
     * @return 更新结果
     */
    Boolean updateGoods(WechatLiveGoodsEditRequest goods);

    /**
     * 更新排序状态
     * @param id 直播商品id
     * @param sort 直播商品排序
     * @return 执行排序结果
     */
    Boolean updateSort(Integer id, Integer sort);

    /**
     * 获取商品列表
     * @param pageParamRequest 分页参数 分页大小，默认30，不超过100
     * @param status 商品状态，0：未审核。1：审核中，2：审核通过，3：审核驳回
     * @return 分页查询结果
     */
    WxMaLiveResult getApprovedGoods(PageParamRequest pageParamRequest, Integer status);

    /**
     * 删除商品
     * @param id 待删除商品id
     * @return 删除结果
     */
    Boolean deleteGoods(Integer id);

    WechatLiveGoods getGoodInfo(Integer id);
}
